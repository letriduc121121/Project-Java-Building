package com.devon.building.api.admin;

import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.dto.TransactionDTO;
import com.devon.building.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/transactions")
public class TransactionAPI {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        transactionService.createTransaction(transactionDTO);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Thêm giao dịch thành công");
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<?> deleteTransaction(@PathVariable List<Long> ids) {
        ResponseDTO responseDTO = new ResponseDTO();
        transactionService.deleteTransactions(ids);
        responseDTO.setMessage("Xóa giao dịch thành công");
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping
    public ResponseEntity<?> updateTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        transactionService.updateTransaction(transactionDTO);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Cập nhật giao dịch thành công");
        return ResponseEntity.ok(responseDTO);
    }
}
