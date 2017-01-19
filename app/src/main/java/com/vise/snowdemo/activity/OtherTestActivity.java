package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vise.snowdemo.R;
import com.vise.xsnow.ui.BaseActivity;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:10.
 */
public class OtherTestActivity extends BaseActivity implements View.OnClickListener {

    private Button mDb_insert;
    private Button mDb_delete;
    private Button mDb_update;
    private Button mDb_query;
    private Button mMemory_cache_put;
    private Button mMemory_cache_get;
    private Button mSp_cache_put;
    private Button mSp_cache_get;
    private Button mSend_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_test);
    }

    @Override
    protected void initView() {
        mDb_insert = (Button) findViewById(R.id.db_insert);
        mDb_delete = (Button) findViewById(R.id.db_delete);
        mDb_update = (Button) findViewById(R.id.db_update);
        mDb_query = (Button) findViewById(R.id.db_query);
        mMemory_cache_put = (Button) findViewById(R.id.memory_cache_put);
        mMemory_cache_get = (Button) findViewById(R.id.memory_cache_get);
        mSp_cache_put = (Button) findViewById(R.id.sp_cache_put);
        mSp_cache_get = (Button) findViewById(R.id.sp_cache_get);
        mSend_event = (Button) findViewById(R.id.send_event);
    }

    @Override
    protected void bindEvent() {
        mDb_insert.setOnClickListener(this);
        mDb_delete.setOnClickListener(this);
        mDb_update.setOnClickListener(this);
        mDb_query.setOnClickListener(this);
        mMemory_cache_put.setOnClickListener(this);
        mMemory_cache_get.setOnClickListener(this);
        mSp_cache_put.setOnClickListener(this);
        mSp_cache_get.setOnClickListener(this);
        mSend_event.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.db_insert:
                break;
            case R.id.db_delete:
                break;
            case R.id.db_update:
                break;
            case R.id.db_query:
                break;
            case R.id.memory_cache_put:
                break;
            case R.id.memory_cache_get:
                break;
            case R.id.sp_cache_put:
                break;
            case R.id.sp_cache_get:
                break;
            case R.id.send_event:
                break;
        }
    }
}
