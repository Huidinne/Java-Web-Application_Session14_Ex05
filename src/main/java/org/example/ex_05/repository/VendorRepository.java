package org.example.ex_05.repository;

import org.example.ex_05.model.Vendor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VendorRepository {

    private final SessionFactory sessionFactory;

    public VendorRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Vendor findById(Long id) {
        return currentSession().find(Vendor.class, id);
    }

    public Vendor save(Vendor vendor) {
        currentSession().persist(vendor);
        return vendor;
    }

    public List<Vendor> findAll() {
        return currentSession().createSelectionQuery("from Vendor v order by v.id", Vendor.class).getResultList();
    }

    public long countAll() {
        return currentSession().createSelectionQuery("select count(v.id) from Vendor v", Long.class).getSingleResult();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}


