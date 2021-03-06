package com.kjs.test.coffeelibrary.net.gson.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * 作者：柯嘉少 on 2018/10/19 16:49
 * 邮箱：2449926649@qq.com
 * 说明：
 **/
public class DoubleDefaultAdapter implements JsonSerializer<Double>, JsonDeserializer<Double> {
    @Override
    public Double deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try{
            String str=jsonElement.getAsString();
            if(str.equals("")||str.equals(" ")||str.equals("null")){
                return 0.0D;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            return jsonElement.getAsDouble();
        }catch (Exception e){
            return 0.0D;
        }
    }

    @Override
    public JsonElement serialize(Double aDouble, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(aDouble);
    }
}
