package de.nmo.dsa.roller.entity;

import javax.persistence.*;

/**
 * Datenhaltung in JPA für user
 * @author marco.bergen
 *
 */
@Entity(name="user")
@NamedQuery(name = User.findAll,
		query = "SELECT u FROM user u",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@NamedQuery(name = User.findByName,
		query = "SELECT u FROM user u where name = :id",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@Cacheable
public class User {

	public static final String findAll = "User.findAll";
	public static final String findByName = "User.findByName";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String name;
	private String password;

	private boolean admin;

	private long attr_mu;
	private long attr_kl;
	private long attr_in;
	private long attr_ch;
	private long attr_ff;
	private long attr_ge;
	private long attr_ko;
	private long attr_kk;

	public static String getFindAll() {
		return findAll;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public long getAttr_mu() {
		return attr_mu;
	}

	public void setAttr_mu(long attr_mu) {
		this.attr_mu = attr_mu;
	}

	public long getAttr_kl() {
		return attr_kl;
	}

	public void setAttr_kl(long attr_kl) {
		this.attr_kl = attr_kl;
	}

	public long getAttr_in() {
		return attr_in;
	}

	public void setAttr_in(long attr_in) {
		this.attr_in = attr_in;
	}

	public long getAttr_ch() {
		return attr_ch;
	}

	public void setAttr_ch(long attr_ch) {
		this.attr_ch = attr_ch;
	}

	public long getAttr_ff() {
		return attr_ff;
	}

	public void setAttr_ff(long attr_ff) {
		this.attr_ff = attr_ff;
	}

	public long getAttr_ge() {
		return attr_ge;
	}

	public void setAttr_ge(long attr_ge) {
		this.attr_ge = attr_ge;
	}

	public long getAttr_ko() {
		return attr_ko;
	}

	public void setAttr_ko(long attr_ko) {
		this.attr_ko = attr_ko;
	}

	public long getAttr_kk() {
		return attr_kk;
	}

	public void setAttr_kk(long attr_kk) {
		this.attr_kk = attr_kk;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				", admin=" + admin +
				", attr_mu=" + attr_mu +
				", attr_kl=" + attr_kl +
				", attr_in=" + attr_in +
				", attr_ch=" + attr_ch +
				", attr_ff=" + attr_ff +
				", attr_ge=" + attr_ge +
				", attr_ko=" + attr_ko +
				", attr_kk=" + attr_kk +
				'}';
	}
}
