package com.exl.rdaas.provider;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public interface ResponseEntityProvider {

	public Mono<ResponseEntity<String>> createResponseEntity(String payload, String contentType, String tenantid) throws Exception;
}
