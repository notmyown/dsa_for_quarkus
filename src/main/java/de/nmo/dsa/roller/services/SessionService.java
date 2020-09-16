package de.nmo.dsa.roller.services;

import de.nmo.dsa.roller.entity.Session;
import de.nmo.dsa.roller.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SessionService implements DataService<Session> {
	
	@Inject
    private EntityManager entityManager;

	@Override
	public Session get(long id) {
		try {
			return entityManager.find(Session.class, id);
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public List<Session> all() {
		return entityManager.createNamedQuery(Session.findAll, Session.class)
				.getResultList();
	}

	@Override
	@Transactional
	public Session create(Session u) {
		entityManager.persist(u);
		return u;
	}

	@Transactional
	public Session create(User u) {
		Session s = new Session();
		s.setUser(u.getId());
		s.setToken(UUID.randomUUID().toString());
		entityManager.persist(s);
		return s;
	}

	@Override
	@Transactional
	public boolean update(long id, Session s) {
		return false;
	}

	@Override
	@Transactional
	public Session delete(Session s) {
		Session sdel = get(s.getId());
		if (sdel != null) {
			entityManager.remove(sdel);
		}
		return s;
	}

	public Session getByUser(User u) {
		try {
			return entityManager.createNamedQuery(Session.findAllByUser, Session.class).setParameter("id", u.getId())
					.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public Session getByToken(String t) {
		try {
			return entityManager.createNamedQuery(Session.findByToken, Session.class).setParameter("id", t)
					.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
