package com.exl.rdaas.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import com.exl.rdaas.exception.BaseException;
import com.exl.rdaas.model.AuthDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class KeyProvider implements AuthProvider {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ExperianJsonProfileProvider experianResourceLoader;

	@Autowired
	AuthDetails authDetails;

	
	@Override
	public String getAuthKey(String serviceName, String tenantId) throws Exception {

		WebClient client = WebClient.builder().baseUrl(authDetails.getTokenbaseurl(serviceName, tenantId))
				.defaultHeader(HttpHeaders.CONTENT_TYPE, authDetails.getContenttype(serviceName, tenantId))
				.defaultHeader("Grant_type", authDetails.getAuthtype(serviceName, tenantId)).build();

		UriSpec<RequestBodySpec> uriSpec = client.method(HttpMethod.resolve(authDetails.getHttpmethod(serviceName, tenantId)));
		RequestBodySpec bodySpec = uriSpec.uri(authDetails.getTokenuri(serviceName, tenantId));

		Object socureEntity = objectMapper.readValue(experianResourceLoader.getProperty(serviceName, "key", tenantId).toString(), Object.class);

		RequestHeadersSpec headersSpec = bodySpec.body(Mono.just(socureEntity), Object.class);

		ResponseSpec responseSpec = headersSpec.header(HttpHeaders.CONTENT_TYPE, authDetails.getContenttype(serviceName, tenantId))
				.retrieve();

		 String response = responseSpec
				.onStatus(httpStatus -> httpStatus.value() == 400,
						error -> Mono.error(new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, "error Body")))
				.bodyToMono(String.class).block();
		 JsonNode collectorNodes = objectMapper.readValue(response, JsonNode.class);
		 return collectorNodes.get("access_token").asText();
	}

}
