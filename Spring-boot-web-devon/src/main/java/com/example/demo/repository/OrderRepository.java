package com.example.demo.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Product;

import com.example.demo.model.CartInfo;
import com.example.demo.model.CartLineInfo;
import com.example.demo.model.CustomerInfo;
import com.example.demo.model.OrderDetailInfo;
import com.example.demo.model.OrderInfo;
import com.example.demo.pagination.PaginationResult;

@Transactional
@Repository
public class OrderRepository {
 
    @PersistenceContext
    private EntityManager entityManager;
 
    @Autowired
    private ProductRepository productRepository;
 
    private int getMaxOrderNum() {
        String sql = "Select max(o.orderNum) from " + Order.class.getName() + " o ";
        Query query = entityManager.createQuery(sql, Integer.class);
        Integer value = (Integer) query.getSingleResult();
        if (value == null) {
            return 0;
        }
        return value;
    }
 
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(CartInfo cartInfo) {
 
        int orderNum = this.getMaxOrderNum() + 1;
        Order order = new Order();
 
        order.setId(UUID.randomUUID().toString());
        order.setOrderNum(orderNum);
        order.setOrderDate(new Date());
        order.setAmount(cartInfo.getAmountTotal());
 
        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
        order.setCustomerName(customerInfo.getName());
        order.setCustomerEmail(customerInfo.getEmail());
        order.setCustomerPhone(customerInfo.getPhone());
        order.setCustomerAddress(customerInfo.getAddress());

        entityManager.persist(order);
 
        List<CartLineInfo> lines = cartInfo.getCartLines();
 
        for (CartLineInfo line : lines) {
            OrderDetail detail = new OrderDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setOrder(order);
            detail.setAmount(line.getAmount());
            detail.setPrice(line.getProductInfo().getPrice());
            detail.setQuanity(line.getQuantity());
 
            String code = line.getProductInfo().getCode();
            Product product = this.productRepository.findProduct(code);
            detail.setProduct(product);

            entityManager.persist(detail);
        }
 
        // Order Number!
        cartInfo.setOrderNum(orderNum);
        // Flush
        entityManager.flush();
    }
 
    // @page = 1, 2, ...
    public PaginationResult<OrderInfo> listOrderInfo(int page, int maxResult, int maxNavigationPage) {

        String sql = "SELECT NEW " + OrderInfo.class.getName()
                + "(ord.id, ord.orderDate, ord.orderNum, ord.amount, "
                + "ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) "
                + "FROM " + Order.class.getName() + " ord "
                + "ORDER BY ord.orderNum DESC";

        String countSql = "SELECT COUNT(ord.id) FROM " + Order.class.getName() + " ord";

        TypedQuery<OrderInfo> query = entityManager.createQuery(sql, OrderInfo.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }
 
    public Order findOrder(String orderId) {
        return entityManager.find(Order.class, orderId);
    }
 
    public OrderInfo getOrderInfo(String orderId) {
        Order order = this.findOrder(orderId);
        if (order == null) {
            return null;
        }
        return new OrderInfo(order.getId(), order.getOrderDate(), //
                order.getOrderNum(), order.getAmount(), order.getCustomerName(), //
                order.getCustomerAddress(), order.getCustomerEmail(), order.getCustomerPhone());
    }
 
    public List<OrderDetailInfo> listOrderDetailInfos(String orderId) {
        String sql = "Select new " + OrderDetailInfo.class.getName() //
                + "(d.id, d.product.code, d.product.name , d.quanity,d.price,d.amount) "//
                + " from " + OrderDetail.class.getName() + " d "//
                + " where d.order.id = :orderId ";

        Query query = entityManager.createQuery(sql, OrderDetailInfo.class);
        query.setParameter("orderId", orderId);
 
        return query.getResultList();
    }
 
}