package com.devon.building.service.impl;

import com.devon.building.entity.CustomerEntity;
import com.devon.building.entity.TransactionEntity;
import com.devon.building.model.dto.TransactionDTO;
import com.devon.building.repository.CustomerRepository;
import com.devon.building.repository.TransactionRepository;
import com.devon.building.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public TransactionEntity createTransaction(TransactionDTO dto) {
        CustomerEntity customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        TransactionEntity entity = new TransactionEntity();
        entity.setCode(dto.getCode());
        entity.setNote(dto.getNote());
        entity.setCustomer(customer);
        return transactionRepository.save(entity);
    }

    @Override
    @Transactional
    public TransactionEntity updateTransaction(TransactionDTO dto) {
        TransactionEntity entity = transactionRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));
        entity.setNote(dto.getNote());
        entity.setCode(dto.getCode());
        return transactionRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteTransactions(List<Long> ids) {
        transactionRepository.deleteAllById(ids);
    }
}
