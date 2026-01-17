package com.devon.building.repository;

import com.devon.building.entity.User;
import com.devon.building.model.dto.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User findAccount(String userName) {
        return entityManager.find(User.class, userName);
    }
}
