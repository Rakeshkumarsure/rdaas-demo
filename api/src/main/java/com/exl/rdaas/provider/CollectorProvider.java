package com.exl.rdaas.provider;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.exl.rdaas.util.Connector;
import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class CollectorProvider<T> {

	public Mono<JsonNode> getScoreDetails(String pConnectorName, String pConnectorPayload, Connector provider,
			Class<?> obj, String serviceName) throws Exception {
		return this.callService(pConnectorName, pConnectorPayload, provider, obj, serviceName);
	}

	private Mono<JsonNode> callService(String pConnectorName, String pConnectorPayload, Connector provider,
			Class<?> clazz, String serviceName) throws Exception {

		provider.setServiceName(serviceName);
		WebClient client1 = WebClient.builder().baseUrl(provider.getBaseUrl()).build();
		return client1.method(HttpMethod.valueOf(provider.getHttpMethod())).uri(provider.getUri())
				.headers(httpHeaders -> {

					try {
						provider.getHeaders().forEach((k, v) -> {
							((HttpHeaders) httpHeaders).set(k, v);
						});
					} catch (Exception e) {
						e.printStackTrace();
					}

				}).body(BodyInserters.fromValue(pConnectorPayload))

				.exchangeToMono(response -> {
					if (response.statusCode() != null && (response.statusCode().isError())) {
						response.body((clientHttpResponse, context) -> {
							return response.bodyToMono(JsonNode.class);
						});
					} else {
						return response.bodyToMono(JsonNode.class);
					}
					return response.bodyToMono(JsonNode.class);
				});
	}

}
