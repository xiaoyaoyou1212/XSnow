package com.vise.snowdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.snowdemo.activity.ImageLoaderActivity;
import com.vise.snowdemo.activity.NetTestActivity;
import com.vise.snowdemo.activity.OtherTestActivity;
import com.vise.snowdemo.activity.StatusSwitchActivity;
import com.vise.snowdemo.activity.UploadDownActivity;
import com.vise.snowdemo.mode.AuthorEvent;
import com.vise.utils.view.ActivityUtil;
import com.vise.utils.view.DialogUtil;
import com.vise.xsnow.event.IEvent;
import com.vise.xsnow.event.Subscribe;
import com.vise.xsnow.permission.OnPermissionCallback;
import com.vise.xsnow.permission.PermissionManager;
import com.vise.xsnow.ui.BaseActivity;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAboutDialog();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void initData() {
        PermissionManager.instance().with(this).request(new OnPermissionCallback() {
            @Override
            public void onRequestAllow(String permissionName) {
                DialogUtil.showTips(mContext, getString(R.string.permission_control),
                        getString(R.string.permission_allow) + "\n" + permissionName);
            }

            @Override
            public void onRequestRefuse(String permissionName) {
                DialogUtil.showTips(mContext, getString(R.string.permission_control),
                        getString(R.string.permission_refuse) + "\n" + permissionName);
            }

            @Override
            public void onRequestNoAsk(String permissionName) {
                DialogUtil.showTips(mContext, getString(R.string.permission_control),
                        getString(R.string.permission_noAsk) + "\n" + permissionName);
            }
        }, Manifest.permission.CALL_PHONE);
    }

    @Override
    protected void processClick(View view) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_net) {
            ActivityUtil.startForwardActivity(this, NetTestActivity.class);
        } else if (id == R.id.menu_down) {
            ActivityUtil.startForwardActivity(this, UploadDownActivity.class);
        } else if (id == R.id.menu_image_loader) {
            ActivityUtil.startForwardActivity(this, ImageLoaderActivity.class);
        } else if (id == R.id.menu_status_view) {
            ActivityUtil.startForwardActivity(this, StatusSwitchActivity.class);
        } else if (id == R.id.menu_other) {
            ActivityUtil.startForwardActivity(this, OtherTestActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected boolean isRegisterEvent() {
        return true;
    }

    @Subscribe
    public void showAuthor(IEvent event) {
        if (event != null && event instanceof AuthorEvent) {
            ViseLog.i("Receive Event Message:" + ((AuthorEvent) event).getAuthorModel());
            DialogUtil.showTips(mContext, "Receive Event Message", "MainActivity:\n" + ((AuthorEvent) event).getAuthorModel().toString());
        }
    }

    private void displayAboutDialog() {
        final int paddingSizeDp = 5;
        final float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (paddingSizeDp * scale + 0.5f);

        final TextView textView = new TextView(this);
        final SpannableString text = new SpannableString(getString(R.string.about_dialog_text));

        textView.setText(text);
        textView.setAutoLinkMask(RESULT_OK);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        Linkify.addLinks(text, Linkify.ALL);
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_about)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .setView(textView)
                .show();
    }

}
