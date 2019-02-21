package com.kjs.trymvvm.base;

import java.lang.reflect.ParameterizedType;

/**
 * 作者：柯嘉少 on 2019/1/4 15:40
 * 邮箱：2449926649@qq.com
 * 说明：获取泛型的类的类型clazz
 **/
public class ClazzGetUtil<T> {
    public Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
