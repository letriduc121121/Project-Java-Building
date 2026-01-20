package com.devon.building.repository;

import com.devon.building.entity.AssignmentBuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentBuildingRepository extends JpaRepository<AssignmentBuildingEntity, Long> {

    // Tìm tất cả assignment của 1 tòa nhà
    List<AssignmentBuildingEntity> findByBuildingId(Long buildingId);

    // Xóa tất cả assignment của 1 tòa nhà
    void deleteByBuildingId(Long buildingId);

    // Xóa tất cả assignment của nhiều tòa nhà
    void deleteAllByBuildingIdIn(List<Long> buildingIds);
}
