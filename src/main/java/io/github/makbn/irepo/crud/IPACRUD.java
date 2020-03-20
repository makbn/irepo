package io.github.makbn.irepo.crud;

import io.github.makbn.irepo.exception.ResourceNotFoundException;
import io.github.makbn.irepo.model.IPA;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class IPACRUD {

    private final SessionFactory session;

    @Autowired
    public IPACRUD(SessionFactory session) {
        this.session = session;
    }

    @Transactional
    public IPA getByUUID(String uuid, boolean loadInfo){
        IPA ipa = session.getCurrentSession().createQuery("FROM IPA WHERE uuid = :uuid",IPA.class)
                .setParameter("uuid", uuid)
                .uniqueResult();

        if(ipa == null)
            throw  new ResourceNotFoundException("no ipa assigned with this id");
        if(loadInfo)
            Hibernate.initialize(ipa.getIpaInfo());
        return ipa;
    }

    @Transactional
    public long save(IPA ipa) {
        Session cSession = session.getCurrentSession();
        long id = (Long) cSession.save(ipa);
        return id;
    }
}
