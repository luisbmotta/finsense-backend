package com.finsense.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finsense.backend.ai.dto.TransacaoExtraida;
import com.finsense.backend.transaction.Category;
import com.finsense.backend.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Integra com a API da Groq (endpoint compativel com OpenAI) para tarefas de IA do app:
 * extrair transacoes de texto livre e gerar insights financeiros curtos.
 */
@Service
public class GroqService {

    private static final Logger log = LoggerFactory.getLogger(GroqService.class);

    private static final String GROQ_API_BASE_URL = "https://api.groq.com/openai/v1";
    private static final String MODEL = "openai/gpt-oss-20b";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public GroqService(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${groq.api-key:}") String apiKey
    ) {
        this.restClient = restClientBuilder.baseUrl(GROQ_API_BASE_URL).build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public TransacaoExtraida parseTransacaoTexto(String texto) {
        requireApiKey();

        String categoriasDisponiveis = Arrays.stream(Category.values())
                .map(Category::getValue)
                .collect(Collectors.joining(", "));

        String systemPrompt = """
                Voce extrai dados estruturados de uma frase em portugues sobre um gasto financeiro.
                Responda APENAS com um JSON puro, sem markdown e sem texto adicional, no formato exato:
                {"valor": <numero>, "categoria": "<uma das categorias>", "descricao": "<texto curto>"}
                O campo "valor" deve ser um numero positivo (use ponto como separador decimal).
                O campo "categoria" deve ser exatamente uma destas opcoes: %s.
                O campo "descricao" deve ser um texto curto resumindo o gasto.
                """.formatted(categoriasDisponiveis);

        String content = chatCompletion(systemPrompt, texto);

        try {
            JsonNode json = objectMapper.readTree(content);
            BigDecimal valor = new BigDecimal(json.get("valor").asText());
            Category categoria = Category.fromValue(json.get("categoria").asText());
            String descricao = json.get("descricao").asText();
            return new TransacaoExtraida(valor, categoria, descricao);
        } catch (Exception e) {
            log.error("Nao foi possivel interpretar a resposta da IA para a transacao. Conteudo recebido: {}", content, e);
            throw new GroqServiceException("Nao foi possivel interpretar a resposta da IA para a transacao", e);
        }
    }

    public List<String> gerarInsights(List<Transaction> transacoes, BigDecimal rendaMensal) {
        requireApiKey();

        String resumo = montarResumoTransacoes(transacoes, rendaMensal);

        String systemPrompt = """
                Voce e um assistente financeiro que gera insights curtos e praticos em portugues do Brasil,
                voltados a jovens de 18 a 30 anos, a partir de um resumo de transacoes e da renda mensal do usuario.
                Gere de 2 a 4 insights curtos (uma frase cada), incluindo pelo menos:
                - uma dica de investimento ou organizacao financeira adequada ao contexto informado;
                - um alerta caso o usuario esteja gastando muito, proporcionalmente, em alguma categoria.
                Responda APENAS com um JSON puro, sem markdown e sem texto adicional, no formato exato:
                {"insights": ["<insight 1>", "<insight 2>"]}
                """;

        String content = chatCompletion(systemPrompt, resumo);

        try {
            JsonNode insightsNode = objectMapper.readTree(content).get("insights");
            List<String> insights = new ArrayList<>();
            for (JsonNode insight : insightsNode) {
                insights.add(insight.asText());
            }
            return insights;
        } catch (Exception e) {
            log.error("Nao foi possivel interpretar os insights gerados pela IA. Conteudo recebido: {}", content, e);
            throw new GroqServiceException("Nao foi possivel interpretar os insights gerados pela IA", e);
        }
    }

    private String montarResumoTransacoes(List<Transaction> transacoes, BigDecimal rendaMensal) {
        Map<Category, BigDecimal> gastosPorCategoria = transacoes.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        BigDecimal totalGasto = gastosPorCategoria.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder resumo = new StringBuilder();
        resumo.append("Renda mensal: R$ ").append(rendaMensal).append('\n');
        resumo.append("Total gasto no periodo: R$ ").append(totalGasto).append('\n');
        resumo.append("Quantidade de transacoes: ").append(transacoes.size()).append('\n');
        resumo.append("Gastos por categoria:\n");
        gastosPorCategoria.forEach((categoria, valor) ->
                resumo.append("- ").append(categoria.getValue()).append(": R$ ").append(valor).append('\n'));

        return resumo.toString();
    }

    private String chatCompletion(String systemPrompt, String userPrompt) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("temperature", 0.2);
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));

        GroqChatResponse response;
        try {
            response = restClient.post()
                    .uri("/chat/completions")
                    // A chave nunca deve ser logada: nao logar headers/requisicao neste client.
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(GroqChatResponse.class);
        } catch (RestClientException e) {
            log.error("Falha ao chamar a API da Groq", e);
            throw new GroqServiceException("Falha ao chamar a API da Groq", e);
        }

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new GroqServiceException("Resposta vazia da API da Groq");
        }

        return response.choices().get(0).message().content();
    }

    private void requireApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("GROQ_API_KEY nao configurada");
            throw new GroqServiceException(
                    "GROQ_API_KEY nao configurada. Defina a variavel de ambiente para usar os recursos de IA.");
        }
    }

    private record GroqChatResponse(List<GroqChoice> choices) {
    }

    private record GroqChoice(GroqMessage message) {
    }

    private record GroqMessage(String content) {
    }
}
