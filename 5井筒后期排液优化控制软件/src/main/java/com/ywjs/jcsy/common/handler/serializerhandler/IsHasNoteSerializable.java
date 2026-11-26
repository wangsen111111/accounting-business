package com.ywjs.jcsy.common.handler.serializerhandler;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.lang.reflect.Type;

/**
 * 是否有笔记序列化器
 * @author zhouwei
 * @create 2020-11-03 16:03
 */
public class IsHasNoteSerializable implements ObjectSerializer, ObjectDeserializer {

    //反序列化过程
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object parse = parser.parse();
        if(parse == null){
            return null;
        }
        Boolean result = parse.toString().equals("有") ? true : false;
        return (T) result;
    }

    //暂时还不清楚
    public int getFastMatchToken() {
        return 0;
    }

    //序列化过程
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)   {
        Boolean b = (Boolean) object;
        String text = b ? "有" : "无";
        serializer.write(text);
    }
}