package com.exl.rdaas.service;

import java.util.Map;

import com.exl.rdaas.exception.BaseException;
import com.exl.rdaas.util.Connector;

public interface BaseScoreService<T> {
	
	public Connector getConnectorType(Map<String, Connector> providers, String connectorName) throws BaseException;
}
