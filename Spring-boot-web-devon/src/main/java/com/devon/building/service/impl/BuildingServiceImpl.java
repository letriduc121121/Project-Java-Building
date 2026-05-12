package com.devon.building.service.impl;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.convert.BuildingConvertor;
import com.devon.building.convert.BuildingSearchBuilderConvertor;
import com.devon.building.entity.BuildingEntity;
import com.devon.building.entity.RentAreaEntity;
import com.devon.building.entity.User;
import com.devon.building.exception.InvalidEntityException;
import com.devon.building.model.dto.*;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.*;
import com.devon.building.service.BuildingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingConvertor buildingConvertor;
    private final BuildingSearchBuilderConvertor buildingSearchBuilderConvertor;

    @PersistenceContext
    private EntityManager entityManager;

    public BuildingServiceImpl(
            ModelMapper modelMapper,
            UserRepository userRepository,
            BuildingRepository buildingRepository,
            BuildingConvertor buildingConvertor,
            BuildingSearchBuilderConvertor buildingSearchBuilderConvertor) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.buildingConvertor = buildingConvertor;
        this.buildingSearchBuilderConvertor = buildingSearchBuilderConvertor;
    }

    @Override
    public PaginationResult<BuildingSearchResponse> searchBuildings(BuildingSearchRequest request, int page, int maxResult, int maxNavigationPage) {
        PaginationResult<BuildingEntity> buildingEntities = buildingRepository.searchBuildings(request,page,maxResult,maxNavigationPage);
        PaginationResult<BuildingSearchResponse> buildingSearchResponseList = new PaginationResult<>();
        List<BuildingSearchResponse> buildingSearchResponses = new ArrayList<>();
        for (BuildingEntity building : buildingEntities.getList()) {
            BuildingSearchResponse buildingResponseDTO = buildingConvertor.convertToBuildingResponseDTO(building);
            buildingSearchResponses.add(buildingResponseDTO);
        }
        buildingSearchResponseList.setList(buildingSearchResponses);
        buildingSearchResponseList.setCurrentPage(buildingEntities.getCurrentPage());
        buildingSearchResponseList.setMaxResult(buildingEntities.getMaxResult());
        buildingSearchResponseList.setMaxNavigationPage(buildingEntities.getMaxNavigationPage());
        buildingSearchResponseList.setNavigationPages(buildingEntities.getNavigationPages());
        buildingSearchResponseList.setTotalPages(buildingEntities.getTotalPages());
        buildingSearchResponseList.setTotalRecords(buildingEntities.getTotalRecords());


        return buildingSearchResponseList;
    }
    private void convertToByte(BuildingDTO buildingDTO, BuildingEntity building) {
        try {
            String base64 = buildingDTO.getImageBase64();

            // Không có ảnh → giữ nguyên
            if (base64 == null || base64.trim().isEmpty()) {
                return;
            }

            // Loại bỏ prefix (data:image/...;base64,...)
            if (base64.contains(",")) {
                base64 = base64.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64);
            building.setImage(imageBytes);

        } catch (IllegalArgumentException e) {
            // lỗi decode base64
            throw new IllegalArgumentException("Invalid base64 image format", e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    @Override
    @Transactional
    public BuildingEntity create(BuildingDTO buildingDTO) {
        // chuyen dto -> entity
        BuildingEntity buildingEntity = buildingConvertor.toBuildingEntity(buildingDTO);

        // Sử dụng setter để gán list mới cho Entity mới
        buildingEntity.setRentAreaEntities(createRentAreaForBuilding(buildingDTO, buildingEntity));

        convertToByte(buildingDTO,buildingEntity);
        // Lưu và đẩy thẳng query xuống DB bằng JpaRepository
        buildingEntity = buildingRepository.saveAndFlush(buildingEntity);

        return buildingEntity;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        buildingRepository.deleteByIdIn(ids);
    }

    @Override
    @Transactional
    public BuildingEntity update(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = buildingConvertor.toBuildingEntity(buildingDTO);
        // Merge để lấy entity đang được quản lý bởi JPA
        BuildingEntity storedBuilding = entityManager.merge(buildingEntity);
        // Xóa RentArea (clear để giữ lại reference của Hibernate list)
        storedBuilding.getRentAreaEntities().clear();
        // Thêm RentArea
        storedBuilding.getRentAreaEntities().addAll(createRentAreaForBuilding(buildingDTO, storedBuilding));
        convertToByte(buildingDTO,storedBuilding);
        // Lưu thay đổi
        storedBuilding = buildingRepository.saveAndFlush(storedBuilding);
        return storedBuilding;
    }

    private List<RentAreaEntity> createRentAreaForBuilding(BuildingDTO buildingDTO, BuildingEntity buildingEntity) {
        List<RentAreaEntity> rentAreas = new ArrayList<>();
        if (buildingDTO.getRentArea() != null && !buildingDTO.getRentArea().isEmpty()) {
            String[] rentAreaArray = buildingDTO.getRentArea().split(",");
            for (String val : rentAreaArray) {
                if (!val.trim().isEmpty()) {
                    RentAreaEntity rentAreaEntity = new RentAreaEntity();
                    rentAreaEntity.setBuilding(buildingEntity);
                    rentAreaEntity.setValue(Long.parseLong(val.trim()));
                    rentAreas.add(rentAreaEntity);
                }
            }
        }
        return rentAreas;
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

        // Lấy building với staff list thông qua ManyToMany relationship
        BuildingEntity building = buildingRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Building not found"));

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
        List<User> newStaffs = new ArrayList<>();
        if (dto.getStaffIds() != null && !dto.getStaffIds().isEmpty()) {
            newStaffs = userRepository.findAllById(dto.getStaffIds());
        }
        building.setStaffs(newStaffs);
        buildingRepository.save(building);

    }


}
