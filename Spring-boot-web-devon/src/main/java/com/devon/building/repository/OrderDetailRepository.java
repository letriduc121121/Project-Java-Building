package com.devon.building.repository;

import com.devon.building.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    
    // Xóa tất cả order detail của building
    void deleteAllByBuilding_IdIn(List<Long> buildingIds);
}
