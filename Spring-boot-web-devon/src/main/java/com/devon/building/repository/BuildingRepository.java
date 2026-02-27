package com.devon.building.repository;

import com.devon.building.entity.BuildingEntity;
import com.devon.building.repository.custom.BuildingRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<BuildingEntity, Long>, BuildingRepositoryCustom {
    void deleteByIdIn(List<Long> ids);

}
