package com.ywjs.jcsy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value="Project对象", description="")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project extends Model<Project> {

    private static final long serialVersionUID=1L;

    @TableId(value = "project_id", type = IdType.AUTO)
    private Long projectId;             // 项目id

    private String projectName;         // 项目名称

    private String note;                // 项目介绍

    private String progressOf;          // 项目进度

    @TableField(fill = FieldFill.INSERT)
    private String createUser;          // 创建人

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUser;          // 更新人

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;            // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;            // 更新时间

//    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
//    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JSONField(format="yyyy-MM-dd")
    private Date startTime;             // 开始时间

    @JSONField(format="yyyy-MM-dd")
    private Date endTime;               // 结束时间

    @Override
    protected Serializable pkVal() {
        return this.projectId;
    }

}
