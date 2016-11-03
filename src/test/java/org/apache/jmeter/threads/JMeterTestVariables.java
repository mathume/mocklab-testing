package org.apache.jmeter.threads;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastian on 3/11/16.
 */
public class JMeterTestVariables extends JMeterVariables {
    private final Map<String, Object> vars = new HashMap();

    public JMeterTestVariables(){

    }

    @Override
    public void put(String key, String value) {
        this.vars.put(key, value);
    }

    @Override
    public String get(String key) {
        return (String)this.vars.get(key);
    }
}
