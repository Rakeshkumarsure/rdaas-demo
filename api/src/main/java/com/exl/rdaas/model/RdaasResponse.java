package com.exl.rdaas.model;

public class RdaasResponse {

	private String requestId;

	private String message;

	public RdaasResponse() {
		
	}
	public RdaasResponse(String requestId, String message) {
		super();
		this.requestId = requestId;
		this.message = message;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getMessage() {
		return message;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
