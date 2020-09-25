package de.nmo.dsa.roller.entity;

import javax.persistence.*;

/**
 * Datenhaltung in JPA für user
 * @author marco.bergen
 *
 */
@Entity(name="chatmessage")
@NamedQuery(name = ChatMessage.findAll,
		query = "SELECT c FROM chatmessage c",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@NamedQuery(name = ChatMessage.findByRoom,
		query = "SELECT u FROM chatmessage u where room = :room ORDER BY time",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@NamedQuery(name = ChatMessage.findByRoomSince,
		query = "SELECT u FROM chatmessage u where room = :room and time > :time ORDER BY time",
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true") )
@Cacheable
public class ChatMessage {

	public static final String findAll = "ChatMessage.findAll";
	public static final String findByRoom = "ChatMessage.findByRoom";
	public static final String findByRoomSince = "ChatMessage.findByRoomSince";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Lob
	private String message;
	private String room;
	private long time;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "ChatMessage{" +
				"id=" + id +
				", message='" + message + '\'' +
				", room='" + room + '\'' +
				", time=" + time +
				'}';
	}
}
