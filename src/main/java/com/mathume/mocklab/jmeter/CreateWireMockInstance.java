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
public class CreateWireMockInstance extends AbstractJavaSamplerClient {
    private Map<String, String> mapParams = new HashMap<String, String>();

    public static final String ApiKey = "ApiKey";
    public static final String ApiUser = "ApiUser";
    public static final String MockLabUrl = "MockLabUrl";
    public static final String ServiceIdVariable = "ServiceIdVariable";

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

            MockLabApiClient client = new MockLabApiClient(mapParams.get(ApiKey), mapParams.get(ApiUser), mapParams.get(MockLabUrl), result);

            ResponseEntity<MockServiceResponse> response = client.createService(UUID.randomUUID().toString());

            if(response.getStatusCode() == HttpStatus.CREATED) {
                result.sampleEnd();
                result.setSuccessful(true);
                SampleResult[] subresults = result.getSubResults();
                for(int i=0; i<subresults.length; i++){
                    if(!subresults[i].isSuccessful()){
                        result.setSuccessful(false);
                        break;
                    }
                }
                result.setSamplerData("ServiceId saved into variable " + this.mapParams.get(ServiceIdVariable));
                String id = response.getBody().getMockService().getId();
                JMeterContextService.getContext().getVariables().put(this.mapParams.get(ServiceIdVariable), id);
                this.getLogger().info("Saved service id " + id + " to variable " + this.mapParams.get(ServiceIdVariable));
            }else{
                result.sampleEnd();
                throw new Exception("Failed to create service " + response.getStatusCodeValue() + "\n " + new ObjectMapper().writeValueAsString(response.getBody()));
            }
        } catch (Throwable e) {
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
