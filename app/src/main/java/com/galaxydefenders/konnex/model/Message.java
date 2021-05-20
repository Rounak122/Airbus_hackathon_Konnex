package com.galaxydefenders.konnex.model;

import java.io.Serializable;

public class Message implements Serializable {
	private long id;
	private String content;
	private boolean fromMe;
	private boolean showTime = true;

	public Message(long id, String content, boolean fromMe) {
		this.id = id;
		this.content = content;
		this.fromMe = fromMe;
	}

	public Message(long id, String content, boolean fromMe, boolean showTime) {
		this.id = id;
		this.content = content;
		this.fromMe = fromMe;
		this.showTime = showTime;
	}

	public long getId() {
		return id;
	}


	public String getContent() {
		return content;
	}

	public boolean isFromMe() {
		return fromMe;
	}

	public boolean isShowTime() {
		return showTime;
	}
}