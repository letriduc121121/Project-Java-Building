package com.devon.building.repository;

import com.devon.building.entity.RentAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentAreaRepository extends JpaRepository<RentAreaEntity, Long> {
    void deleteAllByBuilding_IdIn(List<Long> buildingIds);
}
