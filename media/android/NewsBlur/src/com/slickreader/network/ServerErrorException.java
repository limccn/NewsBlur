package com.slickreader.network;

public class ServerErrorException extends Exception {
	
	public ServerErrorException(String errorMessage) {
		super(errorMessage);
	}
}
