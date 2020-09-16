package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.User;

public class UserListDAO {

	private final long id;
	private final String name;

	public UserListDAO() {
		this.id = -1;
		this.name = "Undefined";
	}

	public UserListDAO(User u) {
		super();
		this.id = u.getId();
		this.name = u.getName();
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
