package com.mathume.mocklab.jmeter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathume.com.mocklab.clients.MockLabApiClient;
import com.mathume.mocklab.dtos.responses.MockServiceResponse;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sebastian on 2/11/16.
 */
public class CreateWireMockInstance extends AbstractJavaSamplerClient {
    private Map<String, String> mapParams = new HashMap<String, String>();

    private static final String ApiKey = "ApiKey";
    private static final String ApiUser = "ApiUser";
    private static final String MockLabUrl = "MockLabUrl";

    public CreateWireMockInstance() {
        super();
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        for (Iterator<String> it = context.getParameterNamesIterator(); it.hasNext();) {
            String paramName =  it.next();
            mapParams.put(paramName, context.getParameter(paramName));
        }
    }

    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();

        try {
            result.sampleStart();

            MockLabApiClient client = new MockLabApiClient(mapParams.get(ApiKey), mapParams.get(ApiUser), mapParams.get(MockLabUrl));

            ResponseEntity<MockServiceResponse> response = client.createService("someName");

            if(response.getStatusCode() == HttpStatus.CREATED) {
                result.sampleEnd();


                result.setSuccessful(true);
                result.setSampleLabel("SUCCESS: ");// + student.getStudentname());
            }else{
                throw new Exception("Failed to create service " + response.getStatusCodeValue() + "\n" + new ObjectMapper().writeValueAsString(response.getBody()));
            }
        } catch (Throwable e) {
            result.sampleEnd();
            result.setSampleLabel("FAILED: '" + e.getMessage() + "' || " + e.toString());
            result.setSuccessful(false);

            e.printStackTrace();
            System.out.println("\n\n\n");
        }

        return result;
    }

    @Override
    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();

        params.addArgument(ApiUser, "mathume");
        params.addArgument(ApiKey, "edw");
        params.addArgument(MockLabUrl, "https://api.mocklab.io/v1/mock-services");
        return params;
    }
}
