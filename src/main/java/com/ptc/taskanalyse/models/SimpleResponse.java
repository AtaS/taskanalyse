package com.ptc.taskanalyse.models;

/**
 * Created by asasmaz on 22/05/2017.
 */
public class SimpleResponse {
    private Object data;

    public SimpleResponse() {}
    public SimpleResponse(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
