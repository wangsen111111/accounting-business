package com.ywjs.jcsy.common.handler.serializerhandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.lang.reflect.Type;

/**
 * @author zhouwei
 * @create 2020-11-03 16:03
 */
public class ListSerializable implements ObjectSerializer, ObjectDeserializer {
    //反序列化过程
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        System.out.println("反序列化过程=>"+parser.parse());
        Object parse = parser.parse();

//        List<String> value = (List<String>) parser.parse(); // castToInt
//        List<String> list = JSON.parseObject(value, new TypeReference<List<String>>() { });
//        return value == null ? null : (T) list;
//        value.add("测试");
        return (T) parse;
    }

    //暂时还不清楚
    public int getFastMatchToken() {
        return 0;
    }

    //序列化过程
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)   {
        System.out.println("序列化过程=>"+object);
        String jsonString = JSON.toJSONString(object);
        serializer.write(jsonString);
    }
}