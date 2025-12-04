package com.example.demo.repository;

import com.example.demo.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Account findAccount(String userName) {
        return entityManager.find(Account.class, userName);
    }
}
