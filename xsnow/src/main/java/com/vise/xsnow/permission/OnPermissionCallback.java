package com.vise.xsnow.permission;

/**
 * @Description: 申请权限回调
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2017-04-21 11:24
 */
public interface OnPermissionCallback {
    //允许
    void onRequestAllow(String permissionName);
    //拒绝
    void onRequestRefuse(String permissionName);
    //不在询问
    void onRequestNoAsk(String permissionName);
}
