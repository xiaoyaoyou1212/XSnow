package com.vise.netexpand.func;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.vise.netexpand.mode.ApiResult;
import com.vise.netexpand.mode.ResponseCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * @Description: ResponseBody转ApiResult<T>
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-30 17:55
 */
public class ApiResultFunc<T> implements Func1<ResponseBody, ApiResult<T>> {
    protected Type type;

    public ApiResultFunc(Type type) {
        this.type = type;
    }

    @Override
    public ApiResult<T> call(ResponseBody responseBody) {
        Gson gson = new Gson();
        ApiResult<T> apiResult = new ApiResult<T>();
        apiResult.setCode(-1);
        try {
            String json = responseBody.string();
            if (type.equals(String.class)) {
                apiResult.setData((T) json);
                apiResult.setCode(0);
            } else {
                ApiResult result = parseApiResult(json, apiResult);
                if (result != null) {
                    apiResult = result;
                    if (apiResult.getData() != null) {
                        T data = gson.fromJson(apiResult.getData().toString(), type);
                        apiResult.setData(data);
                        apiResult.setCode(ResponseCode.HTTP_SUCCESS);
                    } else {
                        apiResult.setMsg("ApiResult's data is null");
                    }
                } else {
                    apiResult.setMsg("json is null");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            apiResult.setMsg(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            apiResult.setMsg(e.getMessage());
        } finally {
            responseBody.close();
        }
        return apiResult;
    }

    private ApiResult parseApiResult(String json, ApiResult apiResult) throws JSONException {
        if (TextUtils.isEmpty(json)) return null;
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("code")) {
            apiResult.setCode(jsonObject.getInt("code"));
        }
        if (jsonObject.has("data")) {
            apiResult.setData(jsonObject.getString("data"));
        }
        if (jsonObject.has("msg")) {
            apiResult.setMsg(jsonObject.getString("msg"));
        }
        return apiResult;
    }
}
