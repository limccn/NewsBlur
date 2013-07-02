package com.slickreader.network.domain;

public class Message {

	// {"message": "Overloaded, no autocomplete results.", "code": -1, "authenticated": true, "result": "ok"}
	
	public String message;
	public int code;
	public boolean authenticated;
	public String result; 
}
