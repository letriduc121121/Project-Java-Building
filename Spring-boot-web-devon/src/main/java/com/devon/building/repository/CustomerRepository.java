package com.devon.building.repository;

import com.devon.building.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    List<CustomerEntity> findByIsActive(Integer isActive);
    boolean existsByPhone(String phone);
    boolean existsByIdAndStaffs_Id(Long id, Long staffId);
}
