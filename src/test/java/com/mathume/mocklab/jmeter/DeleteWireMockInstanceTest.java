package com.mathume.mocklab.jmeter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mathume.mocklab.dtos.MockService;
import com.mathume.mocklab.dtos.responses.MockServiceResponse;
import jodd.log.LoggerFactory;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterTestContext;
import org.apache.jmeter.threads.JMeterTestVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by sebastian on 3/11/16.
 */
public class DeleteWireMockInstanceTest {

    DeleteWireMockInstance instance;
    TestContext context;
    JMeterTestContext jmeterContext = new JMeterTestContext();
    SampleResult result;
    ObjectMapper mapper = new ObjectMapper();
    static final String route = "/services";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void beforeTest(){
        this.context = new TestContext();
        this.instance = new DeleteWireMockInstance();
        this.context.setParameter(CreateWireMockInstance.ApiKey, "someKey");
        this.context.setParameter(CreateWireMockInstance.ApiUser, "someUser");
        this.context.setParameter(CreateWireMockInstance.MockLabUrl, "http://localhost:" + wireMockRule.port() + route);
        this.context.setParameter(CreateWireMockInstance.ServiceIdVariable, "serviceId");
        this.instance.setupTest(context);
        this.jmeterContext.setVariables(new JMeterTestVariables());
        JMeterContextService.replaceContext(this.jmeterContext);
    }

    @Test
    public void runTest_not_successful_when_request_fails() throws Exception {
        result = this.instance.runTest(this.context);
        assertFalse(result.isSuccessful());
    }

    @Test
    public void runTest_hasSubresult() throws Exception {
        result = this.instance.runTest(this.context);
        assertTrue(result.getSubResults().length > 0);
    }

    @Test
    public void runTest_successfully() throws Exception {
        this.jmeterContext.getVariables().put("serviceId", "someServiceId");
        stubResponseOk();
        result = this.instance.runTest(this.context);
        assertTrue(result.isSuccessful());
        verify(deleteRequestedFor(urlEqualTo("/services/someServiceId")));
    }

    @Test
    public void runTest_successfully_allSamplersAreSuccessful() throws Exception {
        this.jmeterContext.getVariables().put("serviceId", "someServiceId");
        stubResponseOk();
        result = this.instance.runTest(this.context);
        assertTrue(result.isSuccessful());
        for(int i=0; i<result.getSubResults().length; i++){
            assertTrue(result.getSubResults()[i].isSuccessful());
        }
    }

    private void stubResponseOk() throws JsonProcessingException {
        stubFor(delete(urlEqualTo(route + "/someServiceId"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.OK.value())));
    }

    @After
    public void logSampleResult(){
        LoggerFactory.getLogger(DeleteWireMockInstanceTest.class).info(result.getResponseDataAsString());
    }
}