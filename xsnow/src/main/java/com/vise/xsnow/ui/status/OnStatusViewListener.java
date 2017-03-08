package com.vise.xsnow.ui.status;

import android.view.View;

/**
 * @Description: 状态视图显示监听
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2017-03-08 15:48
 */
public interface OnStatusViewListener {
    void onShowView(View view, int id);

    void onHideView(View view, int id);
}
