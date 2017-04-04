package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.snowdemo.R;
import com.vise.snowdemo.mode.GithubModel;
import com.vise.utils.assist.SSLUtil;
import com.vise.xsnow.net.api.ViseApi;
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.exception.ApiException;
import com.vise.xsnow.net.mode.ApiHost;
import com.vise.xsnow.net.mode.CacheMode;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.ui.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @Description: 网络获取相关展示
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:04.
 */
public class NetTestActivity extends BaseActivity {

    private Button mNormal_request;
    private Button mFirst_cache_request;
    private Button mFirst_remote_request;
    private Button mOnly_cache_request;
    private Button mOnly_remote_request;
    private Button mCache_and_remote_request;
    private Button mClear_cache;
    private TextView mShow_response_data;
    private ViseApi mViseApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_test);
    }

    @Override
    protected void initView() {
        mNormal_request = F(R.id.normal_request);
        mFirst_cache_request = F(R.id.first_cache_request);
        mFirst_remote_request = F(R.id.first_remote_request);
        mOnly_cache_request = F(R.id.only_cache_request);
        mOnly_remote_request = F(R.id.only_remote_request);
        mCache_and_remote_request = F(R.id.cache_and_remote_request);
        mClear_cache = F(R.id.clear_cache);
        mShow_response_data = (TextView) findViewById(R.id.show_response_data);
    }

    @Override
    protected void bindEvent() {
        C(mNormal_request);
        C(mFirst_cache_request);
        C(mFirst_remote_request);
        C(mOnly_cache_request);
        C(mOnly_remote_request);
        C(mCache_and_remote_request);
        C(mClear_cache);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void processClick(View view) {
        switch (view.getId()) {
            case R.id.normal_request:
                normalRequest();
                break;
            case R.id.first_cache_request:
                firstCacheRequest();
                break;
            case R.id.first_remote_request:
                firstRemoteRequest();
                break;
            case R.id.only_cache_request:
                onlyCacheRequest();
                break;
            case R.id.only_remote_request:
                onlyRemoteRequest();
                break;
            case R.id.cache_and_remote_request:
                cacheAndRemoteRequest();
                break;
            case R.id.clear_cache:
                clearCache();
                break;
        }
    }

    private void normalRequest() {
        mShow_response_data.setText("");
        mViseApi = new ViseApi.Builder(mContext).build();
        mViseApi.get("", new HashMap<String, String>(), new ApiCallback<GithubModel>() {
            @Override
            public void onStart() {
                ViseLog.i("request start");
            }

            @Override
            public void onError(ApiException e) {
                ViseLog.e("request error" + e);
            }

            @Override
            public void onCompleted() {
                ViseLog.i("request completed");
            }

            @Override
            public void onNext(GithubModel githubModel) {
                ViseLog.i("request next");
                if (githubModel == null) {
                    return;
                }
                mShow_response_data.setText(githubModel.toString());
            }
        });
    }

    private void firstCacheRequest() {
        mShow_response_data.setText("");
        mViseApi = new ViseApi.Builder(mContext).cacheKey(ApiHost.getHost()).cacheMode(CacheMode.FIRST_CACHE).build();
        mViseApi.cacheGet("", new HashMap<String, String>(), new ApiCallback<CacheResult<GithubModel>>() {
            @Override
            public void onStart() {
                ViseLog.i("request start");
            }

            @Override
            public void onError(ApiException e) {
                ViseLog.e("request error" + e);
            }

            @Override
            public void onCompleted() {
                ViseLog.i("request completed");
            }

            @Override
            public void onNext(CacheResult<GithubModel> cacheResult) {
                ViseLog.i("request next");
                if (cacheResult == null) {
                    return;
                }
                if (cacheResult.isCache()) {
                    mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                } else {
                    mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                }
            }
        });
    }

    private void firstRemoteRequest() {
        mShow_response_data.setText("");
        mViseApi = new ViseApi.Builder(mContext).cacheKey(ApiHost.getHost()).cacheMode(CacheMode.FIRST_REMOTE).build();
        mViseApi.cacheGet("", new HashMap<String, String>(), new ApiCallback<CacheResult<GithubModel>>() {
            @Override
            public void onStart() {
                ViseLog.i("request start");
            }

            @Override
            public void onError(ApiException e) {
                ViseLog.e("request error" + e);
            }

            @Override
            public void onCompleted() {
                ViseLog.i("request completed");
            }

            @Override
            public void onNext(CacheResult<GithubModel> cacheResult) {
                ViseLog.i("request next");
                if (cacheResult == null) {
                    return;
                }
                if (cacheResult.isCache()) {
                    mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                } else {
                    mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                }
            }
        });
    }

    private void onlyCacheRequest() {
        mShow_response_data.setText("");
        mViseApi = new ViseApi.Builder(mContext).cacheKey(ApiHost.getHost()).cacheMode(CacheMode.ONLY_CACHE).build();
        mViseApi.cacheGet("", new HashMap<String, String>(), new ApiCallback<CacheResult<GithubModel>>() {
            @Override
            public void onStart() {
                ViseLog.i("request start");
            }

            @Override
            public void onError(ApiException e) {
                ViseLog.e("request error" + e);
            }

            @Override
            public void onCompleted() {
                ViseLog.i("request completed");
            }

            @Override
            public void onNext(CacheResult<GithubModel> cacheResult) {
                ViseLog.i("request next");
                if (cacheResult == null) {
                    return;
                }
                if (cacheResult.isCache()) {
                    mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                } else {
                    mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                }
            }
        });
    }

    private void onlyRemoteRequest() {
        mShow_response_data.setText("");
        mViseApi = new ViseApi.Builder(mContext).cacheKey(ApiHost.getHost()).cacheMode(CacheMode.ONLY_REMOTE).build();
        mViseApi.cacheGet("", new HashMap<String, String>(), new ApiCallback<CacheResult<GithubModel>>() {
            @Override
            public void onStart() {
                ViseLog.i("request start");
            }

            @Override
            public void onError(ApiException e) {
                ViseLog.e("request error" + e);
            }

            @Override
            public void onCompleted() {
                ViseLog.i("request completed");
            }

            @Override
            public void onNext(CacheResult<GithubModel> cacheResult) {
                ViseLog.i("request next");
                if (cacheResult == null) {
                    return;
                }
                if (cacheResult.isCache()) {
                    mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                } else {
                    mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                }
            }
        });
    }

    private void cacheAndRemoteRequest() {
        mShow_response_data.setText("");
        try {
            mViseApi = new ViseApi.Builder(mContext).SSLSocketFactory(SSLUtil.getSslSocketFactory(new InputStream[]{this.getAssets().open("srca.cer")}, null, null)).cacheKey(ApiHost.getHost()).cacheMode(CacheMode.CACHE_AND_REMOTE).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mViseApi.cacheGet("", new HashMap<String, String>(), new ApiCallback<CacheResult<GithubModel>>() {
            @Override
            public void onStart() {
                ViseLog.i("request start");
            }

            @Override
            public void onError(ApiException e) {
                ViseLog.e("request error" + e);
            }

            @Override
            public void onCompleted() {
                ViseLog.i("request completed");
            }

            @Override
            public void onNext(CacheResult<GithubModel> cacheResult) {
                ViseLog.i("request next");
                if (cacheResult == null) {
                    return;
                }
                if (cacheResult.isCache()) {
                    mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                } else {
                    mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                }
            }
        });
    }

    private void clearCache() {
        mViseApi.clearCache();
    }

}
