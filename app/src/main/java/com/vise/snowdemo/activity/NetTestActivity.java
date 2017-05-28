package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.netexpand.mode.ApiResult;
import com.vise.netexpand.request.ApiGetRequest;
import com.vise.snowdemo.R;
import com.vise.snowdemo.mode.GithubModel;
import com.vise.snowdemo.mode.ZhiHuModel;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.callback.ACallback;
import com.vise.xsnow.net.mode.CacheMode;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.ui.BaseActivity;

import java.util.List;

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
        /*ViseNet.BASE(new ApiGetRequest())
                .baseUrl("http://news-at.zhihu.com/")
                .suffixUrl("api/3/sections")
                .request(mContext, new ACallback<List<ZhiHuModel>>() {
                    @Override
                    public void onSuccess(List<ZhiHuModel> data) {
                        ViseLog.i("request onSuccess:" + data);
                        if (data == null) {
                            return;
                        }
                        mShow_response_data.setText(data.toString());
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });*/
        ViseNet.GET().request(mContext, new ACallback<GithubModel>() {
            @Override
            public void onSuccess(GithubModel githubModel) {
                ViseLog.i("request onSuccess!");
                if (githubModel == null) {
                    return;
                }
                mShow_response_data.setText(githubModel.toString());
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void firstCacheRequest() {
        mShow_response_data.setText("");
        ViseNet.GET()
                .setLocalCache(true)
                .cacheMode(CacheMode.FIRST_CACHE)
                .request(mContext, new ACallback<CacheResult<GithubModel>>() {
                    @Override
                    public void onSuccess(CacheResult<GithubModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null) {
                            return;
                        }
                        if (cacheResult.isCache()) {
                            mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                        } else {
                            mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }

    private void firstRemoteRequest() {
        mShow_response_data.setText("");
        ViseNet.GET()
                .setLocalCache(true)
                .cacheMode(CacheMode.FIRST_REMOTE)
                .request(mContext, new ACallback<CacheResult<GithubModel>>() {
                    @Override
                    public void onSuccess(CacheResult<GithubModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null) {
                            return;
                        }
                        if (cacheResult.isCache()) {
                            mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                        } else {
                            mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }

    private void onlyCacheRequest() {
        mShow_response_data.setText("");
        ViseNet.GET()
                .setLocalCache(true)
                .cacheMode(CacheMode.ONLY_CACHE)
                .request(mContext, new ACallback<CacheResult<GithubModel>>() {
                    @Override
                    public void onSuccess(CacheResult<GithubModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null) {
                            return;
                        }
                        if (cacheResult.isCache()) {
                            mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                        } else {
                            mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }

    private void onlyRemoteRequest() {
        mShow_response_data.setText("");
        ViseNet.GET()
                .setLocalCache(true)
                .cacheMode(CacheMode.ONLY_REMOTE)
                .request(mContext, new ACallback<CacheResult<GithubModel>>() {
                    @Override
                    public void onSuccess(CacheResult<GithubModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null) {
                            return;
                        }
                        if (cacheResult.isCache()) {
                            mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                        } else {
                            mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }

    private void cacheAndRemoteRequest() {
        mShow_response_data.setText("");
        ViseNet.GET()
                .setLocalCache(true)
                .cacheMode(CacheMode.CACHE_AND_REMOTE)
                .request(mContext, new ACallback<CacheResult<GithubModel>>() {
                    @Override
                    public void onSuccess(CacheResult<GithubModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null) {
                            return;
                        }
                        if (cacheResult.isCache()) {
                            mShow_response_data.setText("From Cache:\n" + cacheResult.getCacheData().toString());
                        } else {
                            mShow_response_data.setText("From Remote:\n" + cacheResult.getCacheData().toString());
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }

    private void clearCache() {
        ViseNet.getInstance().clearCache();
    }

}
