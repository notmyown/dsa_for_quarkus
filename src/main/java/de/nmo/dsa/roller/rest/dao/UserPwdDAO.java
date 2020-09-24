package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.User;

public class UserPwdDAO {

	private final long id;
	private final String name;
	private final String username;
	private final String password;

	public UserPwdDAO() {
		this.id = -1;
		this.name = "Undefined";
		this.username = this.name;
		this.password = "";
	}

	public UserPwdDAO(User u) {
		super();
		this.id = u.getId();
		this.name = u.getName();
		this.username = u.getUsername();
		this.password = u.getPassword();
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

	public String getPassword() {
		return password;
	}
}
