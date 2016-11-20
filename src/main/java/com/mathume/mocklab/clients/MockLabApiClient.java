package com.mathume.mocklab.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathume.mocklab.dtos.MockService;
import com.mathume.mocklab.dtos.requests.MockServiceRequest;
import com.mathume.mocklab.dtos.responses.MockServiceResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * Created by sebastian on 2/11/16.
 */
public class MockLabApiClient {

    private String apiKey;
    private String apiUser;
    private String mockLabUrl;

    private SampleResult sampler;
    private HttpHeaders headers;
    private ObjectMapper mapper;

    public MockLabApiClient(String apiKey, String apiUser, String mockLabUrl, SampleResult sampler) {
        this.apiKey = apiKey;
        this.apiUser = apiUser;
        this.mockLabUrl = mockLabUrl;
        this.sampler = sampler;
        this.headers = new HttpHeaders();
        this.setHeaders();
        this.mapper = new ObjectMapper();
    }

    public ResponseEntity<MockServiceResponse> createService(String name) throws JsonProcessingException {
        MockService dto = new MockService();
        dto.setName(name);
        MockServiceRequest msRequest = new MockServiceRequest();
        msRequest.setMockService(dto);
        HttpEntity<MockServiceRequest> request = new HttpEntity<>(msRequest, this.headers);

        SampleResult subresult = new SampleResult();
        subresult.setSampleLabel(this.getMockLabUrl());
        subresult.setRequestHeaders(this.serialize(this.headers));

        RestTemplate template = new RestTemplate();

        subresult.sampleStart();
        ResponseEntity<MockServiceResponse> response = null;
        try {
            response = template.postForEntity(this.getMockLabUrl(), request, MockServiceResponse.class);
            subresult.sampleEnd();
            subresult.setSuccessful(true);
            subresult.setResponseCode(response.getStatusCode().value() + "");
            subresult.setResponseData(
                    this.serialize(this.getMockLabUrl()) + "\n" +
                            this.serialize(response.getHeaders()) + "\n" +
                            this.serialize(response.getBody().getMockService()), StandardCharsets.UTF_8.name());
        } catch (HttpStatusCodeException e) {
            subresult.setSuccessful(false);
            subresult.setResponseCode(e.getStatusCode().value() + "");
            subresult.setResponseMessage(e.getResponseBodyAsString());
            subresult.setResponseHeaders(this.serialize(e.getResponseHeaders()));
            subresult.sampleEnd();
        } finally {
            this.sampler.addRawSubResult(subresult);
        }
        return response;
    }

    public void deleteService(String serviceId) throws JsonProcessingException {
        String url = this.getMockLabUrl() + "/" + serviceId;
        HttpEntity<?> request = new HttpEntity<Object>(headers);

        SampleResult subresult = new SampleResult();
        subresult.setSampleLabel(url);
        subresult.setRequestHeaders(this.serialize(this.headers));

        RestTemplate template = new RestTemplate();

        subresult.sampleStart();

        try {

            template.exchange(url, HttpMethod.DELETE, request, String.class);
            subresult.sampleEnd();
            subresult.setSuccessful(true);
            subresult.setResponseCode("200");
        } catch (HttpStatusCodeException e) {
            subresult.setResponseCode(e.getStatusCode().value() + "");
            subresult.setResponseMessage(e.getResponseBodyAsString());
            subresult.setResponseHeaders(this.serialize(e.getResponseHeaders()));
            subresult.sampleEnd();
            subresult.setSuccessful(false);
        } finally {
            this.sampler.addRawSubResult(subresult);
        }
    }

    private String serialize(Object obj) throws JsonProcessingException {
        return this.mapper.writeValueAsString(obj);
    }

    private String getMockLabUrl() {
        return this.mockLabUrl;
    }

    private void setHeaders() {
        headers.add("Authorization", "Basic " + this.getBase64Credentials());
    }

    private String getBase64Credentials() {
        String plainCreds = this.apiUser + ":" + this.apiKey;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }
}
