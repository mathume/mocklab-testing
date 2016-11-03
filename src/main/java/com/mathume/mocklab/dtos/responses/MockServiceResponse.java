package com.mathume.mocklab.dtos.responses;

import com.mathume.mocklab.dtos.MockService;

/**
 * Created by sebastian on 2/11/16.
 */
public class MockServiceResponse {
    public MockService getMockService() {
        return mockService;
    }

    public void setMockService(MockService mockService) {
        this.mockService = mockService;
    }

    private MockService mockService;
}
