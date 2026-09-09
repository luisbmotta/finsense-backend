package com.finsense.backend.transaction;

import com.finsense.backend.ai.GroqService;
import com.finsense.backend.ai.dto.TransacaoExtraida;
import com.finsense.backend.security.UserPrincipal;
import com.finsense.backend.transaction.dto.CreateTransactionRequest;
import com.finsense.backend.transaction.dto.ParseTransactionRequest;
import com.finsense.backend.transaction.dto.ParsedTransactionResponse;
import com.finsense.backend.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final GroqService groqService;

    public TransactionController(TransactionService transactionService, GroqService groqService) {
        this.transactionService = transactionService;
        this.groqService = groqService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(transactionService.listForUser(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        TransactionResponse response = transactionService.create(principal.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/parse")
    public ResponseEntity<ParsedTransactionResponse> parse(@Valid @RequestBody ParseTransactionRequest request) {
        TransacaoExtraida extraida = groqService.parseTransacaoTexto(request.text());
        return ResponseEntity.ok(ParsedTransactionResponse.from(extraida));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id
    ) {
        transactionService.delete(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
