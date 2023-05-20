package com.exl.rdaas.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey = "flzxsqcysyhljt";
    //validity in milliseconds
    private long validityInMs = 3600000;
    
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public long getValidityInMs() {
		return validityInMs;
	}
	public void setValidityInMs(long validityInMs) {
		this.validityInMs = validityInMs;
	}
    
    
}