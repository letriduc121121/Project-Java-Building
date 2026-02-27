package com.devon.building.api.admin;

import com.devon.building.model.dto.AssignBuildingDTO;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/buildings")
public class BuildingAPI {
    private static final String SERVER_ERROR_PREFIX = "Server Error: ";
    
    private final BuildingService buildingService;

    public BuildingAPI(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addBuilding(@RequestBody @Valid BuildingDTO dto, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Failed to add Building");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            buildingService.create(dto);
            responseDTO.setMessage("Successfully ADDED Building");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> updateBuilding(@RequestBody @Valid BuildingDTO dto, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Failed to update Building");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        if (dto.getId() == null) {
            responseDTO.setMessage("Building ID is required for update");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            buildingService.update(dto);
            responseDTO.setMessage("Successfully UPDATED Building");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<ResponseDTO> deleteBuilding(@PathVariable List<Long> ids) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            buildingService.delete(ids);
            responseDTO.setMessage("Successfully DELETED Building(s)");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    //Lấy danh sách nhân viên
    @GetMapping("/{id}/staffs")
    public ResponseEntity<ResponseDTO> getStaffs(@PathVariable Long id) {
        try {
            ResponseDTO responseDTO = buildingService.loadStaffByBuildingId(id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMessage("Error loading staffs: " + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    //Giao tòa nhà cho nhân viên
    @PutMapping("/assign")
    public ResponseEntity<ResponseDTO> assignBuilding(@RequestBody @Valid AssignBuildingDTO dto, BindingResult result) {
        ResponseDTO responseDTO = new ResponseDTO();
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            responseDTO.setDetail(errors);
            responseDTO.setMessage("Validation failed");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        try {
            buildingService.assignBuilding(dto);
            responseDTO.setMessage("Successfully assigned building to staffs");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage(SERVER_ERROR_PREFIX + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
}
