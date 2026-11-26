package com.ywjs.jcsy.common.handler.serializerhandler;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author zhouwei
 * @create 2020-10-29 16:07
 */
public class ToLongDeserialize implements ObjectDeserializer  {

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            String strVal = object.toString();
            out.writeString(strVal);
        }
    }

    //反序列化过程
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String value = (String)parser.parse(); // castToInt
        if(value == null){
            return null;
        }else {
            Long result = Long.parseLong(value);
            return (T) result;
        }
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}