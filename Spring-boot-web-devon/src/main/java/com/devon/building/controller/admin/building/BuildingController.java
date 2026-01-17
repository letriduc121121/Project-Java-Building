package com.devon.building.controller.admin.building;

import com.devon.building.enums.District;
import com.devon.building.enums.RentType;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.service.BuildingService;
import com.devon.building.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin/buildings")
public class BuildingController {

    @Autowired
    private UserService userService;
    @Autowired
    private BuildingService buildingService;

    @GetMapping("/list")
    public String getListBuilding(@ModelAttribute BuildingSearchRequest buildingSearchRequest, Model model) {
        // Gọi service để tìm kiếm
        List<BuildingSearchResponse> resultBuildings = buildingService.searchBuildings(
                convertRequestToMap(buildingSearchRequest),
                buildingSearchRequest.getTypeCode()
        );

        // Thêm dữ liệu vào model
        model.addAttribute("resultBuilding", resultBuildings);
        model.addAttribute("modelSearch", buildingSearchRequest);
        model.addAttribute("districts", District.getDistrict());
        model.addAttribute("staffs", userService.getAllStaff());
        model.addAttribute("typeCode", RentType.getTypeCode());
        return "admin/building/buildingList";
    }

    private Map<String, String> convertRequestToMap(BuildingSearchRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("name", request.getName());
        map.put("floorArea", request.getFloorArea() != null ? String.valueOf(request.getFloorArea()) : null);
        map.put("districtCode", request.getDistrictCode() != null ? String.valueOf(request.getDistrictCode()) : null);
        map.put("ward", request.getWard());
        map.put("street", request.getStreet());
        map.put("numberOfBasement", request.getNumberOfBasement() != null ? String.valueOf(request.getNumberOfBasement()) : null);
        map.put("direction", request.getDirection());
        map.put("level", request.getLevel() != null ? String.valueOf(request.getLevel()) : null);
        map.put("rentAreaFrom", request.getRentAreaFrom() != null ? String.valueOf(request.getRentAreaFrom()) : null);
        map.put("rentAreaTo", request.getRentAreaTo() != null ? String.valueOf(request.getRentAreaTo()) : null);
        map.put("rentPriceFrom", request.getRentPriceFrom() != null ? String.valueOf(request.getRentPriceFrom()) : null);
        map.put("rentPriceTo", request.getRentPriceTo() != null ? String.valueOf(request.getRentPriceTo()) : null);
        map.put("managerName", request.getManagerName());
        map.put("managerPhone", request.getManagerPhone());
        map.put("staffId", request.getStaffId() != null ? String.valueOf(request.getStaffId()) : null);
        return map;
    }

    @GetMapping("/edit")
    public String createBuilding(Model model) {
        model.addAttribute("building", new BuildingDTO()); // Thêm object rỗng để tránh lỗi Thymeleaf
        model.addAttribute("districts", District.getDistrict());
        model.addAttribute("typeCode", RentType.getTypeCode());
        return "admin/building/buildingEdit";
    }

    @GetMapping("/update/{id}")
    public String updateBuilding(Model model, @PathVariable long id) {
        BuildingDTO building = buildingService.findById(id);
        model.addAttribute("building", building);
        model.addAttribute("districts", District.getDistrict());
        model.addAttribute("typeCode", RentType.getTypeCode());
        return "admin/building/buildingEdit";
    }
}
