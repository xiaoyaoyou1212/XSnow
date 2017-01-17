package com.vise.xsnow.download.mode;

/**
 * @Description: 下载记录，包含下载的所有基本信息
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-16 18:27
 */
public class DownRecord {
    private String url;
    private String saveName;
    private String savePath;
    private DownProgress downProgress;
    private int status = DownStatus.NORMAL.getStatus();
    private long date;

    public String getUrl() {
        return url;
    }

    public DownRecord setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSaveName() {
        return saveName;
    }

    public DownRecord setSaveName(String saveName) {
        this.saveName = saveName;
        return this;
    }

    public String getSavePath() {
        return savePath;
    }

    public DownRecord setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    public DownProgress getDownProgress() {
        return downProgress;
    }

    public DownRecord setDownProgress(DownProgress downProgress) {
        this.downProgress = downProgress;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public DownRecord setStatus(int status) {
        this.status = status;
        return this;
    }

    public long getDate() {
        return date;
    }

    public DownRecord setDate(long date) {
        this.date = date;
        return this;
    }

    @Override
    public String toString() {
        return "DownRecord{" +
                "url='" + url + '\'' +
                ", saveName='" + saveName + '\'' +
                ", savePath='" + savePath + '\'' +
                ", downProgress=" + downProgress +
                ", status=" + status +
                ", date=" + date +
                '}';
    }
}
