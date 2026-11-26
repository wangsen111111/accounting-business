package com.ywjs.jcsy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
@ApiModel(value="Tag对象", description="")
public class Tag extends Model<Tag> {

    private static final long serialVersionUID=1L;

    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    private String tagName;

    private Long tagPid;

    @Override
    protected Serializable pkVal() {
        return this.tagId;
    }

}
