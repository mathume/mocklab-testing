package com.mathume.mocklab.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sebastian on 3/11/16.
 */
public class TestContext extends JavaSamplerContext {

    private Map<String, String> mapParams = new HashMap<String, String>();

    public TestContext(){
        super(new Arguments());
    }

    public void setParameter(String name, String value){
        this.mapParams.put(name, value);
    }

    @Override
    public String getParameter(String name) {
        return this.mapParams.get(name);
    }

    @Override
    public Iterator<String> getParameterNamesIterator() {
        return this.mapParams.keySet().iterator();
    }
}
