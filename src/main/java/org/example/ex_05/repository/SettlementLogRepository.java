package org.example.ex_05.repository;

import org.example.ex_05.model.VendorSettlementLog;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SettlementLogRepository {

    private final SessionFactory sessionFactory;

    public SettlementLogRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(VendorSettlementLog log) {
        currentSession().persist(log);
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}

