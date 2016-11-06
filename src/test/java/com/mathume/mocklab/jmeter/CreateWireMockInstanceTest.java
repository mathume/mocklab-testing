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
import org.junit.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by sebastian on 3/11/16.
 */
public class CreateWireMockInstanceTest {

    CreateWireMockInstance instance;
    TestContext context;
    JMeterTestContext jmeterContext = new JMeterTestContext();
    SampleResult result;
    MockService someMockService = new MockService();
    MockServiceResponse someResponse = new MockServiceResponse();
    ObjectMapper mapper = new ObjectMapper();
    static final String route = "/services";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void beforeTest(){
        this.someMockService.setDomains(new String[0]);
        this.someMockService.setName("");
        this.someMockService.setLinks(new HashMap<>());
        this.someMockService.setOwnerId("");
        this.someResponse.setMockService(this.someMockService);
        this.context = new TestContext();
        this.instance = new CreateWireMockInstance();
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
    public void runTest_successfully_setsServiceId() throws Exception {
        someMockService.setId("someId");
        stubResponseOk();
        result = this.instance.runTest(this.context);
        assertTrue(result.isSuccessful());
        assertTrue(JMeterContextService.getContext().getVariables()
                .get(this.context.getParameter(CreateWireMockInstance.ServiceIdVariable)).equals(someMockService.getId()));
    }

    @Test
    public void runTest_successfully_allSamplersAreSuccessful() throws Exception {
        someMockService.setId("someId");
        stubResponseOk();
        result = this.instance.runTest(this.context);
        assertTrue(result.isSuccessful());
        for(int i=0; i<result.getSubResults().length; i++){
            assertTrue(result.getSubResults()[i].isSuccessful());
        }
    }

    private void stubResponseOk() throws JsonProcessingException {
        stubFor(post(urlEqualTo(route))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.CREATED.value())
                        .withBody(mapper.writeValueAsString(someResponse))));
    }

    @After
    public void logSampleResult(){
        LoggerFactory.getLogger(CreateWireMockInstanceTest.class).info(result.getResponseDataAsString());
    }
}