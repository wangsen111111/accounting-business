package com.ywjs.jcsy.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Operation对象", description="")
public class Operation extends Model<Operation> {

    private static final long serialVersionUID=1L;

    @TableId(value = "operation_id", type = IdType.ASSIGN_UUID)
    private String operationId;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String createUser;

    private String operationTable;

    private String targetId;

    private String targetName;

    private String operationDescription;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date operationDate;

    @Override
    protected Serializable pkVal() {
        return this.operationId;
    }


}
