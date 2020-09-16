package de.nmo.dsa.roller.services;

import de.nmo.dsa.roller.entity.SkillToUser;
import de.nmo.dsa.roller.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserService implements DataService<User> {
	
	@Inject
    private EntityManager entityManager;

	@Override
	public User get(long id) {
		try {
			return entityManager.find(User.class, id);
		} catch (NoResultException nre) {
			return null;
		}
	}

	public User getByName(String name) {
		System.out.println("Get User By Name:" + name + " - " + entityManager);
		try {
			List<User> us = entityManager.createNamedQuery(User.findByName, User.class).setParameter("id", name).getResultList();
			System.out.println(us.size());
			System.out.println();
			return us.get(0);
		} catch (NoResultException nre) {
			System.err.println(nre);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	@Override
	public List<User> all() {
		return entityManager.createNamedQuery(User.findAll, User.class)
				.getResultList();
	}

	@Override
	@Transactional
	public User create(User u) {
		entityManager.persist(u);
		return u;
	}

	@Override
	@Transactional
	public boolean update(long id, User u) {
		User entity = entityManager.find(User.class, u.getId());
		entity.setName(u.getName());
		entity.setAttr_mu(u.getAttr_mu());
		entity.setAttr_ch(u.getAttr_ch());
		entity.setAttr_ko(u.getAttr_ko());
		entity.setAttr_kl(u.getAttr_kl());
		entity.setAttr_kk(u.getAttr_kk());
		entity.setAttr_ge(u.getAttr_ge());
		entity.setAttr_in(u.getAttr_in());
		entity.setAttr_ff(u.getAttr_ff());
		entity.setAdmin(u.isAdmin());
		entityManager.persist(entity);
		return true;
	}

	@Override
	@Transactional
	public User delete(User u) {
		entityManager.remove(u);
		return u;
	}

	@Transactional
	public void updateSkill(User u, long skill, long value) {
		SkillToUser s = null;
		try {
			s = entityManager.createNamedQuery(SkillToUser.findAllByUserAndSkill, SkillToUser.class).setParameter("id", u.getId()).setParameter("skill", skill)
					.getSingleResult();
		} catch (NoResultException nre) {
			s = new SkillToUser();
			s.setUser(u.getId());
			s.setSkill(skill);
		}
		s.setValue(value);
		entityManager.persist(s);
	}
}
