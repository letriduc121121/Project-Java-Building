package com.devon.building.service;

import com.devon.building.entity.User;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.pagination.PaginationResult;

import java.util.List;
import java.util.Map;

public interface UserService {
    PaginationResult<User> listUserInfo(String key,int page, int maxResult, int maxNavigationPage);

    void save(UserDTO userDTO);

    void update(UserDTO userDTO);

    void delete(List<Long> ids);

    Map<Long,String> getAllStaff();
}
