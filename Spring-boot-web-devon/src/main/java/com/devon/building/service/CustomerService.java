package com.devon.building.service;

import com.devon.building.entity.CustomerEntity;
import com.devon.building.model.dto.AssignCustomerDTO;
import com.devon.building.model.dto.CustomerDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.model.response.CustomerSearchResponse;
import com.devon.building.pagination.PaginationResult;

import java.util.List;

public interface CustomerService {
    List<CustomerEntity> findAll();
    PaginationResult<CustomerSearchResponse> searchCustomers(CustomerSearchRequest request, int page, int maxResult, int maxNavigationPage);
    CustomerDTO findById(Long id);
    CustomerEntity create(CustomerDTO dto);
    CustomerEntity update(CustomerDTO dto);
    void delete(List<Long> ids);
    ResponseDTO loadStaffByCustomerId(Long id);
    void assignCustomer(AssignCustomerDTO dto);
}
