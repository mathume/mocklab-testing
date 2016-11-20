package com.mathume.mocklab.jmeter;

import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sebastian on 20/11/16.
 */
public class SetupPerformanceTest extends AbstractJavaSamplerClient {
    private Map<String, String> mapParams = new HashMap<String, String>();

    public static final String WireMockProtocol = "WireMockProcotol";
    public static final String ServiceId = "ServiceId";
    public static final String WireMockHost = "WireMockHost";
    public static final String WireMockPort = "WireMockPort";
    public static final String BigFilePath = "BigFilePath";

    public static final String BigFileSetup = "BigFileSetup";
    public static final String UrlEqualToSetup = "UrlEqualToSetup";

    public SetupPerformanceTest() {
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
            WireMock wm = new WireMock(
                    mapParams.get(WireMockProtocol),
                    mapParams.get(ServiceId) + "." + mapParams.get(WireMockHost),
                    Integer.parseInt(mapParams.get(WireMockPort)));
            result.sampleStart();
            this.getLogger().info("Reset all mappings");
            wm.resetMappings();
            setBigFile(wm, result);
            setUrlEqualTo(wm, result);
            result.setSuccessful(true);
            SampleResult[] subResults = result.getSubResults();
            for (int i = 0; i < subResults.length; i++) {
                if (!subResults[i].isSuccessful()) {
                    result.setSuccessful(false);
                    break;
                }
            }
        } catch (Exception e) {
            result.setSuccessful(false);
            this.getLogger().error("ERROR");
            this.getLogger().error(e.getMessage());
        }

        result.sampleEnd();
        return result;
    }

    private void setUrlEqualTo(WireMock wm, SampleResult result) {
        if(Boolean.parseBoolean(mapParams.get(UrlEqualToSetup))) {
            SampleResult subResult = new SampleResult();
            try {
                subResult.sampleStart();
                this.getLogger().info("Set exact url match at /urlEqualTo");
                subResult.setSampleLabel("/urlEqualTo");
                wm.register(get(urlEqualTo("/urlEqualTo")).willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Accept", "application/json")
                                .withBody("{ \"response\": \"urlEqualTo\" }")
                ));
                subResult.setSuccessful(true);
            } catch (Exception e) {
                subResult.setSuccessful(false);
                this.getLogger().error(e.getMessage());
            } finally {
                subResult.sampleEnd();
                result.addRawSubResult(subResult);
            }
        }
    }

    private void setBigFile(WireMock wm, SampleResult result) {
        if(Boolean.parseBoolean(mapParams.get(BigFileSetup))) {
            SampleResult subResult = new SampleResult();
            try {
                subResult.sampleStart();
                subResult.setSampleLabel("/bigfile");
                this.getLogger().info("set mapping at /bigfile to return " + mapParams.get(BigFilePath));
                wm.register(get(urlEqualTo("/bigfile")).willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/octet-stream")
                                .withHeader("Content-Disposition", "attachment; filename=\"" + mapParams.get(BigFilePath) + "\"")
                                .withBody(
                                        new String(Files.readAllBytes(Paths.get(mapParams.get(BigFilePath)))))));
                subResult.setSuccessful(true);
            } catch (Exception e) {
                subResult.setSuccessful(false);
                this.getLogger().error(e.getMessage());
            } finally {
                subResult.sampleEnd();
                result.addRawSubResult(subResult);
            }
        }
    }

    @Override
    public Arguments getDefaultParameters() {

        Arguments params = new Arguments();

        params.addArgument(WireMockProtocol, "https");
        params.addArgument(ServiceId, "serviceId");
        params.addArgument(WireMockHost, "mocklab.io");
        params.addArgument(WireMockPort, "443");
        params.addArgument(BigFilePath, "./data/1M.random.txt");
        params.addArgument(UrlEqualToSetup, "true");
        params.addArgument(BigFileSetup, "true");
        return params;
    }
}
