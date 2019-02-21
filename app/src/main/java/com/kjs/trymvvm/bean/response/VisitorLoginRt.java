package com.kjs.trymvvm.bean.response;

/**
 * Created by Administrator on 2019/1/30.
 */

public class VisitorLoginRt {

    private String success;
    private String errorMsg;
    private String content;
    private int code;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "VisitorLoginRt{" +
                "success='" + success + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", content='" + content + '\'' +
                ", code=" + code +
                '}';
    }
}
