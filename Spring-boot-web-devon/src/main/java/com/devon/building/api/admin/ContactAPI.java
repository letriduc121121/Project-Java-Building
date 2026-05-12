package com.devon.building.api.admin;

import com.devon.building.enums.Status;
import com.devon.building.model.dto.ContactDTO;
import com.devon.building.model.dto.CustomerDTO;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactAPI {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<?> receiveContact(@RequestBody @Valid ContactDTO contactDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMsg);
        }

        if (customerRepository.existsByPhone(contactDTO.getCustomerPhone())) {
            return ResponseEntity.badRequest().body("Số điện thoại này đã được đăng ký liên hệ trước đó.");
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setFullName(contactDTO.getFullName());
        dto.setEmail(contactDTO.getEmail());
        dto.setPhone(contactDTO.getCustomerPhone());
        dto.setDemand(contactDTO.getDemand());
        dto.setStatus(Status.CHUA_XU_LY.name());

        customerService.create(dto);

        return ResponseEntity.ok("Gửi liên hệ thành công! Chúng tôi sẽ phản hồi sớm nhất.");
    }
}
