package com.devon.building.repository;

import com.devon.building.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);

    boolean existsByUserName(String userName);

    User findByEmail(String email);

    java.util.Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    void deleteByIdIn(List<Long> ids);

    List<User> findByActiveAndUserRole(boolean active, String role);
}
