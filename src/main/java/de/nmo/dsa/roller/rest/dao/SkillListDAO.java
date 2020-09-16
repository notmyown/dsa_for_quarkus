package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.Skill;

public class SkillListDAO {

	private final long id;
	private final String name;
	private final String category;
	private final String attributes;

	public SkillListDAO() {
		this.id = -1;
		this.name = "Undefined";
		this.category = "Undefined";
		this.attributes = "Undefined";
	}

	public SkillListDAO(Skill s) {
		super();
		this.id = s.getId();
		this.name = s.getName();
		this.category = s.getCategory();
		this.attributes = s.getAttributes();
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}
}
