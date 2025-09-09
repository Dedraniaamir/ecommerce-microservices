package com.msproj.orderservice.dto;

import java.util.List;
import java.util.Map;

public class BulkOrderOperationDto {
    private String operation;
    private List<Long> orderIds;
    private Map<String, Object> parameters;

    // Getters and Setters
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public List<Long> getOrderIds() { return orderIds; }
    public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}
