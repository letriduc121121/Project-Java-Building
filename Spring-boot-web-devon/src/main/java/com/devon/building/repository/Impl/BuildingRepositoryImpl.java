package com.devon.building.repository.Impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.entity.BuildingEntity;
import com.devon.building.repository.custom.BuildingRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class BuildingRepositoryImpl implements BuildingRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BuildingEntity> searchBuildings(BuildingSearchBuilder buildingSearchBuilder) {
        StringBuilder sql = new StringBuilder("SELECT b.* FROM building b");

        buildJoin(buildingSearchBuilder, sql);

        StringBuilder where = new StringBuilder(" where 1=1");

        buildWhere(buildingSearchBuilder, where);
        sql.append(where);
        sql.append(" GROUP BY b.id");

        Query query = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);

        @SuppressWarnings("unchecked")
        List<BuildingEntity> result = (List<BuildingEntity>) query.getResultList();
        return result;
    }

    private void buildJoin(BuildingSearchBuilder buildingSearchBuilder, StringBuilder join) {
        Long staffId = buildingSearchBuilder.getStaffId();
        if (staffId != null) {
            join.append(" INNER JOIN assignmentbuilding ab ON ab.buildingid = b.id");
        }

        Long rentAreaFrom = buildingSearchBuilder.getRentAreaFrom();
        Long rentAreaTo = buildingSearchBuilder.getRentAreaTo();
        if (rentAreaFrom != null || rentAreaTo != null) {
            join.append(" INNER JOIN rentarea ra ON ra.buildingid = b.id");
        }
    }

    private void buildWhere(BuildingSearchBuilder buildingSearchBuilder, StringBuilder where) {
        try {
            Field[] fields = BuildingSearchBuilder.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();

                // Bỏ qua các field được xử lý riêng hoặc field class con (Builder)
                if (!fieldName.equals("staffId") && !fieldName.equals("typeCode") &&
                        !fieldName.startsWith("rentArea") && !fieldName.startsWith("rentPrice") &&
                        !fieldName.equals("districtCode")) {

                    Object value = field.get(buildingSearchBuilder);
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
        Long staffId = buildingSearchBuilder.getStaffId();
        if (staffId != null) {
            where.append(" AND ab.staffid = ").append(staffId);
        }

        String districtCode = buildingSearchBuilder.getDistrictCode();
        if (districtCode != null && !districtCode.isEmpty()) {
            where.append(" AND b.district = '").append(districtCode).append("'");
        }

        // Rent Area
        Long rentAreaFrom = buildingSearchBuilder.getRentAreaFrom();
        Long rentAreaTo = buildingSearchBuilder.getRentAreaTo();
        if (rentAreaFrom != null) {
            where.append(" AND ra.value >= ").append(rentAreaFrom);
        }
        if (rentAreaTo != null) {
            where.append(" AND ra.value <= ").append(rentAreaTo);
        }

        // Rent Price
        Long rentPriceFrom = buildingSearchBuilder.getRentPriceFrom();
        Long rentPriceTo = buildingSearchBuilder.getRentPriceTo();
        if (rentPriceFrom != null) {
            where.append(" AND b.rentprice >= ").append(rentPriceFrom);
        }
        if (rentPriceTo != null) {
            where.append(" AND b.rentprice <= ").append(rentPriceTo);
        }
        // Type Code
        List<String> typeCode = buildingSearchBuilder.getTypeCode();
        if (typeCode != null && !typeCode.isEmpty()) {
            where.append(" AND (");
            String sqlJoin = typeCode.stream().map(item -> "b.type LIKE '%" + item + "%'").collect(Collectors.joining(" OR "));
            where.append(sqlJoin);
            where.append(" )");
        }
    }
}
