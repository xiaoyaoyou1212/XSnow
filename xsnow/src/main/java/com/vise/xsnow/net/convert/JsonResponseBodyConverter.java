package com.vise.xsnow.net.convert;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @Description: ResponseBody to T
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-04 18:04
 */
final class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    JsonResponseBodyConverter() {
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        return (T) value.string();
    }
}
