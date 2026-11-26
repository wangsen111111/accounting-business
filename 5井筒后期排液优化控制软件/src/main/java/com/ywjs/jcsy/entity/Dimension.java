package com.ywjs.jcsy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Dimension对象", description="")
public class Dimension implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "dimension_id", type = IdType.AUTO)
    private Long dimensionId;

    private String dimensionName;

    private String dimensionNote;


}
