package com.exl.rdaas.jwt.model;

import java.util.List;

public class JwtResponse {
	private String token;
	private String username;
	private String tenantid;
	private String email;
	private List<String> roles;

	public JwtResponse(String accessToken, String username, String tenantid, String email, List<String> roles) {
		this.token = accessToken;
		this.username = username;
		this.tenantid = tenantid;
		this.email = email;
		this.roles = roles;
	}
	
	public JwtResponse(String username, String tenantid, String email, List<String> roles) {
		this.username = username;
		this.tenantid = tenantid;
		this.email = email;
		this.roles = roles;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
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

	public List<String> getRoles() {
		return roles;
	}
}