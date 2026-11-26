package com.ywjs.jcsy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ywjs.jcsy.common.handler.serializerhandler.ToLongDeserialize;
import com.ywjs.jcsy.common.handler.typehandler.ListTypeHandler;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@ApiModel(value="FileNote对象", description="")
public class FileNote extends Model<FileNote> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    // 解决前端精度问题
    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = ToLongDeserialize.class)
    private Long fileId;
    private String content;
//    @JSONField(serializeUsing = ListSerializable.class, deserializeUsing = ListSerializable.class)
//    @JSONField(serializeUsing = ListSerializable.class)
    @TableField(typeHandler = ListTypeHandler.class)
    private List<String> keywords;
    @TableField(value = "create_user", fill = FieldFill.INSERT_UPDATE)
    private String createUser;              // 创建人
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;              // 更新人
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;                // 更新时间
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;                // 创建时间

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
