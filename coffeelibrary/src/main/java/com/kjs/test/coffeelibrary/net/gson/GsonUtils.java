package com.kjs.test.coffeelibrary.net.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kjs.test.coffeelibrary.config.CoffeeConfig;
import com.kjs.test.coffeelibrary.net.gson.adapter.DoubleDefaultAdapter;
import com.kjs.test.coffeelibrary.net.gson.adapter.FloatDefaultAdapter;
import com.kjs.test.coffeelibrary.net.gson.adapter.IntegerDefaultAdapter;
import com.kjs.test.coffeelibrary.net.gson.adapter.LongDefaultAdapter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * gson解析工具类
 */

public class GsonUtils {
    static Gson GSON =new GsonBuilder()
            .registerTypeAdapter(Integer.class,new IntegerDefaultAdapter())
            .registerTypeAdapter(int.class,new IntegerDefaultAdapter())

            .registerTypeAdapter(Long.class,new LongDefaultAdapter())
            .registerTypeAdapter(long.class,new LongDefaultAdapter())

            .registerTypeAdapter(Float.class,new FloatDefaultAdapter())
            .registerTypeAdapter(float.class,new FloatDefaultAdapter())

            .registerTypeAdapter(Double.class,new DoubleDefaultAdapter())
            .registerTypeAdapter(double.class,new DoubleDefaultAdapter())

            .create();

    /**
     * 转化为object
     *
     * @param reader        json数据
     * @param tranformClass 要转化的Class
     * @param <T>
     * @return
     */
    public static <T> T fromJsonObject(String reader, Class tranformClass) throws IllegalStateException {
        T result;
        Type type = new ParameterizedTypeImpl(CoffeeConfig.MClASS, new Class[]{tranformClass});
        try {
            result = (T) GSON.fromJson(reader, type);
        } catch (Exception e) {
            type = new ParameterizedTypeImpl(CoffeeConfig.MClASS, new Class[]{String.class});
            //如果有错尝试字符解析String.class
            try {
                result = (T) GSON.fromJson(reader, type);
            }catch (Exception el){
                throw new IllegalStateException("出错的解析类：" + tranformClass.getSimpleName()+ "------>>" + e.getMessage());
            }

        }
        return result;
    }

    /**
     * 转化为列表
     *
     * @param reader    json数据
     * @param listClass 要转化的Class
     * @param <T>
     * @return
     */
    public static <T> T fromJsonArray(String reader, Class listClass) throws IllegalStateException {
        T result;
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{listClass});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(CoffeeConfig.MClASS, new Type[]{listType});
        try {
            result = (T) GSON.fromJson(reader, type);
        } catch (Exception e) {
            type = new ParameterizedTypeImpl(CoffeeConfig.MClASS, new Class[]{String.class});
            //如果有错尝试字符解析String.class
            try {
                result = (T) GSON.fromJson(reader, type);
            }catch (Exception el){
                throw new IllegalStateException("出错的解析类：" + listClass.getSimpleName()+ "------>>" + e.getMessage());
            }
        }
        return result;
    }


    public static <T> T fromJsonNoCommonClass(String reader, Class listClass) {
        T result;
        try {
            result = (T) GSON.fromJson(reader, listClass);
        } catch (Exception e) {
            throw new IllegalStateException("出错的解析类：" + listClass.getSimpleName() + "------>>" + e.getMessage());
        }
        return result;
    }
}
