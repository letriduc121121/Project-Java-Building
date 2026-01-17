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
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.repository.AssignmentBuildingRepository;
import com.devon.building.repository.BuildingRepository;
import com.devon.building.repository.RentAreaRepository;
import com.devon.building.repository.UserRepository;
import com.devon.building.service.BuildingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RentAreaRepository rentAreaRepository;

    @Autowired
    private AssignmentBuildingRepository assignmentBuildingRepository;

    @Autowired
    private BuildingConvertor buildingConvertor;

    @Autowired
    // dug cai nay ko can khoi tao
    private BuildingSearchBuilderConvertor buildingSearchBuilderConvertor;
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<BuildingSearchResponse> searchBuildings(Map<String, String> requestParams, List<String> typeCode) {

        BuildingSearchBuilder buildingSearchBuilder = buildingSearchBuilderConvertor.toBuildingSearchBuilder(requestParams, typeCode);

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
        entityManager.flush();
        Query q = entityManager.createNativeQuery("SELECT b.* FROM building b", BuildingEntity.class);
        int count = q.getResultList().size();

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
        rentAreaRepository.deleteAllByBuilding_IdIn(ids);
        buildingRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional
    public BuildingEntity update(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = buildingConvertor.toBuildingEntity(buildingDTO);

        if (buildingDTO.getId() != null) {
            buildingEntity.setId(buildingDTO.getId());
        }

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
//        //dữ liệu mẫu
//        StaffResponseDTO staff1 = new StaffResponseDTO();
//        StaffResponseDTO staff2 = new StaffResponseDTO();
//        StaffResponseDTO staff3 = new StaffResponseDTO();
//        staff1.setId(22L);
//        staff1.setUsername("Devonstaff1");
//        staff1.setChecked("checked");
//
//        staff2.setId(33L);
//        staff2.setUsername("Devonstaff2");
//        staff2.setChecked("");
//
//        staff3.setId(44L);
//        staff3.setUsername("aNDYNGO");
//        staff3.setChecked("");
//        staffResponseDTOS.add(staff1);
//        staffResponseDTOS.add(staff2);
//        staffResponseDTOS.add(staff3);


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
