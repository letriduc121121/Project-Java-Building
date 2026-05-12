package com.devon.building.controller.admin.building;

import com.devon.building.constant.SystemConstant;
import com.devon.building.entity.BuildingEntity;
import com.devon.building.entity.User;
import com.devon.building.enums.District;
import com.devon.building.enums.RentType;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.BuildingRepository;
import com.devon.building.service.BuildingService;
import com.devon.building.service.UserService;
import com.devon.building.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("admin/buildings")
public class BuildingController {
    private static final String ATTR_DISTRICTS = "districts";
    private static final String ATTR_TYPE_CODE = "typeCode";

    private final UserService userService;
    private final BuildingService buildingService;
    private final BuildingRepository buildingRepository;

    public BuildingController(UserService userService, BuildingService buildingService, BuildingRepository buildingRepository) {
        this.userService = userService;
        this.buildingService = buildingService;
        this.buildingRepository = buildingRepository;
    }

    @GetMapping("/list")
    public String getListBuilding(@RequestParam (value = "page",defaultValue = "1") String pageStr, @ModelAttribute BuildingSearchRequest buildingSearchRequest, Model model) {
        // Gọi service để tìm kiếm
        int page = 1;
        try{
            page = Integer.parseInt(pageStr);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Thêm dữ liệu vào model
        model.addAttribute("buildingSearchRequest", buildingSearchRequest);
        model.addAttribute("modelSearch", buildingSearchRequest);
        model.addAttribute(ATTR_DISTRICTS, District.getDistrict());
        model.addAttribute("staffs", userService.getAllStaff());
        model.addAttribute(ATTR_TYPE_CODE, RentType.getTypeCode());
        if(SecurityUtils.getAuthorities().contains((SystemConstant.STAFF_ROLE))){
            User user=userService.getUserByUserName(SecurityUtils.getCurrentUser().getUsername());
            buildingSearchRequest.setStaffId(user.getId());//tim kiem theo nhan vien phu trach
        }
        PaginationResult<BuildingSearchResponse> buildingSearchResponses = buildingService.searchBuildings(buildingSearchRequest,page, SystemConstant.MAX_RESULT,SystemConstant.MAX_NAVIGATION_PAGE);

        model.addAttribute("resultBuilding", buildingSearchResponses);
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
        //neu la staff thi user ca quan ly nha hien tai ko, neu la manager thi cho di tiep
        if(SecurityUtils.getAuthorities().contains((SystemConstant.STAFF_ROLE))){
            User user=userService.getUserByUserName(SecurityUtils.getCurrentUser().getUsername());
            if(user.getBuildings().stream().noneMatch(b->b.getId().equals(id))){
                      return "/404";
            }
        }
        BuildingDTO building = buildingService.findById(id);
        model.addAttribute("building", building);
        model.addAttribute(ATTR_DISTRICTS, District.getDistrict());
        model.addAttribute(ATTR_TYPE_CODE, RentType.getTypeCode());
        return "admin/building/buildingEdit";
    }
    @GetMapping("/buildingImage")
        public void productImage(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(value = "id", defaultValue = "") Long id) throws IOException {
        BuildingEntity building = buildingRepository.findById(id).orElseThrow();

        if (building != null && building.getImage() != null) {
            response.setContentType("image/jpeg");
            response.setContentType("image/png");
            response.getOutputStream().write(building.getImage());
        }
        response.getOutputStream().close();
    }
}
