package com.exl.rdaas.service;

import com.exl.rdaas.model.RdaasJsonResponse;

import reactor.core.publisher.Mono;

public interface IJsonScoreService extends BaseScoreService<RdaasJsonResponse> {
	
	public Mono<String> jsonResponse(String payload, String tenantid) throws Exception;

}
