package org.example.ex_05.repository;

import org.example.ex_05.model.CustomerOrder;
import org.example.ex_05.model.OrderItem;
import org.example.ex_05.model.OrderStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private final SessionFactory sessionFactory;

    public OrderRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CustomerOrder saveOrder(CustomerOrder order) {
        currentSession().persist(order);
        return order;
    }

    public void saveOrderItem(OrderItem item) {
        currentSession().persist(item);
    }

    public long countByStatus(OrderStatus status) {
        return currentSession()
                .createSelectionQuery("select count(o.id) from CustomerOrder o where o.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}


