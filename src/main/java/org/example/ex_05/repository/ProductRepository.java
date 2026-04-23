package org.example.ex_05.repository;

import org.example.ex_05.model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;

@Repository
public class ProductRepository {

    private final SessionFactory sessionFactory;

    public ProductRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Product findByIdForUpdate(Long id) {
        return currentSession().find(Product.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    public Product save(Product product) {
        currentSession().persist(product);
        return product;
    }

    public List<Product> findAllWithVendor() {
        return currentSession()
                .createSelectionQuery("select p from Product p join fetch p.vendor order by p.id", Product.class)
                .getResultList();
    }

    public long countAll() {
        return currentSession().createSelectionQuery("select count(p.id) from Product p", Long.class).getSingleResult();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}


