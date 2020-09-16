package de.nmo.dsa.roller.services;

import de.nmo.dsa.roller.entity.Skill;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class SkillService implements DataService<Skill> {
	
	@Inject
    private EntityManager entityManager;

	@Override
	public Skill get(long id) {
		try {
			return entityManager.find(Skill.class, id);
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Transactional
	public Skill add(Skill s) {
		if (get(s.getId()) == null) {
			s.setId(0);
			create(s);
		}
		return get(s.getId());
	}

	@Override
	public List<Skill> all() {
		return entityManager.createNamedQuery(Skill.findAll, Skill.class)
				.getResultList();
	}

	@Override
	@Transactional
	public Skill create(Skill u) {
		entityManager.persist(u);
		return u;
	}

	@Override
	@Transactional
	public boolean update(long id, Skill u) {
		Skill entity = entityManager.find(Skill.class, u.getId());
		entityManager.persist(entity);
		return true;
	}

	@Override
	@Transactional
	public Skill delete(Skill u) {
		entityManager.remove(u);
		return u;
	}

}
