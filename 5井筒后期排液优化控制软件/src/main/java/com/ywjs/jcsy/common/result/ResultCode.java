package com.ywjs.jcsy.common.result;

public enum   ResultCode {

    OK(20000,"成功"),
    ERROR(20001,"失败"),
    BIND_ERROR(500101, "参数校验异常：%s"),
    FILE_CHECK_ERROR(30000,"压缩文件必须为Zip或rar格式"),
    EXISTS_ERROR(30001,"已存在"),
    FILE_NOT_EXIST_ERROR(30002,"文件不存在")
    ;

    private int code ;
    private String message ;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Result enumToResult(ResultCode rc){
        Result result = new Result();
        result.setCode(rc.getCode());
        result.setMessage(rc.getMessage());
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }


}
