package com.tarun.TaskManagement.exception;



public class ApiResponseModel<Y> {

    private final boolean success;
    private final String message;
    private final int status;
    private final Y data;

    public ApiResponseModel(boolean success, String message, int status, Y data){
        this.success = success;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public Y getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ApiResponseModel{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", data=" + data +
                '}';
    }

    public String getMessage() {
        return message;
    }
}
