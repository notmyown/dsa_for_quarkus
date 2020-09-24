package de.nmo.dsa.roller.services;

import de.nmo.dsa.roller.entity.ChatMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ChatMessageService implements DataService<ChatMessage> {

	final static int PAGESIZE = 50;
	
	@Inject
    private EntityManager entityManager;

	@Override
	public ChatMessage get(long id) {
		try {
			return entityManager.find(ChatMessage.class, id);
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Transactional
	public List<ChatMessage> getByRoom(String name, int page) {
		try {
			return entityManager.createNamedQuery(ChatMessage.findByRoom, ChatMessage.class).setParameter("room", name).setFirstResult(page*PAGESIZE).setMaxResults(PAGESIZE).getResultList();
		} catch (NoResultException nre) {
			System.err.println(nre);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public List<ChatMessage> all() {
		return entityManager.createNamedQuery(ChatMessage.findAll, ChatMessage.class)
				.getResultList();
	}

	@Override
	@Transactional
	public ChatMessage create(ChatMessage u) {
		entityManager.persist(u);
		return u;
	}

	@Override
	@Transactional
	public boolean update(long id, ChatMessage u) {
		return false;
	}

	@Override
	@Transactional
	public ChatMessage delete(ChatMessage u) {
		entityManager.remove(u);
		return u;
	}

}
