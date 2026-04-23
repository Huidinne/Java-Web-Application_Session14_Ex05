package org.example.ex_05.repository;

import org.example.ex_05.model.Wallet;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WalletRepository {

    private final SessionFactory sessionFactory;

    public WalletRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Wallet findByIdForUpdate(Long id) {
        return currentSession().find(Wallet.class, id, LockMode.PESSIMISTIC_WRITE);
    }

    public Wallet findById(Long id) {
        return currentSession().find(Wallet.class, id);
    }

    public Wallet save(Wallet wallet) {
        currentSession().persist(wallet);
        return wallet;
    }

    public List<Wallet> findAll() {
        return currentSession().createSelectionQuery("from Wallet w order by w.id", Wallet.class).getResultList();
    }

    public long countAll() {
        return currentSession().createSelectionQuery("select count(w.id) from Wallet w", Long.class).getSingleResult();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}


