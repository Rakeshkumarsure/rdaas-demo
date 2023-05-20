package com.exl.rdaas.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("rdaas")
public class RdaasJsonResponse extends RdaasResponse {

	
	private Object body;

	public RdaasJsonResponse(String requestId, Object body, String message) {
		super(requestId, message);
		this.body = body;
	}

	public RdaasJsonResponse() {
	}

	 @JsonGetter("products")
	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	
}
