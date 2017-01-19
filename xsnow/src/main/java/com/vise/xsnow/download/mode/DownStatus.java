package com.vise.xsnow.download.mode;

/**
 * @Description: 下载状态
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-16 18:26
 */
public enum  DownStatus {
    NORMAL(1110),      //未下载
    WAITING(1111),     //等待中
    STARTED(1112),     //已开始下载
    PAUSED(1113),      //已暂停
    CANCELED(1114),    //已取消
    COMPLETED(1115),   //已完成
    FAILED(1116),      //下载失败
    DELETED(1117);     //已删除

    private int status;

    DownStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
