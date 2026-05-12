package com.devon.building.repository.Impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import com.devon.building.entity.BuildingEntity;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.pagination.PaginationResult;
import com.devon.building.repository.custom.BuildingRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class BuildingRepositoryImpl implements BuildingRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PaginationResult<BuildingEntity> searchBuildings(BuildingSearchRequest buildingSearchRequest, int page, int maxResult, int maxNavigationPage) {
        StringBuilder sql = new StringBuilder("SELECT b.* FROM building b");

        buildJoin(buildingSearchRequest, sql);

        StringBuilder where = new StringBuilder(" where 1=1");

        buildWhere(buildingSearchRequest, where);
        sql.append(where);
        sql.append(" GROUP BY b.id");
        sql.append(" ORDER BY b.name ASC"); // Sắp xếp theo tên tăng dần (A → Z)

        Query query = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);

        // Đếm tổng số bản ghi bằng cách đếm số lượng bản ghi của chuỗi sql vừa build xong
        String countSql = "SELECT COUNT(*) FROM (" + sql.toString() + ") AS total";
        Query countQuery = entityManager.createNativeQuery(countSql);
        int totalRecords = ((Number) countQuery.getSingleResult()).intValue();

        return new PaginationResult<>(query, totalRecords, page, maxResult, maxNavigationPage);
    }

    private void buildJoin(BuildingSearchRequest buildingSearchRequest, StringBuilder join) {
        Long staffId = buildingSearchRequest.getStaffId();
        if (staffId != null) {
            join.append(" INNER JOIN assignmentbuilding ab ON ab.buildingid = b.id");
        }

        Long rentAreaFrom = buildingSearchRequest.getRentAreaFrom();
        Long rentAreaTo = buildingSearchRequest.getRentAreaTo();
        if (rentAreaFrom != null || rentAreaTo != null) {
            join.append(" INNER JOIN rentarea ra ON ra.buildingid = b.id");
        }
    }

    private void buildWhere(BuildingSearchRequest buildingSearchRequest, StringBuilder where) {
        try {
            Field[] fields = BuildingSearchRequest.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();

                // Bỏ qua các field được xử lý riêng hoặc field class con (Builder)
                if (!fieldName.equals("staffId") && !fieldName.equals("typeCode") &&
                        !fieldName.startsWith("rentArea") && !fieldName.startsWith("rentPrice") &&
                        !fieldName.equals("districtCode")) {

                    Object value = field.get(buildingSearchRequest);
                    if (value != null && !value.toString().equals("")) {
                        if (field.getType().getName().equals("java.lang.Long")
                                || field.getType().getName().equals("java.lang.Integer")) {
                            where.append(" AND b.").append(fieldName.toLowerCase()).append(" = ").append(value);
                        } else if (field.getType().getName().equals("java.lang.String")) {
                            where.append(" AND b.").append(fieldName.toLowerCase()).append(" LIKE '%").append(value)
                                    .append("%'");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Staff
        Long staffId = buildingSearchRequest.getStaffId();
        if (staffId != null) {
            where.append(" AND ab.staffid = ").append(staffId);
        }

        String districtCode = buildingSearchRequest.getDistrictCode();
        if (districtCode != null && !districtCode.isEmpty()) {
            where.append(" AND b.district = '").append(districtCode).append("'");
        }

        // Rent Area
        Long rentAreaFrom = buildingSearchRequest.getRentAreaFrom();
        Long rentAreaTo = buildingSearchRequest.getRentAreaTo();
        if (rentAreaFrom != null) {
            where.append(" AND ra.value >= ").append(rentAreaFrom);
        }
        if (rentAreaTo != null) {
            where.append(" AND ra.value <= ").append(rentAreaTo);
        }

        // Rent Price
        Long rentPriceFrom = buildingSearchRequest.getRentPriceFrom();
        Long rentPriceTo = buildingSearchRequest.getRentPriceTo();
        if (rentPriceFrom != null) {
            where.append(" AND b.rentprice >= ").append(rentPriceFrom);
        }
        if (rentPriceTo != null) {
            where.append(" AND b.rentprice <= ").append(rentPriceTo);
        }
        // Type Code
        List<String> typeCode = buildingSearchRequest.getTypeCode();
        if (typeCode != null && !typeCode.isEmpty()) {
            where.append(" AND (");
            String sqlJoin = typeCode.stream().map(item -> "b.type LIKE '%" + item + "%'").collect(Collectors.joining(" OR "));
            where.append(sqlJoin);
            where.append(" )");
        }
    }
}
