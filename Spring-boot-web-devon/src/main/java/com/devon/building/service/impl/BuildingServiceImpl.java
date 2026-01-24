package com.devon.building.service.impl;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.convert.BuildingConvertor;
import com.devon.building.convert.BuildingSearchBuilderConvertor;
import com.devon.building.entity.AssignmentBuildingEntity;
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
import jakarta.persistence.Query;
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
    private final AssignmentBuildingRepository assignmentBuildingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final BuildingConvertor buildingConvertor;
    private final BuildingSearchBuilderConvertor buildingSearchBuilderConvertor;

    @PersistenceContext
    private EntityManager entityManager;

    public BuildingServiceImpl(
            ModelMapper modelMapper,
            UserRepository userRepository,
            BuildingRepository buildingRepository,
            RentAreaRepository rentAreaRepository,
            AssignmentBuildingRepository assignmentBuildingRepository,
            OrderDetailRepository orderDetailRepository,
            BuildingConvertor buildingConvertor,
            BuildingSearchBuilderConvertor buildingSearchBuilderConvertor) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.rentAreaRepository = rentAreaRepository;
        this.assignmentBuildingRepository = assignmentBuildingRepository;
        this.orderDetailRepository = orderDetailRepository;
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
        BuildingEntity buildingEntity = buildingConvertor.toBuildingEntity(buildingDTO);
        entityManager.persist(buildingEntity);

        List<RentAreaEntity> rentAreaEntities = new ArrayList<>();
        if (buildingDTO.getRentArea() != null && !buildingDTO.getRentArea().isEmpty()) {
            String[] rentAreas = buildingDTO.getRentArea().split(",");
            for (String val : rentAreas) {
                if (!val.trim().isEmpty()) {
                    RentAreaEntity rentAreaEntity = new RentAreaEntity();
                    rentAreaEntity.setBuilding(buildingEntity);
                    rentAreaEntity.setValue(Long.parseLong(val.trim()));
                    rentAreaEntities.add(rentAreaEntity);
                }
            }
        }
        rentAreaRepository.saveAll(rentAreaEntities);
        return buildingEntity;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        // Xóa theo thứ tự: OrderDetail -> AssignmentBuilding -> RentArea -> Building
        // để tránh foreign key constraint violation
        orderDetailRepository.deleteAllByBuilding_IdIn(ids);

        // Xóa assignment của building
        assignmentBuildingRepository.deleteAllByBuildingIdIn(ids);

        // Xóa rent area
        rentAreaRepository.deleteAllByBuilding_IdIn(ids);

        // Cuối cùng mới xóa building
        buildingRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional
    public BuildingEntity update(BuildingDTO buildingDTO) {
        // toBuildingEntity đã tự động tìm entity cũ và map DTO vào
        BuildingEntity buildingEntity = buildingConvertor.toBuildingEntity(buildingDTO);

        BuildingEntity storedBuilding = entityManager.merge(buildingEntity);

        Query query = entityManager.createQuery("DELETE FROM RentAreaEntity r WHERE r.building.id = :id");
        query.setParameter("id", storedBuilding.getId());
        query.executeUpdate();

        if (buildingDTO.getRentArea() != null && !buildingDTO.getRentArea().isEmpty()) {
            String[] rentAreas = buildingDTO.getRentArea().split(",");
            for (String val : rentAreas) {
                if (!val.trim().isEmpty()) {
                    RentAreaEntity rentAreaEntity = new RentAreaEntity();
                    rentAreaEntity.setBuilding(storedBuilding);
                    rentAreaEntity.setValue(Long.parseLong(val.trim()));
                    entityManager.persist(rentAreaEntity);
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

        // Lấy danh sách staff đang quản lý tòa nhà này
        List<AssignmentBuildingEntity> assignments = assignmentBuildingRepository.findByBuildingId(id);

        Set<Long> assignedStaffIds = assignments.stream()
                .map(AssignmentBuildingEntity::getStaffId)
                .collect(Collectors.toSet());

        //làm sao tìm kiếm tối ưu 1 list các user theo map
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
        // Xóa tất cả assignment cũ của tòa nhà này
        assignmentBuildingRepository.deleteByBuildingId(dto.getBuildingId());

        // Tạo assignment mới cho các staff được chọn
        if (dto.getStaffIds() != null && !dto.getStaffIds().isEmpty()) {
            for (Long staffId : dto.getStaffIds()) {
                AssignmentBuildingEntity assignment = new AssignmentBuildingEntity();
                assignment.setBuildingId(dto.getBuildingId());
                assignment.setStaffId(staffId);
                assignmentBuildingRepository.save(assignment);
            }
        }
    }


}
