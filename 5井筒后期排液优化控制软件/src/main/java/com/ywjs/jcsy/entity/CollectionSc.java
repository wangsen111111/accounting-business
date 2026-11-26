package com.ywjs.jcsy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ywjs.jcsy.common.enums.CollectionTypeEnum;
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
@ApiModel(value="Collection对象", description="")
@TableName("collection")
public class CollectionSc extends Model<CollectionSc> {

    private static final long serialVersionUID=1L;

    @TableId(value = "collection_id", type = IdType.AUTO)
    private Long collectionId;

    private CollectionTypeEnum collectionType;

    private String collectionName;

    private String targetId;

    private Long userId;

    @Override
    protected Serializable pkVal() {
        return this.collectionId;
    }

}
