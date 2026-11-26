package com.ywjs.jcsy.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-17 15:54
 */
@Data
@Accessors(chain = true)
public class PageVo<T> {

    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    private List<T> data;

}
