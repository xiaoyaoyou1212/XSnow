package com.vise.xsnow.download.mode;

import android.os.Parcel;
import android.os.Parcelable;

import com.vise.utils.file.FileUtil;

import java.text.NumberFormat;

/**
 * @Description: 下载进度，包含总大小、下载大小以及是否分块信息
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 21:49.
 */
public class DownProgress implements Parcelable {
    private boolean isChunked = false;//是否分块
    private long totalSize;
    private long downloadSize;

    public DownProgress() {
    }

    public DownProgress(long totalSize, long downloadSize) {
        this.totalSize = totalSize;
        this.downloadSize = downloadSize;
    }

    public DownProgress(boolean isChunked, long totalSize, long downloadSize) {
        this.isChunked = isChunked;
        this.totalSize = totalSize;
        this.downloadSize = downloadSize;
    }

    protected DownProgress(Parcel in) {
        isChunked = in.readByte() != 0;
        totalSize = in.readLong();
        downloadSize = in.readLong();
    }

    public boolean isChunked() {
        return isChunked;
    }

    public DownProgress setChunked(boolean chunked) {
        isChunked = chunked;
        return this;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public DownProgress setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public DownProgress setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
        return this;
    }

    /**
     * 获得格式化的总Size
     *
     * @return example: 2KB , 10MB
     */
    public String getFormatTotalSize() {
        return FileUtil.byteCountToDisplaySize(totalSize);
    }

    public String getFormatDownloadSize() {
        return FileUtil.byteCountToDisplaySize(downloadSize);
    }

    /**
     * 获得格式化的状态字符串
     *
     * @return example: 2MB/36MB
     */
    public String getFormatStatusString() {
        return getFormatDownloadSize() + "/" + getFormatTotalSize();
    }

    /**
     * 获得下载的百分比, 保留两位小数
     *
     * @return example: 5.25%
     */
    public String getPercent() {
        String percent;
        Double result;
        if (totalSize == 0L) {
            result = 0.0;
        } else {
            result = downloadSize * 1.0 / totalSize;
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);//控制保留小数点后几位，2：表示保留2位小数点
        percent = nf.format(result);
        return percent;
    }

    public static final Parcelable.Creator<DownProgress> CREATOR = new Parcelable.Creator<DownProgress>() {
        @Override
        public DownProgress createFromParcel(Parcel in) {
            return new DownProgress(in);
        }

        @Override
        public DownProgress[] newArray(int size) {
            return new DownProgress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (isChunked ? 1 : 0));
        parcel.writeLong(totalSize);
        parcel.writeLong(downloadSize);
    }
}
