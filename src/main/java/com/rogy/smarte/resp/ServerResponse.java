package com.rogy.smarte.resp;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * HTTP 请求返回的最外层对象
 * Created by vostor on 2018/10/26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse {
    //错误码
    private Integer status;
    //提示信息
    private String statusText;
    //具体内容
    private Object data;
    //userKey
    private String token;
    //数据总数
    private Long total;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 正常返回结果(结果集合)
     *
     * @param responseCode
     * @param object
     * @param total
     * @param token
     * @return
     */
    public static <T> ServerResponse success(ResponseCode responseCode, Object object, Long total, String token) {
        ServerResponse result = new ServerResponse();
        result.setStatus(responseCode.getCode());
        result.setStatusText(responseCode.getDesc());
        result.setData(object);
        result.setTotal(total);
        result.setToken(token);
        return result;
    }

    /**
     * 正常返回结果(单条结果)
     *
     * @param responseCode
     * @param object
     * @param token
     * @return
     */
    public static ServerResponse success(ResponseCode responseCode, Object object, String token) {
        return success(responseCode, object, null, token);
    }

    /**
     * 正常返回结果(不带结果)
     *
     * @param responseCode
     * @return
     */
    public static ServerResponse success(ResponseCode responseCode) {
        return success(responseCode, null, null);
    }

    /**
     * 自定义异常 带消息
     *
     * @param responseCode
     * @param msg
     * @return
     */
    public static ServerResponse customError(ResponseCode responseCode, String msg) {
        ServerResponse result = new ServerResponse();
        result.setStatus(responseCode.getCode());
        result.setStatusText(msg);
        return result;
    }

    /**
     * 自定义异常
     *
     * @param responseCode
     * @return
     */
    public static ServerResponse customError(ResponseCode responseCode) {
        return customError(responseCode, responseCode.getDesc());
    }
}
