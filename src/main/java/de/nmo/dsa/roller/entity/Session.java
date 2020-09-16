package de.nmo.dsa.roller.entity;

import javax.persistence.*;

/**
 * Datenhaltung in JPA für user
 * @author marco.bergen
 *
 */
@Entity(name="session")
@NamedQuery(name = Session.findAll,
		query = "SELECT s FROM session s ",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@NamedQuery(name = Session.findAllByUser,
		query = "SELECT s FROM session s WHERE user = :id",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@NamedQuery(name = Session.findByToken,
		query = "SELECT s FROM session s WHERE token = :id",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@Cacheable
public class Session {

	public static final String findAll = "Session.findAll";
	public static final String findAllByUser = "Session.findAllByUser";
	public static final String findByToken = "Session.findByToken";
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private long user;
	private String token;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
