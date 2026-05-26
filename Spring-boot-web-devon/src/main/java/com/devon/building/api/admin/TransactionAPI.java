package com.devon.building.api.admin;

import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.dto.TransactionDTO;
import com.devon.building.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/transactions")
public class TransactionAPI {

    private static final String SERVER_ERROR_PREFIX = "Server Error: ";

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ResponseDTO> createTransaction(@RequestBody @Valid TransactionDTO transactionDTO, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Dữ liệu giao dịch không hợp lệ");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            transactionService.createTransaction(transactionDTO);
            responseDTO.setMessage("Thêm giao dịch thành công");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> updateTransaction(@RequestBody @Valid TransactionDTO transactionDTO, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Dữ liệu giao dịch không hợp lệ");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        if (transactionDTO.getId() == null) {
            responseDTO.setMessage("Transaction ID is required for update");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            transactionService.updateTransaction(transactionDTO);
            responseDTO.setMessage("Cập nhật giao dịch thành công");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<ResponseDTO> deleteTransaction(@PathVariable List<Long> ids) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            transactionService.deleteTransactions(ids);
            responseDTO.setMessage("Xóa giao dịch thành công");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
}
