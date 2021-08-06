package com.turing.codingsample.somesample.datapath;

//some of the imports are removed
import org.apache.http.HttpEntity;

/**
 * This class is responsible for making request URL and parsing the the response returned by DummyService
 */
public interface DatapathRequestProcessor {
    /**
     * This method takes input parameter and builds the URL to hit the datapath gateway
     * @param input consist of asin and marketplace
     * @return string representation of URL
     */
    String buildRequestUrl(CatalogItemRequest input);

    /**
     * This method accepts httpEntity and further converts it to Ion to extract asin attributes
     * During the extraction process if some of the attrbutes are not present in response
     * we build the AsinAttributes with default value.
     * @param input consist of asin and marketplace
     * @param httpEntity response returned by DummyService
     * @return AsinAttributes
     */
    AsinAttributes buildOutput(CatalogItemRequest input, HttpEntity httpEntity);
}
