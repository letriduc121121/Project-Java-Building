package com.devon.building.service.impl;

import com.devon.building.constant.SystemConstant;
import com.devon.building.entity.User;
import com.devon.building.model.dto.UserDTO;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.UserRepository;
import com.devon.building.service.UserService;
import com.devon.building.utils.JwtTokenUtils;
import com.devon.building.utils.LocalizationUtils;
import com.devon.building.utils.MessageKeys;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalizationUtils localizationUtils;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            LocalizationUtils localizationUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.localizationUtils = localizationUtils;
    }

    // ================= AUTH METHODS =================
    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        String userName = userDTO.getUserName();
        if(userRepository.existsByUserName(userName)) {
            throw new RuntimeException("Username already exists");
        }

        // Đăng ký mặc định luôn là ROLE_USER
        String roleStr = "ROLE_USER";
        
        // tranh error DB do phone not null
        String phone = userDTO.getPhoneNumber() != null ? userDTO.getPhoneNumber() : "";

        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .userName(userDTO.getUserName())
                .phone(phone)
                .active(true)
                .userRole(roleStr)
                .build();

        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setEncrytedPassword(encodedPassword);
        return userRepository.save(newUser);
    }

    // ================= LIST =================
    @Override
    public PaginationResult<User> listUserInfo(String key, int page, int maxResult, int maxNavigationPage) {

        String baseQuery = " FROM " + User.class.getName() + " u WHERE u.active = true ";

        StringBuilder sql = new StringBuilder(
                "SELECT NEW " + User.class.getName() +
                        "(u.id, u.userName, u.active, u.userRole, u.fullName, u.phone) "
        ).append(baseQuery);

        StringBuilder countSql = new StringBuilder("SELECT COUNT(u.id)").append(baseQuery);

        if (isNotEmpty(key)) {
            sql.append(" AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key)");
            countSql.append(" AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key)");
        }

        sql.append(" ORDER BY u.userName DESC");

        TypedQuery<User> query = entityManager.createQuery(sql.toString(), User.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        if (isNotEmpty(key)) {
            String searchKey = "%" + key.toLowerCase() + "%";
            query.setParameter("key", searchKey);
            countQuery.setParameter("key", searchKey);
        }

        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }

    // ================= SAVE =================
    @Override
    public void save(UserDTO userDTO) {

        String userName = userDTO.getUserName();

        if (userRepository.findByUserName(userName) != null) {
            throw new EntityExistsException("User with name " + userName + " already exists");
        }

        User user = new User();
        user.setUserName(userName);
        user.setActive(true);
        user.setFullName(userDTO.getFullName());
        user.setEncrytedPassword(passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT));
        user.setUserRole("ROLE_" + User.ROLE_MANAGER);

        convertToByte(userDTO, user);

        entityManager.persist(user);
        entityManager.flush();
    }

    // ================= UPDATE =================
    @Override
    public void update(UserDTO userDTO) {

        String userName = userDTO.getUserName();
        User user = userRepository.findByUserName(userName);

        if (user == null) {
            throw new EntityNotFoundException("User " + userName + " not found");
        }

        user.setUserName(userName);
        user.setActive(true);
        user.setUserRole(userDTO.getRoleCode());

        convertToByte(userDTO, user);

        userRepository.save(user);
    }

    // ================= DELETE =================
    @Override
    public void delete(List<Long> ids) {
        ids.forEach(id -> userRepository.findById(id)
                .ifPresent(user -> user.setActive(false)));
        userRepository.flush();
    }

    // ================= GET STAFF =================
    @Override
    public Map<Long, String> getAllStaff() {
        return userRepository
                .findByActiveAndUserRole(true, "ROLE_" + User.ROLE_EMPLOYEE)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    @Override
    public User getUserByUserName(String  userName) {
        return userRepository.findByUserName(userName);

    }

    // ================= HELPER =================

    private void convertToByte(UserDTO userDTO, User user) {
        try {
            // 1. Upload file (ưu tiên)
            if (userDTO.getFileData() != null && !userDTO.getFileData().isEmpty()) {
                byte[] image = userDTO.getFileData().getBytes();
                user.setImage(image);
                return;
            }

            // 2. Base64
            if (isNotEmpty(userDTO.getBase64Image())) {
                String base64String = userDTO.getBase64Image();

                if (base64String.contains(",")) {
                    base64String = base64String.split(",")[1];
                }

                byte[] imageBytes = Base64.getDecoder().decode(base64String);
                user.setImage(imageBytes);
                return;
            }

            // 3. Không có gì → giữ nguyên (KHÔNG set null)

        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid image data", e);
        }
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}