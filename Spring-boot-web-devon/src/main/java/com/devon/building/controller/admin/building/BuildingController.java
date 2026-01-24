package com.devon.building.controller.admin.building;

import com.devon.building.enums.District;
import com.devon.building.enums.RentType;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.service.BuildingService;
import com.devon.building.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("admin/buildings")
public class BuildingController {
    private static final String ATTR_DISTRICTS = "districts";
    private static final String ATTR_TYPE_CODE = "typeCode";
    
    private final UserService userService;
    private final BuildingService buildingService;

    public BuildingController(UserService userService, BuildingService buildingService) {
        this.userService = userService;
        this.buildingService = buildingService;
    }

    @GetMapping("/list")
    public String getListBuilding(@ModelAttribute BuildingSearchRequest buildingSearchRequest, Model model) {
        // Gọi service để tìm kiếm
        List<BuildingSearchResponse> resultBuildings = buildingService.searchBuildings(buildingSearchRequest);

        // Thêm dữ liệu vào model
        model.addAttribute("resultBuilding", resultBuildings);
        model.addAttribute("modelSearch", buildingSearchRequest);
        model.addAttribute(ATTR_DISTRICTS, District.getDistrict());
        model.addAttribute("staffs", userService.getAllStaff());
        model.addAttribute(ATTR_TYPE_CODE, RentType.getTypeCode());
        return "admin/building/buildingList";
    }

    @GetMapping("/edit")
    public String createBuilding(Model model) {
        model.addAttribute("building", new BuildingDTO()); // Thêm object rỗng để tránh lỗi Thymeleaf
        model.addAttribute(ATTR_DISTRICTS, District.getDistrict());
        model.addAttribute(ATTR_TYPE_CODE, RentType.getTypeCode());
        return "admin/building/buildingEdit";
    }

    @GetMapping("/update/{id}")
    public String updateBuilding(Model model, @PathVariable long id) {
        BuildingDTO building = buildingService.findById(id);
        model.addAttribute("building", building);
        model.addAttribute(ATTR_DISTRICTS, District.getDistrict());
        model.addAttribute(ATTR_TYPE_CODE, RentType.getTypeCode());
        return "admin/building/buildingEdit";
    }
}
