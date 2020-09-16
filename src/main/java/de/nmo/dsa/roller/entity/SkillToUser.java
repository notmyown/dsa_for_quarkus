package de.nmo.dsa.roller.entity;

import javax.persistence.*;

/**
 * Datenhaltung in JPA für CodingCards
 * @author marco.bergen
 *
 */
@Entity(name="skilltouser")
@NamedQuery(name = SkillToUser.findAll,
		query = "SELECT s FROM skilltouser s",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@NamedQuery(name = SkillToUser.findAllByUser,
		query = "SELECT s FROM skilltouser s WHERE user = :id",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@NamedQuery(name = SkillToUser.findAllByUserAndSkill,
		query = "SELECT s FROM skilltouser s WHERE user = :id AND skill = :skill",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@Cacheable
public class SkillToUser {

	public static final String findAll = "SkillToUser.findAll";
	public static final String findAllByUser = "SkillToUser.findAllByUser";
	public static final String findAllByUserAndSkill = "SkillToUser.findAllByUserAndSkill";


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private long user;
	private long skill;
	private long value;

	public static String getFindAll() {
		return findAll;
	}

	public static String getFindAllByUser() {
		return findAllByUser;
	}

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

	public long getSkill() {
		return skill;
	}

	public void setSkill(long skill) {
		this.skill = skill;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
