package de.nmo.dsa.roller.entity;

import javax.persistence.*;

/**
 * Datenhaltung in JPA für user
 * @author marco.bergen
 *
 */
@Entity(name="skill")
@NamedQuery(name = Skill.findAll,
		query = "SELECT s FROM skill s",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@Cacheable
public class Skill {

	public static final String findAll = "Skill.findAll";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String name;
	private String category;
	private String attributes;

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

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
