package com.exl.rdaas.jwt.model;

import java.util.Set;

public class MessageResponse {
	
	private String message;
	private String username;
	private String tenantid;
	private String email;
	private Set<Role> roles;
	
	public MessageResponse() {
	}
	
	public MessageResponse(String message, String username, String tenantid, String email, Set<Role> roles) {
		this.message = message;
		this.username = username;
		this.tenantid = tenantid;
		this.email = email;
		this.roles = roles;
	}
	
	public MessageResponse(String message) {
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getTenantid() {
		return tenantid;
	}

	public void setTenantid(String tenantid) {
		this.tenantid = tenantid;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}