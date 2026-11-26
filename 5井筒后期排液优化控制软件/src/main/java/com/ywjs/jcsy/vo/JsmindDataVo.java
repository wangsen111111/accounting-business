package com.ywjs.jcsy.vo;

import com.alibaba.fastjson.annotation.JSONField;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ywjs.jcsy.common.handler.serializerhandler.ToListStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhouwei
 * @create 2020-08-21 11:32
 * 前后台属性映射问题：
 *  @JsonIgnoreProperties   将这个注解写在类上之后，就会忽略类中不存在的字段。这个注解还可以指定要忽略的字段
 *  @JsonProperty           用于属性上、set/get方法上，该属性序列化后可重命
 *  @JSONField              注解可以用在方法（method），属性（field）以及方法中的参数（parameter）上。JSONField中的name属性用来指定JSON串中key的名称。
 *  @JsonInclude(JsonInclude.Include.NON_NULL) 转换华为json忽略null字段，方法注解（作用是指定实体类在序列化时的策略）
 *      配置方式spring:
 *                jackson:
 *                   default-property-inclusion: non_null
 */
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)  // jackson中注解
public class JsmindDataVo implements Serializable {

    private Meta meta; // 元数据
    private String format;  // 数据格式
    private JsMindData data; // 节点数据
    private View view; // 视图
    private Layout layout; // 布局
    private Attribute atribute = new Attribute(); // 自定义初始化属性

    /**
     * 自定义属性
     */
    @Data
    public static class Attribute implements Serializable{
        private Double actualZoom = 1.0; // 实际大小
        private Double zoomStep = 0.1; // 缩放步骤
        private Double minZoom = 0.4; //最小缩放
        private Double maxZoom = 2.1; //最大缩放
    }

    @Data
    @Accessors(chain = true)
    public static class Layout implements Serializable{
        private Integer hspace;         // 节点之间的水平间距
        private Integer vspace;         // 节点之间的垂直间距
        private Integer pspace;         // 节点与连接线之间的水平间距（用于容纳节点收缩/展开控制器）
    }

    @Data
    @Accessors(chain = true)
    public static class View implements Serializable{
        private String engine; // 思维导图各节点之间线条的绘制引擎
        private Integer hmargin; // 思维导图距容器外框的最小水平距离
        @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
        private String line_color; // 思维导图线条的颜色
        private Integer line_width; // 思维导图线条的粗细
        private Integer vmargin; // 思维导图距容器外框的最小垂直距离
    }

    @Data
    @Accessors(chain = true)
    public static class Meta implements Serializable{
        private String name; // 思维导图名称
        private String author; // 作者
        private String version; // 版本号
    }



    @Data
    public static class JsMindData implements Serializable{
        @JSONField(serialize = true)
        private int level;
        @JSONField(serialize = true)
        private String pid;//  : string                    节点id
        @JSONField(serialize = true)
        private String pTopic;//  : string

//        @JSONField(serialize = false)
        private List<DateFileMapping> dateFileMapping;

        @JSONField(serialize = false)
        private List<File> files;
        @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
        private String id;//  : string                    节点id
        @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
        private Integer  index;     //  : integer                   节点序号
        @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
        private String topic;     //  : string                    节点主题
        @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
        private Boolean isroot;    //  : boolean                   指示该节点是否为根节点
        @JSONField(serialize = false)
        private JsMindData parent;    //  : node                      该节点的父节点，根节点的父节目为 null ，但请不要根据此属性判断该节点是否为根节点
//        @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
        private String direction; //  : enum(left;center;right)   该节点的分布位置
        @JSONField(serialzeFeatures = SerializerFeature.PrettyFormat)
        private Boolean expanded;  //  : boolean                   该节点的下级节点是否展开


        private List<Icon> icons;
        private String priority; // 优先级
        private String progressRate; // 进度
        //    @JsonProperty(value = "background-color")
        @JSONField(name = "background-color", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String backgroundColor;

        //    @JsonProperty(value = "foreground-color")
        @JSONField(name = "foreground-color", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String foregroundColor;

        //    @JsonProperty(value = "font-size")
        @JSONField(name = "font-size", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontSize;

        //    @JsonProperty(value = "font-style")
        @JSONField(name = "font-style", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontStyle;

        //    @JsonProperty(value = "font-weight")
        @JSONField(name = "font-weight", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontWeight;

        @JSONField(name = "text-decoration", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String textDecoration;

        @JSONField(name = "font-family", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontFamily;

        @JSONField(name = "fileIds" , serializeUsing = ToListStringSerializer.class, serialzeFeatures = SerializerFeature.PrettyFormat)
        private List<Long> fileIds;

        @JSONField(name = "startTime" , serialzeFeatures = SerializerFeature.PrettyFormat)
        private String startTime;

        @JSONField(name = "endTime" , serialzeFeatures = SerializerFeature.PrettyFormat)
        private String endTime;

        //    @JsonProperty(value = "children")
        @JSONField(name = "children", serialzeFeatures = SerializerFeature.PrettyFormat)
        private List<JsMindData> children = new ArrayList<>(); // 该节点的子节点组合
    }


    /**
     * 图标
     */
    @Data
    public static class Icon implements Serializable{
        private String attrName;
        private String attrValue;
        private String imgUrl;
        private String imgHtml;
    }

    /**
     * 时间节点关系
     */
    @Data
    public static class DateFileMapping implements Serializable{
        private Set<String> dates;
        private List<Long> fileIds;
    }

    @Data
    public static class NodeAttr implements Serializable{
        private String priority; // 优先级
        private String progressRate; // 进度
        //    @JsonProperty(value = "background-color")
        @JSONField(name = "background-color", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String backgroundColor;

        //    @JsonProperty(value = "foreground-color")
        @JSONField(name = "foreground-color", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String foregroundColor;

        //    @JsonProperty(value = "font-size")
        @JSONField(name = "font-size", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontSize;

        //    @JsonProperty(value = "font-style")
        @JSONField(name = "font-style", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontStyle;

        //    @JsonProperty(value = "font-weight")
        @JSONField(name = "font-weight", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontWeight;

        @JSONField(name = "text-decoration", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String textDecoration;

        @JSONField(name = "font-family", serialzeFeatures = SerializerFeature.PrettyFormat)
        private String fontFamily;

        //    @JsonProperty(value = "fileIds")
        @JSONField(name = "fileIds" , serializeUsing = ToListStringSerializer.class, serialzeFeatures = SerializerFeature.PrettyFormat)
        private List<Long> fileIds;
    }
}
