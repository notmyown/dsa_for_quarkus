package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.User;

public class UserListDAO {

	private final long id;
	private final String name;
	private final String username;

	public UserListDAO() {
		this.id = -1;
		this.name = "Undefined";
		this.username = this.name;
	}

	public UserListDAO(User u) {
		super();
		this.id = u.getId();
		this.name = u.getName();
		this.username = u.getUsername();
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}
}
