package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.ChatMessage;

public class ChatMessageListDAO {

	private final long id;
	private final String msg;
	private final String room;
	private final long time;

	public ChatMessageListDAO(ChatMessage u) {
		super();
		this.id = u.getId();
		this.msg = u.getMessage();
		this.room = u.getRoom();
		this.time = u.getTime();
	}

	public long getId() {
		return id;
	}

	public String getMsg() {
		return msg;
	}

	public String getRoom() {
		return room;
	}

	public long getTime() {
		return time;
	}
}
