package com.archgeek.data.meta.resp;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pizhihui
 * @date 2024-10-29 10:58
 */
public class Message {

    public enum MessageStatus {

        SUCCESS(0, "SUCCESS"),
        ERROR(1, "ERROR"),
        WARNING(4, "WARNING"),
        NO_LOGIN(-1, "NO_LOGIN");

        private int code;
        private String msg;

        MessageStatus(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }


    private String method;
    private int status;
    private String message;
    private Object data;
    private String stack;

    public Message() {
        this.status = 0;
    }

    public static Message error(Throwable t) {
        return error(ExceptionUtils.getRootCauseMessage(t), t);
    }

    public static Message error(String msg, Throwable t) {
        return error(ExceptionUtils.getRootCauseMessage(t), t, MessageStatus.ERROR.code);
    }

    public static Message error(String msg, Throwable t, int status) {
        Message res = new Message();
        res.setStatus(status);
        res.setMessage(msg);
        if (null != t) {
            res.setStack(ExceptionUtils.getStackTrace(t));
        }
        return res;
    }

    public static Message ok() {
        return ok("OK");
    }

    public static Message ok(String msg) {
        Message res = new Message();
        if (null != msg && !msg.isEmpty()) {
            res.setMessage(msg);
        } else {
            res.setMessage("OK");
        }
        return res;
    }

    public Message setMap(String key, Object value) {
        Map<String, Object> res = new HashMap<>();
        res.put(key, value);
        this.data = res;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Message setData(Object data) {
        this.data = data;
        return this;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }


    public static class MessageBuilder {
        private int status;

        private MessageBuilder(){}

        public Message build() {
            Message msg = new Message();
            msg.setStatus(this.status);
            return msg;
        }

        public MessageBuilder status(int status) {
            this.status = status;
            return this;
        }
    }


}
