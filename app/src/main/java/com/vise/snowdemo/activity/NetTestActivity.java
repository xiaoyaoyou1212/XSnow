package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.netexpand.request.ApiGetRequest;
import com.vise.netexpand.request.ApiPostRequest;
import com.vise.snowdemo.R;
import com.vise.snowdemo.mode.AuthorModel;
import com.vise.xsnow.common.GsonUtil;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.mode.CacheMode;
import com.vise.xsnow.http.mode.CacheResult;
import com.vise.xsnow.ui.BaseActivity;

import java.util.List;

/**
 * @Description: 网络获取相关展示
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:04.
 */
public class NetTestActivity extends BaseActivity {

    private TextView mShow_response_data;
    private Button mClear_cache;
    private Button mRequest_get_1;
    private Button mRequest_get_2;
    private Button mRequest_get_3;
    private Button mRequest_get_4;
    private Button mRequest_get_5;
    private Button mRequest_get_6;
    private Button mRequest_get_7;
    private Button mRequest_get_8;
    private Button mRequest_get_9;
    private Button mRequest_get_10;
    private Button mRequest_get_11;
    private Button mRequest_post_1;
    private Button mRequest_post_2;
    private Button mRequest_post_3;
    private Button mRequest_post_4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_test);
    }

    @Override
    protected void initView() {
        mShow_response_data = F(R.id.show_response_data);
        mClear_cache = F(R.id.clear_cache);
        mRequest_get_1 = F(R.id.request_get_1);
        mRequest_get_2 = F(R.id.request_get_2);
        mRequest_get_3 = F(R.id.request_get_3);
        mRequest_get_4 = F(R.id.request_get_4);
        mRequest_get_5 = F(R.id.request_get_5);
        mRequest_get_6 = F(R.id.request_get_6);
        mRequest_get_7 = F(R.id.request_get_7);
        mRequest_get_8 = F(R.id.request_get_8);
        mRequest_get_9 = F(R.id.request_get_9);
        mRequest_get_10 = F(R.id.request_get_10);
        mRequest_get_11 = F(R.id.request_get_11);
        mRequest_post_1 = F(R.id.request_post_1);
        mRequest_post_2 = F(R.id.request_post_2);
        mRequest_post_3 = F(R.id.request_post_3);
        mRequest_post_4 = F(R.id.request_post_4);
    }

    @Override
    protected void bindEvent() {
        C(mClear_cache);
        C(mRequest_get_1);
        C(mRequest_get_2);
        C(mRequest_get_3);
        C(mRequest_get_4);
        C(mRequest_get_5);
        C(mRequest_get_6);
        C(mRequest_get_7);
        C(mRequest_get_8);
        C(mRequest_get_9);
        C(mRequest_get_10);
        C(mRequest_get_11);
        C(mRequest_post_1);
        C(mRequest_post_2);
        C(mRequest_post_3);
        C(mRequest_post_4);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void processClick(View view) {
        switch (view.getId()) {
            case R.id.clear_cache:
                clearCache();
                break;
            case R.id.request_get_1:
                request_get_1();
                break;
            case R.id.request_get_2:
                request_get_2();
                break;
            case R.id.request_get_3:
                request_get_3();
                break;
            case R.id.request_get_4:
                request_get_4();
                break;
            case R.id.request_get_5:
                request_get_5();
                break;
            case R.id.request_get_6:
                request_get_6();
                break;
            case R.id.request_get_7:
                request_get_7();
                break;
            case R.id.request_get_8:
                request_get_8();
                break;
            case R.id.request_get_9:
                request_get_9();
                break;
            case R.id.request_get_10:
                request_get_10();
                break;
            case R.id.request_get_11:
                request_get_11();
                break;
            case R.id.request_post_1:
                request_post_1();
                break;
            case R.id.request_post_2:
                request_post_2();
                break;
            case R.id.request_post_3:
                request_post_3();
                break;
            case R.id.request_post_4:
                request_post_4();
                break;
        }
    }

    private void clearCache() {
        ViseHttp.getInstance().clearCache();
    }

    private void request_get_1() {
        mShow_response_data.setText("");
        ViseHttp.GET().suffixUrl("getAuthor").request(new ACallback<AuthorModel>() {
            @Override
            public void onSuccess(AuthorModel authorModel) {
                ViseLog.i("request onSuccess!");
                if (authorModel == null) {
                    return;
                }
                mShow_response_data.setText(authorModel.toString());
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void request_get_2() {
        mShow_response_data.setText("");
        ViseHttp.GET()
                .suffixUrl("getAuthor")
                .setLocalCache(true)
                .cacheMode(CacheMode.FIRST_CACHE)
                .request(new ACallback<CacheResult<AuthorModel>>() {
                    @Override
                    public void onSuccess(CacheResult<AuthorModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null || cacheResult.getCacheData() == null) {
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

    private void request_get_3() {
        mShow_response_data.setText("");
        ViseHttp.GET()
                .suffixUrl("getAuthor")
                .setLocalCache(true)
                .cacheMode(CacheMode.FIRST_REMOTE)
                .request(new ACallback<CacheResult<AuthorModel>>() {
                    @Override
                    public void onSuccess(CacheResult<AuthorModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null || cacheResult.getCacheData() == null) {
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

    private void request_get_4() {
        mShow_response_data.setText("");
        ViseHttp.GET()
                .suffixUrl("getAuthor")
                .setLocalCache(true)
                .cacheMode(CacheMode.ONLY_CACHE)
                .request(new ACallback<CacheResult<AuthorModel>>() {
                    @Override
                    public void onSuccess(CacheResult<AuthorModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null || cacheResult.getCacheData() == null) {
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

    private void request_get_5() {
        mShow_response_data.setText("");
        ViseHttp.GET()
                .suffixUrl("getAuthor")
                .setLocalCache(true)
                .cacheMode(CacheMode.ONLY_REMOTE)
                .request(new ACallback<CacheResult<AuthorModel>>() {
                    @Override
                    public void onSuccess(CacheResult<AuthorModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null || cacheResult.getCacheData() == null) {
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

    private void request_get_6() {
        mShow_response_data.setText("");
        ViseHttp.GET()
                .suffixUrl("getAuthor")
                .setLocalCache(true)
                .cacheMode(CacheMode.CACHE_AND_REMOTE)
                .request(new ACallback<CacheResult<AuthorModel>>() {
                    @Override
                    public void onSuccess(CacheResult<AuthorModel> cacheResult) {
                        ViseLog.i("request onSuccess!");
                        if (cacheResult == null || cacheResult.getCacheData() == null) {
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

    private void request_get_7() {
        mShow_response_data.setText("");
        ViseHttp.GET().suffixUrl("getString").request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
                ViseLog.i("request onSuccess!");
                if (data == null) {
                    return;
                }
                mShow_response_data.setText(data);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void request_get_8() {
        mShow_response_data.setText("");
        ViseHttp.GET().suffixUrl("getListAuthor").request(new ACallback<List<AuthorModel>>() {
            @Override
            public void onSuccess(List<AuthorModel> authorModel) {
                ViseLog.i("request onSuccess!");
                if (authorModel == null) {
                    return;
                }
                mShow_response_data.setText(authorModel.toString());
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void request_get_9() {
        mShow_response_data.setText("");
        ViseHttp.BASE(new ApiGetRequest()).suffixUrl("getApiResultAuthor").request(new ACallback<AuthorModel>() {
            @Override
            public void onSuccess(AuthorModel authorModel) {
                ViseLog.i("request onSuccess!");
                if (authorModel == null) {
                    return;
                }
                mShow_response_data.setText(authorModel.toString());
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void request_get_10() {
        mShow_response_data.setText("");
        ViseHttp.BASE(new ApiGetRequest()).suffixUrl("getApiResultString").request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
                ViseLog.i("request onSuccess!");
                if (data == null) {
                    return;
                }
                mShow_response_data.setText(data);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void request_get_11() {
        mShow_response_data.setText("");
        ViseHttp.BASE(new ApiGetRequest()).suffixUrl("getApiResultListAuthor").request(new ACallback<List<AuthorModel>>() {
            @Override
            public void onSuccess(List<AuthorModel> authorModel) {
                ViseLog.i("request onSuccess!");
                if (authorModel == null) {
                    return;
                }
                mShow_response_data.setText(authorModel.toString());
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void request_post_1() {
        mShow_response_data.setText("");
        ViseHttp.BASE(new ApiPostRequest()).suffixUrl("postAuthor").request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
                ViseLog.i("request onSuccess!");
                if (data == null) {
                    return;
                }
                mShow_response_data.setText(data);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void request_post_2() {
        mShow_response_data.setText("");
        ViseHttp.BASE(new ApiPostRequest()
                .addForm("author_name", getString(R.string.author_name))
                .addForm("author_nickname", getString(R.string.author_nickname))
                .addForm("author_account", "xiaoyaoyou1212")
                .addForm("author_github", "https://github.com/xiaoyaoyou1212")
                .addForm("author_csdn", "http://blog.csdn.net/xiaoyaoyou1212")
                .addForm("author_websit", "http://www.huwei.tech/")
                .addForm("author_introduction", getString(R.string.author_introduction)))
                .suffixUrl("postFormAuthor")
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ViseLog.i("request onSuccess!");
                        if (data == null) {
                            return;
                        }
                        mShow_response_data.setText(data);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }

    private void request_post_3() {
        mShow_response_data.setText("");
        AuthorModel mAuthorModel = new AuthorModel();
        mAuthorModel.setAuthor_id(1008);
        mAuthorModel.setAuthor_name(getString(R.string.author_name));
        mAuthorModel.setAuthor_nickname(getString(R.string.author_nickname));
        mAuthorModel.setAuthor_account("xiaoyaoyou1212");
        mAuthorModel.setAuthor_github("https://github.com/xiaoyaoyou1212");
        mAuthorModel.setAuthor_csdn("http://blog.csdn.net/xiaoyaoyou1212");
        mAuthorModel.setAuthor_websit("http://www.huwei.tech/");
        mAuthorModel.setAuthor_introduction(getString(R.string.author_introduction));
        ViseHttp.BASE(new ApiPostRequest()
                .setJson(GsonUtil.gson().toJson(mAuthorModel)))
                .suffixUrl("postJsonAuthor")
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ViseLog.i("request onSuccess!");
                        if (data == null) {
                            return;
                        }
                        mShow_response_data.setText(data);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }

    private void request_post_4() {
        mShow_response_data.setText("");
        AuthorModel mAuthorModel = new AuthorModel();
        mAuthorModel.setAuthor_id(1009);
        mAuthorModel.setAuthor_name(getString(R.string.author_name));
        mAuthorModel.setAuthor_nickname(getString(R.string.author_nickname));
        mAuthorModel.setAuthor_account("xiaoyaoyou1212");
        mAuthorModel.setAuthor_github("https://github.com/xiaoyaoyou1212");
        mAuthorModel.setAuthor_csdn("http://blog.csdn.net/xiaoyaoyou1212");
        mAuthorModel.setAuthor_websit("http://www.huwei.tech/");
        mAuthorModel.setAuthor_introduction(getString(R.string.author_introduction));
        ViseHttp.BASE(new ApiPostRequest()
                .addUrlParam("appId", "10001")
                .addUrlParam("appType", "Android")
                .setJson(GsonUtil.gson().toJson(mAuthorModel)))
                .suffixUrl("postUrlAuthor")
                .request(new ACallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ViseLog.i("request onSuccess!");
                        if (data == null) {
                            return;
                        }
                        mShow_response_data.setText(data);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.e("request errorCode:" + errCode + ",errorMsg:" + errMsg);
                    }
                });
    }
}
