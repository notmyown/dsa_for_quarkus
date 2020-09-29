package de.nmo.dsa.roller.services;

import de.nmo.dsa.roller.entity.Skill;
import de.nmo.dsa.roller.entity.SkillToUser;
import de.nmo.dsa.roller.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class SkillToUserService implements DataService<SkillToUser> {
	
	@Inject
    private EntityManager entityManager;

	@Override
	public SkillToUser get(long id) {
		try {
			return entityManager.find(SkillToUser.class, id);
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Transactional
	public List<SkillToUser> allByUser(User u) {
		return entityManager.createNamedQuery(SkillToUser.findAllByUser, SkillToUser.class).setParameter("id", u.getId())
				.getResultList();
	}

	@Override
	@Transactional
	public List<SkillToUser> all() {
		return entityManager.createNamedQuery(SkillToUser.findAll, SkillToUser.class)
				.getResultList();
	}

	@Override
	@Transactional
	public SkillToUser create(SkillToUser u) {
		entityManager.persist(u);
		return u;
	}

	@Override
	@Transactional
	public boolean update(long id, SkillToUser u) {
		SkillToUser entity = entityManager.find(SkillToUser.class, u.getId());
		entity.setUser(u.getUser());
		entity.setValue(u.getValue());
		entity.setSkill(u.getSkill());
		entityManager.persist(entity);
		return true;
	}

	@Override
	@Transactional
	public SkillToUser delete(SkillToUser u) {
		entityManager.remove(u);
		return u;
	}

}
