package com.ywjs.jcsy.common.handler.serializerhandler;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.util.TypeUtils;
import com.ywjs.jcsy.common.enums.IsMountEnum;
import java.lang.reflect.Type;

/**
 * @author zhouwei
 * @create 2020-08-27 17:07
 */
public class IsMountEnumSerializer implements ObjectSerializer, ObjectDeserializer {


    //反序列化过程
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse(); // castToInt
        return value == null ? null : (T) IsMountEnum.convert(TypeUtils.castToString(value));
    }

    //暂时还不清楚
    public int getFastMatchToken() {
        return 0;
    }

    //序列化过程
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)   {
        serializer.write(((IsMountEnum)object).getDesc());
    }
}
