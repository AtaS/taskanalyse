package com.ptc.taskanalyse.models;

/**
 * Created by asasmaz on 22/05/2017.
 */
public class BooleanResponse {

    private Object result;

    public BooleanResponse() {}

    public BooleanResponse(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
