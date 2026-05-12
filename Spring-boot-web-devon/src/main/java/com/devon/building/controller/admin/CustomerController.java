package com.devon.building.controller.admin;

import com.devon.building.constant.SystemConstant;
import com.devon.building.entity.TransactionEntity;
import com.devon.building.entity.User;
import com.devon.building.enums.Status;
import com.devon.building.model.dto.CustomerDTO;
import com.devon.building.model.dto.TransactionDTO;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.model.response.CustomerSearchResponse;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.TransactionRepository;
import com.devon.building.service.CustomerService;
import com.devon.building.service.UserService;
import com.devon.building.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/list")
    public String customerList(
            @RequestParam(value = "page", defaultValue = "1") String pageStr,
            @ModelAttribute CustomerSearchRequest customerSearchRequest,
            Model model) {

        int page = 1;
        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (SecurityUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
            User user = userService.getUserByUserName(SecurityUtils.getCurrentUser().getUsername());
            customerSearchRequest.setStaffId(user.getId());
        }

        PaginationResult<CustomerSearchResponse> result = customerService.searchCustomers(
                customerSearchRequest, page,
                SystemConstant.MAX_RESULT,
                SystemConstant.MAX_NAVIGATION_PAGE);
        model.addAttribute("modelSearch", customerSearchRequest);
        model.addAttribute("resultCustomer", result);
        model.addAttribute("staffs", userService.getAllStaff());
        model.addAttribute("statuses", Status.getStatusMap());
        return "admin/customer/customerList";
    }

    @GetMapping("/edit")
    public String customerAdd(Model model) {
        model.addAttribute("customerEdit", new CustomerDTO());
        model.addAttribute("statuses", Status.getStatusMap());
        model.addAttribute("transactionType", buildTransactionTypeMap());
        return "admin/customer/customerEdit";
    }

    @GetMapping("/edit/{id}")
    public String customerEdit(@PathVariable Long id, Model model) {
        if (SecurityUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
            User user = userService.getUserByUserName(SecurityUtils.getCurrentUser().getUsername());
            boolean isAssigned = customerRepository.existsByIdAndStaffs_Id(id, user.getId());
            if (!isAssigned) {
                return "/404";
            }
        }

        CustomerDTO customer = customerService.findById(id);
        if (customer == null) {
            return "redirect:/admin/customers/list";
        }
        model.addAttribute("customerEdit", customer);
        model.addAttribute("statuses", Status.getStatusMap());
        model.addAttribute("transactionType", buildTransactionTypeMap());

        List<TransactionDTO> cskh = transactionRepository.findByCustomerIdAndCode(id, "CSKH")
                .stream().map(this::toDTO).collect(Collectors.toList());
        List<TransactionDTO> ddx = transactionRepository.findByCustomerIdAndCode(id, "DDX")
                .stream().map(this::toDTO).collect(Collectors.toList());

        model.addAttribute("CSKH", cskh);
        model.addAttribute("DDX", ddx);
        return "admin/customer/customerEdit";
    }

    private Map<String, String> buildTransactionTypeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("CSKH", "Chăm sóc khách hàng");
        map.put("DDX", "Đặt dịch vụ");
        return map;
    }

    private TransactionDTO toDTO(TransactionEntity e) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setNote(e.getNote());
        dto.setCreatedDate(e.getCreatedDate());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setModifiedDate(e.getModifiedDate());
        dto.setModifiedBy(e.getModifiedBy());
        return dto;
    }
}
