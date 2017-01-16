package com.vise.xsnow.net.func;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * @Description: ResponseBody转T
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-05 14:39
 */
public class ApiFunc<T> implements Func1<ResponseBody, T> {
    protected Class<T> clazz;

    public ApiFunc(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T call(ResponseBody responseBody) {
        Gson gson = new Gson();
        String json = null;
        try {
            json = responseBody.string();
            if (clazz.equals(String.class)) {
                return (T) json;
            } else {
                return gson.fromJson(json, clazz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            responseBody.close();
        }
        return (T) json;
    }
}
