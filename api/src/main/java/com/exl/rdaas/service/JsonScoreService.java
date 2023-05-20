package com.exl.rdaas.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.exl.rdaas.exception.BaseException;
import com.exl.rdaas.jwt.model.Product;
import com.exl.rdaas.jwt.model.TenantDetails;
import com.exl.rdaas.jwt.repository.TenantDetailsRepository;
import com.exl.rdaas.model.ProviderNode;
import com.exl.rdaas.model.ProviderNodeBuilder;
import com.exl.rdaas.model.RdaasJsonResponse;
import com.exl.rdaas.provider.CollectorProvider;
import com.exl.rdaas.provider.Provider;
import com.exl.rdaas.util.Connector;
import com.exl.rdaas.util.RequestIdAspect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JsonScoreService implements IJsonScoreService {

	private final ObjectMapper objectMapper;
	//private final Map<String, Connector> rdaasProviders;
	private final CollectorProvider<Provider> collectorProvider;
	private final TenantDetailsRepository tenantDetailsRepository;
	private final ApplicationContext context;

	@Autowired
	public JsonScoreService(ObjectMapper objectMapper,
			CollectorProvider<Provider> collectorProvider, TenantDetailsRepository tenantDetailsRepository, ApplicationContext context) {
		super();
		this.objectMapper = objectMapper;
		this.tenantDetailsRepository = tenantDetailsRepository;
		this.collectorProvider = collectorProvider;
		this.context = context;
	}

	/*
	 * @Override public String jsonResponse(String payload) throws Exception {
	 * Map<String, Map<String, ProviderNode>> jsonNodesMap =
	 * extractJsonNodes(payload);
	 * 
	 * return objectMapper.writeValueAsString(new
	 * RdaasJsonResponse(RequestIdAspect.getRequestId(),
	 * this.jsonScore(jsonNodesMap),
	 * "Informative message to communicate the Intent")); }
	 */
	
	@Override
	public Mono<String> jsonResponse(String payload, String tenantid) throws Exception {
	    Map<String, Map<String, ProviderNode>> jsonNodesMap = extractJsonNodes(payload);

	    return jsonScore(jsonNodesMap, tenantid)
	            .map(resMap -> {
					try {
						return objectMapper.writeValueAsString(new RdaasJsonResponse(
						        RequestIdAspect.getRequestId(),
						        resMap,
						        "Informative message to communicate the Intent"
						));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					return null;
				});
	}


	private Map<String, Map<String, ProviderNode>> extractJsonNodes(String pPayload) throws Exception {
		JsonNode collectorNode = objectMapper.readValue(pPayload, JsonNode.class);
		Map<String, Map<String, ProviderNode>> connectorNodeMap = new HashMap<>();
		

		if (null != collectorNode && collectorNode.isObject()) {
			JsonNode productsNode = collectorNode.get("products");
			if (productsNode.isObject()) {

				productsNode.fields().forEachRemaining(entry -> {
					Map<String, ProviderNode> submap = new HashMap<>();
					String fieldName = entry.getKey();
					JsonNode fieldValues = entry.getValue();
					fieldValues.forEach(value -> {
						submap.put(value.asText(), new ProviderNodeBuilder().setJsonNode(collectorNode.get(fieldName).get(value.asText()))
								.setIncludeProviders(collectorNode.get("inlcudeProviderResponses").booleanValue())
								.build());
					});
					connectorNodeMap.put(fieldName, submap);
				});
			}
		}
		return connectorNodeMap;
	}

	/*
	 * private Map<String, Map<String, JsonNode>> jsonScore(Map<String, Map<String,
	 * ProviderNode>> jsonNodesMap) throws Exception { Map<String, Map<String,
	 * JsonNode>> resMap = new HashMap<>(); jsonNodesMap.forEach((connectorName,
	 * connectorPayloadMap) -> {
	 * 
	 * Map<String, JsonNode> updatedMap = new HashMap<>();
	 * connectorPayloadMap.entrySet().forEach(entry -> { ProviderNode providerNode =
	 * entry.getValue(); JsonNode jsonNode = providerNode.getJsonNode(); try {
	 * JsonNode scoreNode = collectorProvider .getScoreDetails(connectorName,
	 * objectMapper.writeValueAsString(jsonNode), getConnectorType(rdaasProviders,
	 * connectorName), JsonNode.class, entry.getKey()) .block();
	 * providerNode.setJsonNode(scoreNode); updatedMap.put(entry.getKey(), scoreNode
	 * ); } catch (Exception e) { e.printStackTrace(); } });
	 * resMap.put(connectorName, updatedMap); }); return resMap; }
	 */
	
	private Mono<Map<String, Map<String, JsonNode>>> jsonScore(Map<String, Map<String, ProviderNode>> jsonNodesMap, String tenantid) throws Exception {
	    return Flux.fromIterable(jsonNodesMap.entrySet())
	            .flatMap(connectorPayloadMapEntry -> {
	                String connectorName = connectorPayloadMapEntry.getKey();
	                Map<String, ProviderNode> connectorPayloadMap = connectorPayloadMapEntry.getValue();
	                Map<String, JsonNode> updatedMap = new HashMap<>();

	                return Flux.fromIterable(connectorPayloadMap.entrySet())
	                        .flatMap(entry -> {
	                            ProviderNode providerNode = entry.getValue();
	                            JsonNode jsonNode = providerNode.getJsonNode();
	                            try {
	                                return collectorProvider.getScoreDetails(connectorName,
	                                        objectMapper.writeValueAsString(jsonNode),
	                                        getConnectorType(fetchProducts(tenantid), connectorName),
	                                        JsonNode.class, entry.getKey())
	                                        .map(scoreNode -> {
	                                            providerNode.setJsonNode(scoreNode);
	                                            updatedMap.put(entry.getKey(), scoreNode);
	                                            return scoreNode;
	                                        });
	                            } catch (Exception e) {
	                                return Mono.error(e);
	                            }
	                        })
	                        .then(Mono.just(Map.entry(connectorName, updatedMap)));
	            })
	            .collectMap(Map.Entry::getKey, Map.Entry::getValue);
	}


	// Call to corridor

	@Override
	public Connector getConnectorType(Map<String, Connector> providers, String connectorName) throws BaseException {
		for (Map.Entry<String, Connector> entry : providers.entrySet()) {
			Connector provider = entry.getValue();
			if (provider.getClass().getSimpleName().equalsIgnoreCase(connectorName)) {
				return provider;
			}
		}
		return null;
	}

	private Map<String, Connector> fetchProducts(String tenantID) {
	    Map<String, Connector> connectorMap = new HashMap<>();
	    TenantDetails tenantDetails = tenantDetailsRepository.findByTenantid(tenantID);
	    if (tenantDetails != null) {
	        Set<Product> products = tenantDetails.getProducts();
	        for (Product product : products) {
	            Object lObj = context.getBean(product.getName());
	            if (lObj instanceof Connector) {
	                Connector lConnector = (Connector) lObj;
	                lConnector.setTenantId(tenantID);
	                connectorMap.put(product.getName(), lConnector);
	            }
	        }
	    }
	    return connectorMap;
	}
	
}
