package com.devon.building.service.impl;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.convert.BuildingConvertor;
import com.devon.building.convert.BuildingSearchBuilderConvertor;
import com.devon.building.entity.BuildingEntity;
import com.devon.building.entity.RentAreaEntity;
import com.devon.building.entity.User;
import com.devon.building.exception.InvalidBuildingException;
import com.devon.building.model.dto.AssignBuildingDTO;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.dto.StaffResponseDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.repository.*;
import com.devon.building.service.BuildingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final RentAreaRepository rentAreaRepository;
    private final BuildingConvertor buildingConvertor;
    private final BuildingSearchBuilderConvertor buildingSearchBuilderConvertor;

    @PersistenceContext
    private EntityManager entityManager;

    public BuildingServiceImpl(
            ModelMapper modelMapper,
            UserRepository userRepository,
            BuildingRepository buildingRepository,
            RentAreaRepository rentAreaRepository,
            BuildingConvertor buildingConvertor,
            BuildingSearchBuilderConvertor buildingSearchBuilderConvertor) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.rentAreaRepository = rentAreaRepository;
        this.buildingConvertor = buildingConvertor;
        this.buildingSearchBuilderConvertor = buildingSearchBuilderConvertor;
    }

    @Override
    public List<BuildingSearchResponse> searchBuildings(BuildingSearchRequest request) {

        BuildingSearchBuilder buildingSearchBuilder = buildingSearchBuilderConvertor.toBuildingSearchBuilder(request);

        List<BuildingEntity> buildingEntities = buildingRepository.searchBuildings(buildingSearchBuilder);

        List<BuildingSearchResponse> buildingResponseDTOs = new ArrayList<>();

        for (BuildingEntity entity : buildingEntities) {
            BuildingSearchResponse buildingResponseDTO = buildingConvertor.convertToBuildingResponseDTO(entity);
            buildingResponseDTOs.add(buildingResponseDTO);
        }

        return buildingResponseDTOs;
    }

    @Override
    @Transactional
    public BuildingEntity create(BuildingDTO buildingDTO) {
        // chuyen dto -> entity
        BuildingEntity buildingEntity = buildingConvertor.toBuildingEntity(buildingDTO);

        // luu building vào DB (trở thành Managed)
        entityManager.persist(buildingEntity);

        // Xử lý RentArea: Add trực tiếp vào list của Entity
        // CascadeType.PERSIST sẽ tự động lưu RentArea khi transaction commit
        if (buildingDTO.getRentArea() != null && !buildingDTO.getRentArea().isEmpty()) {
            String[] rentAreas = buildingDTO.getRentArea().split(",");
            for (String val : rentAreas) {
                if (!val.trim().isEmpty()) {
                    RentAreaEntity rentAreaEntity = new RentAreaEntity();
                    rentAreaEntity.setBuilding(buildingEntity);
                    rentAreaEntity.setValue(Long.parseLong(val.trim()));
                    buildingEntity.getRentAreaEntities().add(rentAreaEntity);
                }
            }
        }

        return buildingEntity;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        // Xóa rent area (orphanRemoval sẽ tự động xử lý)
        rentAreaRepository.deleteAllByBuilding_IdIn(ids);
        // Cuối cùng xóa building - JPA tự động xóa assignmentbuilding
        buildingRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional
    public BuildingEntity update(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = buildingConvertor.toBuildingEntity(buildingDTO);
        // Merge để lấy entity đang được quản lý bởi JPA
        BuildingEntity storedBuilding = entityManager.merge(buildingEntity);
        // Xóa RentArea
        // Cần clear list thay vì delete thủ công. orphanRemoval = true sẽ tự động delete trong DB.
        storedBuilding.getRentAreaEntities().clear();
        // Thêm RentArea
        if (buildingDTO.getRentArea() != null && !buildingDTO.getRentArea().isEmpty()) {
            String[] rentAreas = buildingDTO.getRentArea().split(",");
            for (String val : rentAreas) {
                if (!val.trim().isEmpty()) {
                    RentAreaEntity rentAreaEntity = new RentAreaEntity();
                    rentAreaEntity.setBuilding(storedBuilding);
                    rentAreaEntity.setValue(Long.parseLong(val.trim()));
                    storedBuilding.getRentAreaEntities().add(rentAreaEntity);
                }
            }
        }
        return storedBuilding;
    }


    @Override
    public BuildingDTO findById(Long id) {
        BuildingEntity buildingEntity = buildingRepository.findById(id).orElseThrow(() -> new InvalidBuildingException("Building not found"));
        BuildingDTO dto = modelMapper.map(buildingEntity, BuildingDTO.class);
        if (buildingEntity.getType() != null && !buildingEntity.getType().isEmpty()) {
            dto.setTypeCode(Arrays.asList(buildingEntity.getType().split(",")));
        }
        if (buildingEntity.getRentAreaEntities() != null) {
            String rentAreas = buildingEntity.getRentAreaEntities().stream()
                    .map(item -> String.valueOf(item.getValue()))
                    .collect(Collectors.joining(","));
            dto.setRentArea(rentAreas);
        }
        return dto;
    }


    @Override
    public ResponseDTO loadStaffByBuildingId(Long id) {
        ResponseDTO responseDTO = new ResponseDTO();

        List<User> allStaff = userRepository.findByActiveAndUserRole(true, "ROLE_" + User.ROLE_EMPLOYEE);

        // Lấy building với staff list thông qua ManyToMany relationship
        BuildingEntity building = buildingRepository.findById(id)
                .orElseThrow(() -> new InvalidBuildingException("Building not found"));

        // Lấy Set ID của các staff đã được assign
        Set<Long> assignedStaffIds = building.getStaffs().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        // Tạo response DTO cho tất cả staff
        List<StaffResponseDTO> staffResponseDTOS = new ArrayList<>();

        for (User user : allStaff) {
            StaffResponseDTO staffResponseDTO = new StaffResponseDTO();
            staffResponseDTO.setId(user.getId());
            staffResponseDTO.setUsername(user.getFullName());
            staffResponseDTO.setChecked("");
            if (assignedStaffIds.contains(user.getId())) {
                staffResponseDTO.setChecked("checked");
            }
            staffResponseDTOS.add(staffResponseDTO);
        }
        responseDTO.setData(staffResponseDTOS);
        responseDTO.setMessage("Load staffs Successfully");
        return responseDTO;
    }

    @Override
    @Transactional
    public void assignBuilding(AssignBuildingDTO dto) {
        BuildingEntity building = buildingRepository.findById(dto.getBuildingId()).orElseThrow();
        building.getStaffs().clear();   // xóa assignment cũ
        List<User> newStaffs = userRepository.findAllById(dto.getStaffIds());
        building.getStaffs().addAll(newStaffs);

    }


}
