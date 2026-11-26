package com.ywjs.jcsy.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ywjs.jcsy.common.enums.IsMountEnum;
import com.ywjs.jcsy.common.enums.ManagementTypeEnum;
import com.ywjs.jcsy.common.handler.serializerhandler.IsMountEnumSerializer;
import com.ywjs.jcsy.common.handler.serializerhandler.ManagementTypeEnumSerializer;
import com.ywjs.jcsy.common.handler.serializerhandler.ToLongDeserialize;
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
@ApiModel(value="File对象", description="")
public class File extends Model<File> {

    private static final long serialVersionUID=1L;

//    @JsonSerialize(using = ToStringSerializer.class) // 解决前端精度问题
    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = ToLongDeserialize.class)
    @TableId(value = "file_id", type = IdType.ASSIGN_ID)
    private Long fileId;

//    private String serverFileName; //  服务器存储文件名称

    private String fileName; //  真实文件名（上传时文件名）

    private String fileType;

    private String filePath;

    private String accessPath;

    @JSONField(serializeUsing = IsMountEnumSerializer.class, deserializeUsing = IsMountEnumSerializer.class)
    private IsMountEnum isMount; // 是否挂载

//    @JSONType(serializeEnumAsJavaBean = true)（import com.alibaba.fastjson.annotation.JSONType;）
//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    @TableField(typeHandler = JacksonTypeHandler.class)
    @JSONField(serializeUsing = ManagementTypeEnumSerializer.class, deserializeUsing = ManagementTypeEnumSerializer.class)
    private ManagementTypeEnum managementType;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date uploadTime;

//    @TableField(fill = FieldFill.INSERT)
//    private Date createTime;

    private Integer progressOf; // 进度
    private Integer score; // 评分

    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }

//    @Data
//    @Accessors(chain = true)
//    public static class MountLoc{
//        private Long projectId;
//        private List<String> jsmindIds;
//    }

}
