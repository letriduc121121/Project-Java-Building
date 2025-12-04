package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.form.ProductForm;
import com.example.demo.model.ProductInfo;
import com.example.demo.pagination.PaginationResult;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;

@Transactional
@RequiredArgsConstructor
@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Product findProduct(String code) {
        try {
            String sql = "Select e from " + Product.class.getName() + " e Where e.code =:code ";
            Query query = entityManager.createQuery(sql, Product.class);
            query.setParameter("code", code);
            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ProductInfo findProductInfo(String code) {
        Product product = this.findProduct(code);
        if (product == null) {
            return null;
        }
        return new ProductInfo(product.getCode(), product.getName(), product.getPrice());
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(ProductForm productForm) {
        String code = productForm.getCode();

        Product product = null;

        boolean isNew = false;
        if (code != null) {
            product = this.findProduct(code);
        }
        if (product == null) {
            isNew = true;
            product = new Product();
            product.setCreateDate(new Date());
        }
        product.setCode(code);
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());

        if (productForm.getFileData() != null) {
            byte[] image = null;
            try {
                image = productForm.getFileData().getBytes();
            } catch (IOException e) {
            }
            if (image != null && image.length > 0) {
                product.setImage(image);
            }
        }
        if (isNew) {
            entityManager.persist(product);
        }
        // If error in DB, Exceptions will be thrown out immediately
        entityManager.flush();
    }

    @Transactional(readOnly = true)
    public PaginationResult<ProductInfo> queryProducts(
            int page, int maxResult, int maxNavigationPage, String likeName) {

        // MAIN QUERY
        String sql = "Select new " + ProductInfo.class.getName()
                + "(p.code, p.name, p.price) from "
                + Product.class.getName() + " p ";

        // COUNT QUERY
        String countSql = "Select count(p) from "
                + Product.class.getName() + " p ";

        boolean hasLike = likeName != null && !likeName.isEmpty();

        if (hasLike) {
            sql += " where lower(p.name) like :likeName ";
            countSql += " where lower(p.name) like :likeName ";
        }

        sql += " order by p.createDate desc ";

        // Tạo TypedQuery
        TypedQuery<ProductInfo> query =
                entityManager.createQuery(sql, ProductInfo.class);

        // Tạo CountQuery
        TypedQuery<Long> countQuery =
                entityManager.createQuery(countSql, Long.class);
        if (hasLike) {
            String v = "%" + likeName.toLowerCase() + "%";
            query.setParameter("likeName", v);
            countQuery.setParameter("likeName", v);
        }

        // Trả về phân trang
        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }

    @Transactional(readOnly = true)
    public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage) {
        return queryProducts(page, maxResult, maxNavigationPage, null);
    }

}
