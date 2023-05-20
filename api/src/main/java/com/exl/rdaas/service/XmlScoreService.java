package com.exl.rdaas.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.exl.rdaas.exception.BaseException;
import com.exl.rdaas.jwt.model.Product;
import com.exl.rdaas.jwt.model.TenantDetails;
import com.exl.rdaas.jwt.repository.TenantDetailsRepository;
import com.exl.rdaas.model.RdaasXmlResponse;
import com.exl.rdaas.provider.CollectorProvider;
import com.exl.rdaas.provider.Provider;
import com.exl.rdaas.util.Connector;
import com.exl.rdaas.util.Convertor;
import com.exl.rdaas.util.RequestIdAspect;
import com.exl.rdaas.util.XmlParserFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class XmlScoreService implements IXmlScoreService {

	private final Convertor conversionHandler;
	private final ObjectMapper objectMapper;
	private final CollectorProvider<Provider> collectorProvider;
	private final TenantDetailsRepository tenantDetailsRepository;
	private final ApplicationContext context;

	@Autowired
	public XmlScoreService(Convertor conversionHandler, ObjectMapper objectMapper,
			Map<String, Connector> rdaasProviders, CollectorProvider<Provider> collectorProvider,
			TenantDetailsRepository tenantDetailsRepository,
			ApplicationContext context) {
		super();
		this.conversionHandler = conversionHandler;
		this.objectMapper = objectMapper;
		this.tenantDetailsRepository = tenantDetailsRepository;
		this.collectorProvider = collectorProvider;
		this.context = context;
	}

	@Override
	public Mono<String> xmlResponse(String payload, String tenantid) throws Exception {

		Map<String, Map<String, Document>> lConnectorNodeMap = extractXmlNodes(payload);
		Mono<Map<String, Document>> docMap = fetchScore(lConnectorNodeMap, tenantid);
		return mergeDocuments(docMap);
	}

	private Map<String, Map<String, Document>> extractXmlNodes(String pPayload) throws Exception {

		Map<String, Map<String, Document>> map = new HashMap<>();

		InputSource inputSource = new InputSource(new StringReader(pPayload));
		DocumentBuilder docBuilder = XmlParserFactory.getDocumentBuilder();
		Document doc = docBuilder.parse(inputSource);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		NodeList productNodes = (NodeList) xpath.evaluate("//products/*", doc, XPathConstants.NODESET);

		XPath xPath = XPathFactory.newInstance().newXPath();

		for (int i = 0; i < productNodes.getLength(); i++) {
			Node productNode = productNodes.item(i);
			String outerMapKey = productNode.getNodeName();

			NodeList nodes = (NodeList) xPath.evaluate(
					String.format("//*[not(ancestor-or-self::products)]/%s", outerMapKey), doc, XPathConstants.NODESET);

			Element outerNode = (Element) nodes.item(0);
			NodeList innerNodes = outerNode.getChildNodes();
			Map<String, Document> productNodesMap = new HashMap<>();
			for (int j = 0; j < innerNodes.getLength(); j++) {
				Node node = innerNodes.item(j);
				if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
					Document innerDoc = docBuilder.newDocument();
					innerDoc.appendChild(innerDoc.importNode(node, true));
					productNodesMap.put(node.getNodeName(), innerDoc);
				}
			}
			map.put(outerMapKey, productNodesMap);
		}

		return map;
	}

	private Mono<Map<String, Document>> fetchScore(Map<String, Map<String, Document>> lConnectorNodeMap, String tenantid) throws Exception {
	    return Flux.fromIterable(lConnectorNodeMap.entrySet())
	            .flatMap(entry -> {
	            	//hardecoded for now. TU is not plugged-in yet. Hence, sending the request back as response.
	                if (entry.getKey().equals("transunion")) {
	                    try {
	                        Document document = mergeDocs(entry.getValue());
	                        return Mono.just(new AbstractMap.SimpleEntry<>(entry.getKey(), document));
	                    } catch (Exception e) {
	                        return Mono.error(e);
	                    }
	                } else {
	                    return Flux.fromIterable(entry.getValue().entrySet())
	                            .flatMap(subEntry -> {
	                                try {
	                                    JsonNode sourceJsonNode = conversionHandler.convertXmlToJson.apply(subEntry.getValue());
	                                    return Mono.zip(Mono.just(subEntry.getKey()), Mono.just(sourceJsonNode));
	                                } catch (Exception e) {
	                                    return Mono.error(e);
	                                }
	                            })
	                            .flatMap(tuple -> {
	                                String subkey = tuple.getT1();
	                                JsonNode sourceJsonNode = tuple.getT2();
	                                try {
										return collectorProvider.getScoreDetails(entry.getKey(), objectMapper.writeValueAsString(sourceJsonNode),
										        getConnectorType(fetchProducts(tenantid), entry.getKey()), JsonNode.class, subkey)
										        .map(responseJson -> {
										            try {
										                Document document = conversionHandler.convertJsonToXml.apply(responseJson);
										                return new AbstractMap.SimpleEntry<>(subkey, document);
										            } catch (Exception e) {
										                throw Exceptions.propagate(e);
										            }
										        })
										        .onErrorResume(e -> Mono.empty());
									} catch (JsonProcessingException e) {
										e.printStackTrace();
									} catch (BaseException e) {
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
									return null;
	                            })
	                            .collectMap(Map.Entry::getKey, Map.Entry::getValue)
	                            .map(subMap -> {
	                                try {
	                                    Document document = mergeDocs(subMap);
	                                    return new AbstractMap.SimpleEntry<>(entry.getKey(), document);
	                                } catch (Exception e) {
	                                    throw Exceptions.propagate(e);
	                                }
	                            });
	                }
	            })
	            .collectMap(Map.Entry::getKey, Map.Entry::getValue);
	}

	
	private Document mergeDocs(Map<String, Document> docMap) throws Exception {

		Document mergedDoc = XmlParserFactory.getDocumentBuilder().newDocument();
		Element root = mergedDoc.createElement("root");
		mergedDoc.appendChild(root);

		for (Map.Entry<String, Document> entry : docMap.entrySet()) {
			String nodeName = entry.getKey();
			Document document = entry.getValue();
			Element documentWrapper = mergedDoc.createElement(nodeName);
			root.appendChild(documentWrapper);
			NodeList nodes = document.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node importedNode = mergedDoc.importNode(nodes.item(i), true);
				documentWrapper.appendChild(importedNode);
			}
		}
		return mergedDoc;
	}

	private Mono<String> mergeDocuments(Mono<Map<String, Document>> docMapMono) throws Exception {
		return docMapMono.map(docMap -> {

			try {
				Document mergedDoc = XmlParserFactory.getDocumentBuilder().newDocument();
				Element root = mergedDoc.createElement("products");
				mergedDoc.appendChild(root);

				for (Map.Entry<String, Document> entry : docMap.entrySet()) {
					String key = entry.getKey();
					Document document = entry.getValue();
					Element documentWrapper = mergedDoc.createElement(key);
					root.appendChild(documentWrapper);
					NodeList nodes = document.getDocumentElement().getChildNodes();
					for (int i = 0; i < nodes.getLength(); i++) {
						Node importedNode = mergedDoc.importNode(nodes.item(i), true);
						documentWrapper.appendChild(importedNode);
					}
				}

				RdaasXmlResponse response = new RdaasXmlResponse(RequestIdAspect.getRequestId(), mergedDoc,
						"Informative message to communicate the Intent");

				JAXBContext context = JAXBContext.newInstance(RdaasXmlResponse.class);
				Marshaller m = context.createMarshaller();

				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw1 = new StringWriter();
				m.marshal(response, sw1);
				String sw = sw1.toString();
				return sw;
			}

			catch (Exception e) {

			}
			return null;
		}

		);
	}

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
