package com.ywjs.jcsy.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.naming.Name;
import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-09-04 16:35
 */
@Data
@Accessors(chain = true)
public class CountVo implements Serializable {
    private String name;
    private Integer value;
}
