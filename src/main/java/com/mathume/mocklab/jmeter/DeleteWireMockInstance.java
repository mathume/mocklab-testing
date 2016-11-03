package com.mathume.mocklab.jmeter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathume.mocklab.clients.MockLabApiClient;
import com.mathume.mocklab.dtos.responses.MockServiceResponse;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sebastian on 2/11/16.
 */
public class DeleteWireMockInstance extends AbstractJavaSamplerClient {
    private Map<String, String> mapParams = new HashMap<String, String>();

    private static final String ApiKey = "ApiKey";
    private static final String ApiUser = "ApiUser";
    private static final String MockLabUrl = "MockLabUrl";
    private static final String ServiceIdVariable = "ServiceIdVariable";

    public DeleteWireMockInstance() {
        super();
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        for (Iterator<String> it = context.getParameterNamesIterator(); it.hasNext(); ) {
            String paramName = it.next();
            mapParams.put(paramName, context.getParameter(paramName));
        }
    }

    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        try {
            result.sampleStart();

            MockLabApiClient client = new MockLabApiClient(mapParams.get(ApiKey), mapParams.get(ApiUser), mapParams.get(MockLabUrl), result);

            String serviceId = JMeterContextService.getContext().getVariables().get(this.mapParams.get(ServiceIdVariable));
            client.deleteService(serviceId);

            result.sampleEnd();
            result.setSuccessful(true);
        } catch (Throwable e) {
            result.sampleEnd();
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
        params.addArgument(ServiceIdVariable, "serviceId");
        return params;
    }
}
