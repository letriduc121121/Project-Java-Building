package com.devon.building.api.admin;

import com.devon.building.model.dto.AssignCustomerDTO;
import com.devon.building.model.dto.CustomerDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/customers")
public class CustomerAPI {

    private static final String SERVER_ERROR_PREFIX = "Server Error: ";

    private final CustomerService customerService;

    public CustomerAPI(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addCustomer(@RequestBody @Valid CustomerDTO dto, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Failed to add Customer");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            customerService.create(dto);
            responseDTO.setMessage("Thêm khách hàng thành công");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> updateCustomer(@RequestBody @Valid CustomerDTO dto, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Failed to update Customer");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        if (dto.getId() == null) {
            responseDTO.setMessage("Customer ID is required for update");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            customerService.update(dto);
            responseDTO.setMessage("Cập nhật khách hàng thành công");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> deleteCustomers(@RequestBody List<Long> ids) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            customerService.delete(ids);
            responseDTO.setMessage("Xóa khách hàng thành công");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @GetMapping("/{id}/staffs")
    public ResponseEntity<ResponseDTO> getStaffs(@PathVariable Long id) {
        try {
            ResponseDTO responseDTO = customerService.loadStaffByCustomerId(id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMessage("Error loading staffs: " + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PutMapping("/assign")
    public ResponseEntity<ResponseDTO> assignCustomer(@RequestBody @Valid AssignCustomerDTO dto, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Validation failed");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            customerService.assignCustomer(dto);
            responseDTO.setMessage("Giao nhân viên thành công");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
}
