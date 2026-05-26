package com.devon.building.service.impl;

import com.devon.building.constant.SystemConstant;
import com.devon.building.convert.CustomerConvertor;
import com.devon.building.entity.CustomerEntity;
import com.devon.building.entity.User;
import com.devon.building.model.dto.AssignCustomerDTO;
import com.devon.building.model.dto.CustomerDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.dto.StaffResponseDTO;
import com.devon.building.model.request.CustomerSearchRequest;
import com.devon.building.model.response.CustomerSearchResponse;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.service.CustomerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerConvertor customerConvertor;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CustomerEntity> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public PaginationResult<CustomerSearchResponse> searchCustomers(CustomerSearchRequest request, int page,
            int maxResult, int maxNavigationPage) {

        String baseQuery = " FROM " + CustomerEntity.class.getName() + " c WHERE c.isActive = 1 ";

        StringBuilder sql = new StringBuilder(
                "SELECT NEW " + CustomerSearchResponse.class.getName() +
                        "(c.id, c.fullName, c.phone, c.email, c.demand, c.status, c.createdBy, c.createdDate) ")
                .append(baseQuery);

        StringBuilder countSql = new StringBuilder("SELECT COUNT(c.id)").append(baseQuery);

        // --- Filter conditions ---
        if (isNotEmpty(request.getName())) {
            sql.append(" AND LOWER(c.fullName) LIKE :name");
            countSql.append(" AND LOWER(c.fullName) LIKE :name");
        }
        if (isNotEmpty(request.getPhone())) {
            sql.append(" AND LOWER(c.phone) LIKE :phone");
            countSql.append(" AND LOWER(c.phone) LIKE :phone");
        }
        if (isNotEmpty(request.getEmail())) {
            sql.append(" AND LOWER(c.email) LIKE :email");
            countSql.append(" AND LOWER(c.email) LIKE :email");
        }
        // if (isNotEmpty(request.getDemand())) {
        // sql.append(" AND LOWER(c.demand) LIKE :demand");
        // countSql.append(" AND LOWER(c.demand) LIKE :demand");
        // }
        if (isNotEmpty(request.getStatus())) {
            sql.append(" AND c.status = :status");
            countSql.append(" AND c.status = :status");
        }
        if (request.getStaffId() != null) {
            sql.append(" AND EXISTS (SELECT s FROM c.staffs s WHERE s.id = :staffId)");
            countSql.append(" AND EXISTS (SELECT s FROM c.staffs s WHERE s.id = :staffId)");
        }

        sql.append(" ORDER BY c.createdDate DESC");

        TypedQuery<CustomerSearchResponse> query = entityManager.createQuery(sql.toString(),
                CustomerSearchResponse.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        // --- Bind parameters ---
        if (isNotEmpty(request.getName())) {
            query.setParameter("name", "%" + request.getName().toLowerCase() + "%");
            countQuery.setParameter("name", "%" + request.getName().toLowerCase() + "%");
        }
        if (isNotEmpty(request.getPhone())) {
            query.setParameter("phone", "%" + request.getPhone().toLowerCase() + "%");
            countQuery.setParameter("phone", "%" + request.getPhone().toLowerCase() + "%");
        }
        if (isNotEmpty(request.getEmail())) {
            query.setParameter("email", "%" + request.getEmail().toLowerCase() + "%");
            countQuery.setParameter("email", "%" + request.getEmail().toLowerCase() + "%");
        }
        if (isNotEmpty(request.getDemand())) {
            query.setParameter("demand", "%" + request.getDemand().toLowerCase() + "%");
            countQuery.setParameter("demand", "%" + request.getDemand().toLowerCase() + "%");
        }
        if (isNotEmpty(request.getStatus())) {
            query.setParameter("status", request.getStatus());
            countQuery.setParameter("status", request.getStatus());
        }
        if (request.getStaffId() != null) {
            query.setParameter("staffId", request.getStaffId());
            countQuery.setParameter("staffId", request.getStaffId());
        }

        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }

    @Override
    @Transactional
    public CustomerEntity create(CustomerDTO dto) {
        CustomerEntity entity = customerConvertor.toCustomerEntity(dto);
        return customerRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public CustomerEntity update(CustomerDTO dto) {
        CustomerEntity entity = customerConvertor.toCustomerEntity(dto);
        return customerRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //loop tung ID và set active = 0
        ids.forEach(id -> customerRepository.findById(id)
                .ifPresent(customer -> customer.setIsActive(0)));
        customerRepository.flush();
    }

    @Override
    public ResponseDTO loadStaffByCustomerId(Long id) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<User> allStaff = userRepository.findByActiveAndUserRole(true, "ROLE_" + User.ROLE_STAFF);

        Set<Long> assignedIds = customer.getStaffs().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        List<StaffResponseDTO> result = new ArrayList<>();
        for (User user : allStaff) {
            StaffResponseDTO dto = new StaffResponseDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUserName());
            dto.setChecked(assignedIds.contains(user.getId()) ? "checked" : "");
            result.add(dto);
        }

        ResponseDTO response = new ResponseDTO();
        response.setData(result);
        response.setMessage("Load staffs successfully");
        return response;
    }

    @Override
    @Transactional
    public void assignCustomer(AssignCustomerDTO dto) {
        CustomerEntity customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<User> newStaffs = new ArrayList<>();
        if (dto.getStaffIds() != null && !dto.getStaffIds().isEmpty()) {
            newStaffs = userRepository.findAllById(dto.getStaffIds());
        }
        customer.setStaffs(newStaffs);
        customerRepository.save(customer);
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    @Override
    @Transactional
    public CustomerDTO findById(Long id) {
        CustomerEntity entity = customerRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        com.devon.building.model.dto.CustomerDTO dto = new com.devon.building.model.dto.CustomerDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setCompanyName(entity.getCompanyName());
        dto.setDemand(entity.getDemand());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
