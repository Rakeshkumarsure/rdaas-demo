package com.exl.rdaas.util;

public interface AuthProvider {
	
	public String getAuthKey(String serviceName, String tenantId) throws Exception;

}
