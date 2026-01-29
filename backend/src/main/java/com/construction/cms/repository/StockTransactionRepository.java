package com.construction.cms.repository;

import com.construction.cms.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByMaterialIdOrderByTransactionDateDesc(Long materialId);
    List<StockTransaction> findByProjectId(Long projectId);
}
