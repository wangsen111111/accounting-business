package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.Operation;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-09-09 17:48
 */
@Data
public class OperationVo extends Operation implements Serializable {
    private String key;
}
