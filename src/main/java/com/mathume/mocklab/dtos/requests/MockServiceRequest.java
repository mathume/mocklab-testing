package com.mathume.mocklab.dtos.requests;

import com.mathume.mocklab.dtos.MockService;

/**
 * Created by sebastian on 2/11/16.
 */
public class MockServiceRequest {

    private MockService mockService;

    public MockService getMockService() {
        return mockService;
    }

    public void setMockService(MockService mockService) {
        this.mockService = mockService;
    }
}
