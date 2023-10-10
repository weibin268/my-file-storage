package com.zhuang.filestorage.model;

import java.io.Serializable;

public class ApiResult<T> implements Serializable {

    private int code = 0;
    private String msg;
    private T data;

    public static ApiResult alert(String msg) {
        ApiResult result = new ApiResult();
        result.setCode(-1);
        result.setMsg(msg);
        return result;
    }

    public static ApiResult error(String msg) {
        ApiResult result = new ApiResult();
        result.setCode(1);
        result.setMsg(msg);
        return result;
    }

    public static ApiResult error(int code, String msg) {
        ApiResult result = new ApiResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> ApiResult<T> error(String msg, T data) {
        ApiResult<T> result = new ApiResult();
        result.setCode(1);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> ApiResult<T> error(int code, String msg, T data) {
        ApiResult<T> result = new ApiResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static ApiResult success() {
        return new ApiResult();
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setData(data);
        return result;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
