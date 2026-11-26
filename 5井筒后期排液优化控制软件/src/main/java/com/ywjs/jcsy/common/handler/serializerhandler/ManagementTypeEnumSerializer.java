package com.ywjs.jcsy.common.handler.serializerhandler;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.util.TypeUtils;
import com.ywjs.jcsy.common.enums.ManagementTypeEnum;
import java.lang.reflect.Type;

/**
 * @author zhouwei
 * @create 2020-08-27 16:32
 */
//ObjectSerializer和ObjectDeserializer分别是fastjson的编码器和解码器接口
public class ManagementTypeEnumSerializer implements ObjectSerializer, ObjectDeserializer {


    //反序列化过程
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse(); // castToInt
        return value == null ? null : (T) ManagementTypeEnum.convert(TypeUtils.castToString(value));
    }

    //暂时还不清楚
    public int getFastMatchToken() {
        return 0;
    }

    //序列化过程
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)   {
        serializer.write(((ManagementTypeEnum)object).getDesc());
    }
}
