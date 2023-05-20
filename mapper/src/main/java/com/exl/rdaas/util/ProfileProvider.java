package com.exl.rdaas.util;

import java.io.IOException;

public interface ProfileProvider {

	void loader() throws IOException;

	public Object getProperty(String serviceName, String key, String tenantId) throws Exception;
}