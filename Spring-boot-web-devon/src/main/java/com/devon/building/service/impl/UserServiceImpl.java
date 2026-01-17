package com.devon.building.service.impl;

import com.devon.building.constant.SystemConstant;
import com.devon.building.entity.User;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.UserRepository;
import com.devon.building.service.UserService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public PaginationResult<User> listUserInfo(String key, int page, int maxResult, int maxNavigationPage) {
        StringBuilder sql = new StringBuilder("SELECT NEW " + User.class.getName() + "(u.id, u.userName, u.active, u.userRole, u.fullName, u.phone) " + "FROM " + User.class.getName() + " u ");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(u.id) FROM " + User.class.getName() + " u ");

        if (key != null && !key.trim().isEmpty()) {
            sql.append("WHERE (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
            countSql.append("WHERE (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
        }

        sql.append("ORDER BY u.userName DESC");

        TypedQuery<User> query = entityManager.createQuery(sql.toString(), User.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        if (key != null && !key.trim().isEmpty()) {
            String searchKey = "%" + key.toLowerCase() + "%";
            query.setParameter("key", searchKey);
            countQuery.setParameter("key", searchKey);
        }
        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }

    @Override
    public void save(UserDTO userDTO) {
        String userName = userDTO.getUserName();
        User user = null;
        if (userName != null && !userName.isEmpty()) {
            user = userRepository.findByUserName(userName);
        }
        if (user != null) {
            throw new EntityExistsException("User with name " + userName + " already exists");
        }
        user = new User();
        user.setUserName(userName);
        user.setActive(true);
        user.setFullName(userDTO.getFullName());
        user.setEncrytedPassword(passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT));
        user.setUserRole(User.ROLE_MANAGER);
        if (userDTO.getFileData() != null) {
            byte[] image = null;
            try {
                image = userDTO.getFileData().getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Invalid image data", e);
            }
            if (image != null && image.length > 0) {
                user.setImage(image);
            }
        }
        entityManager.persist(user);
        entityManager.flush();
    }

    @Override
    public void update(UserDTO userDTO) {
        String userName = userDTO.getUserName();
        User user = null;
        if (userName != null && !userName.isEmpty()) {
            user = userRepository.findByUserName(userName);
        }
        if (user == null) {
            throw new EntityNotFoundException("Entity with name " + userName + " not found");
        }
        user.setUserName(userName);
        user.setActive(true);
        user.setUserRole(userDTO.getRoleCode());
        try {
            if (userDTO.getBase64Image() != null && !userDTO.getBase64Image().isEmpty()) {
                String base64String = userDTO.getBase64Image();
                if (base64String.contains(",")) {
                    base64String = base64String.split(",")[1];
                }

                byte[] imageBytes = Base64.getDecoder().decode(base64String);
                user.setImage(imageBytes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid image data", e);
        }
        userRepository.save(user);
    }

    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            Optional<User> user = userRepository.findById(id);
            user.ifPresent(value -> value.setActive(false));
            userRepository.flush();
        }
    }

    @Override
    public Map<Long, String> getAllStaff() {
        List<User> users = userRepository.findByActiveAndUserRole(true, "ROLE_"+User.ROLE_EMPLOYEE);
        Map<Long, String> staff = users.stream().collect(Collectors.toMap(User::getId,User::getFullName));
        return staff;
    }
}
