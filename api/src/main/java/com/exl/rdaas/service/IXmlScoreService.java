package com.exl.rdaas.service;

import com.exl.rdaas.model.RdaasXmlResponse;

import reactor.core.publisher.Mono;

public interface IXmlScoreService extends BaseScoreService<RdaasXmlResponse>{
	
	public Mono<String>  xmlResponse(String payload, String tenantid) throws Exception;

}
