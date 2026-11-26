package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.File;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-03 18:21
 */
@Data
public class FileDetailVo extends File implements Serializable {
    List<String> tagName;
}
