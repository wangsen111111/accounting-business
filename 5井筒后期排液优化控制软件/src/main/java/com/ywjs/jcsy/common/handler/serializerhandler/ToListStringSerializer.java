package com.ywjs.jcsy.common.handler.serializerhandler;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * list中大数值数值前端丢失问题，序列化为List<String></>
 * @author zhouwei
 * @create 2020-09-08 9:52
 */
public class ToListStringSerializer implements ObjectSerializer {
    public static final ToListStringSerializer instance = new ToListStringSerializer();

    public ToListStringSerializer() {
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            List<Long> list = (List<Long>) object;
            List<String> listString = list.stream().map(item -> String.valueOf(item)).collect(Collectors.toList());
            out.write(listString);
        }
    }
}