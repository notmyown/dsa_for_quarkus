package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.User;

public class UserListDAO {

	private final long id;
	private final String name;
	private final String username;
	private final long mod;

	public UserListDAO() {
		this.id = -1;
		this.name = "Undefined";
		this.username = this.name;
		this.mod = 0;
	}

	public UserListDAO(User u) {
		super();
		this.id = u.getId();
		this.name = u.getName();
		this.username = u.getUsername();
		this.mod = u.getMod();
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

	public long getMod() {
		return mod;
	}
}
