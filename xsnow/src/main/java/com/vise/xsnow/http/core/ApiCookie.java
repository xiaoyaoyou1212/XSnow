package com.vise.xsnow.http.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.vise.xsnow.http.mode.CookiesStore;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @Description: Cookie
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/12/31 21:05.
 */
public class ApiCookie implements CookieJar {

    private CookiesStore cookieStore;

    public ApiCookie(Context context) {
        if (cookieStore == null) {
            cookieStore = new CookiesStore(context);
        }
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        if (cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }

}
