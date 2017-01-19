package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.snowdemo.R;
import com.vise.snowdemo.mode.GithubModel;
import com.vise.xsnow.net.api.ViseApi;
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.exception.ApiException;
import com.vise.xsnow.net.mode.ApiHost;
import com.vise.xsnow.net.mode.CacheMode;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.ui.BaseActivity;

import java.util.HashMap;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:04.
 */
public class NetTestActivity extends BaseActivity implements View.OnClickListener {

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
        mNormal_request = (Button) findViewById(R.id.normal_request);
        mFirst_cache_request = (Button) findViewById(R.id.first_cache_request);
        mFirst_remote_request = (Button) findViewById(R.id.first_remote_request);
        mOnly_cache_request = (Button) findViewById(R.id.only_cache_request);
        mOnly_remote_request = (Button) findViewById(R.id.only_remote_request);
        mCache_and_remote_request = (Button) findViewById(R.id.cache_and_remote_request);
        mClear_cache = (Button) findViewById(R.id.clear_cache);
        mShow_response_data = (TextView) findViewById(R.id.show_response_data);
    }

    @Override
    protected void bindEvent() {
        mNormal_request.setOnClickListener(this);
        mFirst_cache_request.setOnClickListener(this);
        mFirst_remote_request.setOnClickListener(this);
        mOnly_cache_request.setOnClickListener(this);
        mOnly_remote_request.setOnClickListener(this);
        mCache_and_remote_request.setOnClickListener(this);
        mClear_cache.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
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
        mViseApi = new ViseApi.Builder(mContext).cacheKey(ApiHost.getHost()).cacheMode(CacheMode.CACHE_AND_REMOTE).build();
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
