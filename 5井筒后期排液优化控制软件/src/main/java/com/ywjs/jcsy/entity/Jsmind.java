package com.ywjs.jcsy.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ywjs.jcsy.common.handler.typehandler.JsmindDataVoTypeHandler;
import com.ywjs.jcsy.common.handler.typehandler.JcsyBooleanTypeHandler;
import com.ywjs.jcsy.vo.JsmindDataVo;
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
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Jsmind对象", description="")
public class Jsmind  extends Model<Jsmind> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;                      // 主键作为思维导图容器id
    private Long projectId;                 // 项目id
    private Long dimensionId;               // 维度id

    private String mindMapName;             // 思维导图名称

    @TableField(value = "format", fill = FieldFill.INSERT)
    private String format;                  // 数据格式

//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    @TableField(typeHandler = JacksonTypeHandler.class)
    @TableField(typeHandler = JsmindDataVoTypeHandler.class)
    private JsmindDataVo data;              // 思维导图数据

    private String theme;                   // 主题

    @TableField(value = "mode", fill = FieldFill.INSERT)
    private String mode;                    // 显示模式

    @TableField(typeHandler = JcsyBooleanTypeHandler.class,fill = FieldFill.INSERT)
    private Boolean supportHtml;            // 是否支持节点里的HTML元素
    @TableField(typeHandler = JcsyBooleanTypeHandler.class,fill = FieldFill.INSERT)
    private Boolean editable;               // 是否启用编辑
    @TableField(typeHandler = JcsyBooleanTypeHandler.class,fill = FieldFill.INSERT)
    private Boolean isTemplate;             // 是否为模板

    @TableField(value = "create_user", fill = FieldFill.INSERT_UPDATE)
    private String createUser;              // 创建人
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;              // 更新人
    @TableField(value = "create_time", fill = FieldFill.INSERT_UPDATE)
    private Date createTime;                // 创建时间
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;                // 更新时间

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
