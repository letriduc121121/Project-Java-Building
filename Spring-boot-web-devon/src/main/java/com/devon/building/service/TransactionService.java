package com.devon.building.service;

import com.devon.building.entity.TransactionEntity;
import com.devon.building.model.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {
    TransactionEntity createTransaction(TransactionDTO dto);

    TransactionEntity updateTransaction(TransactionDTO dto);

    void deleteTransactions(List<Long> ids);
}
