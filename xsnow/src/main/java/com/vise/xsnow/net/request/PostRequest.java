package com.vise.xsnow.net.request;

import android.content.Context;

import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.callback.ACallback;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.net.mode.MediaTypes;
import com.vise.xsnow.net.subscriber.ApiCallbackSubscriber;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;

/**
 * @Description: Post请求
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 16:06
 */
public class PostRequest extends BaseRequest<PostRequest> {

    protected Map<String, Object> forms = new LinkedHashMap<>();
    protected StringBuilder stringBuilder = new StringBuilder();
    protected RequestBody requestBody;
    protected MediaType mediaType;
    protected String content;

    @Override
    protected <T> Observable<T> execute(Type type) {
        if (stringBuilder.length() > 0) {
            suffixUrl = suffixUrl + stringBuilder.toString();
        }
        if (forms != null && forms.size() > 0) {
            if (params != null && params.size() > 0) {
                Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (entryIterator.hasNext()) {
                    entry = entryIterator.next();
                    if (entry != null) {
                        forms.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return apiService.postForm(suffixUrl, forms).compose(this.<T>norTransformer(type));
        }
        if (requestBody != null) {
            return apiService.postBody(suffixUrl, requestBody).compose(this.<T>norTransformer(type));
        }
        if (content != null && mediaType != null) {
            requestBody = RequestBody.create(mediaType, content);
            return apiService.postBody(suffixUrl, requestBody).compose(this.<T>norTransformer(type));
        }
        return apiService.post(suffixUrl, params).compose(this.<T>norTransformer(type));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Type type) {
        return this.<T>execute(type).compose(ViseNet.getInstance().getApiCache().<T>transformer(cacheMode, type));
    }

    @Override
    protected <T> Subscription execute(Context context, ACallback<T> callback) {
        if (isLocalCache) {
            return this.cacheExecute(getType(callback))
                    .subscribe(new ApiCallbackSubscriber(context, callback));
        }
        return this.execute(getType(callback))
                .subscribe(new ApiCallbackSubscriber(context, callback));
    }

    public PostRequest addUrlParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append("?");
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(paramKey).append("=").append(paramValue);
        }
        return this;
    }

    public PostRequest addForm(String formKey, Object formValue) {
        if (formKey != null && formValue != null) {
            forms.put(formKey, formValue);
        }
        return this;
    }

    public PostRequest setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public PostRequest setString(String string) {
        this.content = string;
        this.mediaType = MediaTypes.TEXT_PLAIN_TYPE;
        return this;
    }

    public PostRequest setString(String string, MediaType mediaType) {
        this.content = string;
        this.mediaType = mediaType;
        return this;
    }

    public PostRequest setJson(String json) {
        this.content = json;
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostRequest setJson(JSONObject jsonObject) {
        this.content = jsonObject.toString();
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostRequest setJson(JSONArray jsonArray) {
        this.content = jsonArray.toString();
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }
}
