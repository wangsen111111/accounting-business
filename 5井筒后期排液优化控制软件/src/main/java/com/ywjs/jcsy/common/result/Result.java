package com.ywjs.jcsy.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "全局统一返回结果")
public class Result {

    @ApiModelProperty(value = "是否成功")
    private boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    // 响应中的数据
    @ApiModelProperty(value = "返回数据")
    private Object data;

    // @ApiModelProperty(value = "返回数据")
    // private Map<String, Object> data = new HashMap<String, Object>();

    public Result() {
    }

    public Result(ResultCode resultCode) {
        if (resultCode != null) {
            this.code = resultCode.getCode();
            this.message = resultCode.getMessage();
        }
    }

    public static Result ok() {
        Result r = new Result();
        r.setSuccess(true);
        r.setCode(ResultCode.OK.getCode());
        r.setMessage(ResultCode.OK.getMessage());
        return r;
    }

    private Result(Object data) {
        this.data = data;
    }

    public static Result error() {
        Result r = new Result();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR.getCode());
        r.setMessage(ResultCode.ERROR.getMessage());
        return r;
    }

    // 失败时候的调用
    public static Result error(ResultCode codeMsg) {
        return new Result(codeMsg);
    }

    public Result message(String message) {
        this.setMessage(message);
        return this;
    }

    public Result code(Integer code) {
        this.setCode(code);
        return this;
    }

    // public Result data(String key, Object value){
    // this.data.put(key, value);
    // return this;
    // }

    public Result data(Object obj) {
        this.setData(obj);
        return this;
    }

}
