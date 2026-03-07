package com.devon.building.service.impl;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.convert.BuildingConvertor;
import com.devon.building.convert.BuildingSearchBuilderConvertor;
import com.devon.building.entity.*;
import com.devon.building.exception.InvalidEntityException;
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

import java.util.*;
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
        saveRentAreas(buildingDTO, buildingEntity);
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

        saveRentAreas(buildingDTO, storedBuilding);
        return storedBuilding;
    }

    private void saveRentAreas(BuildingDTO buildingDTO, BuildingEntity buildingEntity) {
        if (buildingDTO.getRentArea() != null && !buildingDTO.getRentArea().isEmpty()) {
            String[] rentAreas = buildingDTO.getRentArea().split(",");
            for (String val : rentAreas) {
                if (!val.trim().isEmpty()) {
                    RentAreaEntity rentAreaEntity = new RentAreaEntity();
                    rentAreaEntity.setBuilding(buildingEntity);
                    rentAreaEntity.setValue(Long.parseLong(val.trim()));
                    entityManager.persist(rentAreaEntity);
                }
            }
        }
    }


    @Override
    public BuildingDTO findById(Long id) {
        BuildingEntity buildingEntity = buildingRepository.findById(id).orElseThrow(() -> new InvalidEntityException("Building not found"));
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

        Set<Long> assignedStaffIds = assignmentBuildingRepository.findByBuildingId(id).stream()
                .map(AssignmentBuildingEntity::getStaffId)
                .collect(Collectors.toSet());

        List<StaffResponseDTO> staffResponseDTOList = getStaffResponseDTOS(allStaff, assignedStaffIds);

        responseDTO.setData(staffResponseDTOList);
        responseDTO.setMessage("Load staffs successfully");
        return responseDTO;
    }

    private List<StaffResponseDTO> getStaffResponseDTOS(List<User> allStaff, Set<Long> assignedStaffIds) {
        List<StaffResponseDTO> staffResponseDTOList = new ArrayList<>();
        for (User user : allStaff) {
            StaffResponseDTO staffResponseDTO = new StaffResponseDTO();
            staffResponseDTO.setId(user.getId());
            staffResponseDTO.setUsername(user.getFullName());
            staffResponseDTO.setChecked("");
            if (assignedStaffIds.contains(user.getId())) {
                staffResponseDTO.setChecked("checked");
            }
            staffResponseDTOList.add(staffResponseDTO);
        }
        return staffResponseDTOList;
    }

    @Override
    @Transactional
    public void assignBuilding(AssignBuildingDTO assignBuildingDTO) {
        // Kiểm tra building tồn tại
        buildingRepository.findById(assignBuildingDTO.getBuildingId())
                .orElseThrow(() -> new InvalidEntityException("Building not found"));

        // Xóa tất cả assignment cũ của tòa nhà này
        assignmentBuildingRepository.deleteByBuildingId(assignBuildingDTO.getBuildingId());
        

        List<AssignmentBuildingEntity> assignmentBuildings = new ArrayList<>();
        for (Long staffId : assignBuildingDTO.getStaffIds()) {
            AssignmentBuildingEntity assignmentBuilding = new AssignmentBuildingEntity();
            assignmentBuilding.setBuildingId(assignBuildingDTO.getBuildingId());
            assignmentBuilding.setStaffId(staffId);
            assignmentBuildings.add(assignmentBuilding);
        }

        assignmentBuildingRepository.saveAll(assignmentBuildings);
    }


}
