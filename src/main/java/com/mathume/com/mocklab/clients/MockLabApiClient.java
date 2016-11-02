package com.mathume.com.mocklab.clients;

import com.mathume.mocklab.dtos.MockService;
import com.mathume.mocklab.dtos.requests.MockServiceRequest;
import com.mathume.mocklab.dtos.responses.MockServiceResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by sebastian on 2/11/16.
 */
public class MockLabApiClient {

    private String apiKey;
    private String apiUser;
    private String mockLabUrl;

    private HttpHeaders headers;

    public MockLabApiClient(String apiKey, String apiUser, String mockLabUrl) {
        this.apiKey = apiKey;
        this.apiUser = apiUser;
        this.mockLabUrl = mockLabUrl;
        this.headers = new HttpHeaders();
        this.setHeaders();
    }

    public ResponseEntity<MockServiceResponse> createService(String name) {
        RestTemplate template = new RestTemplate();
        MockService dto = new MockService();
        dto.setName(name);
        MockServiceRequest msRequest = new MockServiceRequest();
        msRequest.setMockService(dto);
        HttpEntity<MockServiceRequest> request = new HttpEntity<>(msRequest, this.headers);
        return template.postForEntity(this.getMockLabUrl(), request, MockServiceResponse.class);
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
