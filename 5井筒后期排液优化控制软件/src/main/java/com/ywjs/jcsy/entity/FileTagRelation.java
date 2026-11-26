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
@ApiModel(value="FileTagRelation对象", description="")
public class FileTagRelation extends Model<FileTagRelation> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long fileId; // 文件id
    private Long fileNodeId; // 笔记id
    private Integer type; // 关联的类型（0：文件；1：文件笔记）
    private Long tagId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
