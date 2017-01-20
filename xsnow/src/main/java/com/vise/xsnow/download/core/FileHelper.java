package com.vise.xsnow.download.core;

import android.text.TextUtils;

import com.vise.log.ViseLog;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.download.mode.DownRange;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import okhttp3.internal.http.OkHeaders;
import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.text.TextUtils.concat;
import static java.io.File.separator;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 21:57.
 */
public class FileHelper {
    private static final String TMP_SUFFIX = ".tmp";  //temp file
    private static final String LMF_SUFFIX = ".lmf";  //last modify file
    private static final String CACHE = ".cache";    //cache directory

    private static final int EACH_RECORD_SIZE = 16; //long + long = 8 + 8
    private int RECORD_FILE_TOTAL_SIZE;
    //|*********************|
    //|*****Record  File****|
    //|*********************|
    //|  0L      |     7L   | 0
    //|  8L      |     15L  | 1
    //|  16L     |     31L  | 2
    //|  ...     |     ...  | MAX_THREADS-1
    //|*********************|
    private int MAX_THREADS = 3;

    private String mDefaultSavePath;
    private String mDefaultCachePath;

    FileHelper() {
        RECORD_FILE_TOTAL_SIZE = EACH_RECORD_SIZE * MAX_THREADS;
        setDefaultSavePath(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath());
    }

    void setDefaultSavePath(String defaultSavePath) {
        mDefaultSavePath = defaultSavePath;
        mDefaultCachePath = concat(defaultSavePath, separator, CACHE).toString();
    }

    int getMaxThreads() {
        return MAX_THREADS;
    }

    void setMaxThreads(int MAX_THREADS) {
        this.MAX_THREADS = MAX_THREADS;
        RECORD_FILE_TOTAL_SIZE = EACH_RECORD_SIZE * MAX_THREADS;
    }

    void createDirectories(String savePath) throws IOException {
        createDirectories(getRealDirectoryPaths(savePath));
    }

    String[] getRealDirectoryPaths(String savePath) {
        String fileDirectory;
        String cacheDirectory;
        if (!TextUtils.isEmpty(savePath)) {
            fileDirectory = savePath;
            cacheDirectory = concat(savePath, separator, CACHE).toString();
        } else {
            fileDirectory = mDefaultSavePath;
            cacheDirectory = mDefaultCachePath;
        }
        return new String[]{fileDirectory, cacheDirectory};
    }

    String[] getRealFilePaths(String saveName, String savePath) {
        String[] directories = getRealDirectoryPaths(savePath);
        String filePath = concat(directories[0], separator, saveName).toString();
        String tempPath = concat(directories[1], separator, saveName, TMP_SUFFIX).toString();
        String lmfPath = concat(directories[1], separator, saveName, LMF_SUFFIX).toString();
        return new String[]{filePath, tempPath, lmfPath};
    }

    void prepareDownload(File lastModifyFile, File saveFile, long fileLength,
                         String lastModify) throws IOException, ParseException {
        writeLastModify(lastModifyFile, lastModify);
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(saveFile, "rws");
            if (fileLength != -1) {
                file.setLength(fileLength);//设置下载文件的长度
            } else {
                ViseLog.i("Chunked download.");
                //Chunked 下载, 无需设置文件大小.
            }
        } finally {
            Utils.closeQuietly(file);
        }
    }

    void saveFile(Subscriber<? super DownProgress> sub, File saveFile, Response<ResponseBody> resp) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            try {
                int readLen;
                int downloadSize = 0;
                byte[] buffer = new byte[8192];

                DownProgress status = new DownProgress();
                inputStream = resp.body().byteStream();
                outputStream = new FileOutputStream(saveFile);

                long contentLength = resp.body().contentLength();
                boolean isChunked = Utils.isChunked(resp);
                if (isChunked || contentLength == -1) {
                    status.setChunked(true);
                }
                status.setTotalSize(contentLength);

                while ((readLen = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readLen);
                    downloadSize += readLen;
                    status.setDownloadSize(downloadSize);
                    sub.onNext(status);
                }
                outputStream.flush(); // This is important!!!
                ViseLog.i("Normal download completed!");
                sub.onCompleted();
            } finally {
                Utils.closeQuietly(inputStream);
                Utils.closeQuietly(outputStream);
                Utils.closeQuietly(resp.body());
            }
        } catch (IOException e) {
            ViseLog.i("Normal download failed or cancel!");
            sub.onError(e);
        }
    }

    void prepareDownload(File lastModifyFile, File tempFile, File saveFile,
                         long fileLength, String lastModify) throws IOException, ParseException {
        writeLastModify(lastModifyFile, lastModify);
        RandomAccessFile rFile = null;
        RandomAccessFile rRecord = null;
        FileChannel channel = null;
        try {
            rFile = new RandomAccessFile(saveFile, "rws");
            rFile.setLength(fileLength);//设置下载文件的长度

            rRecord = new RandomAccessFile(tempFile, "rws");
            rRecord.setLength(RECORD_FILE_TOTAL_SIZE); //设置指针记录文件的大小
            channel = rRecord.getChannel();
            MappedByteBuffer buffer = channel.map(READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);

            long start;
            long end;
            int eachSize = (int) (fileLength / MAX_THREADS);
            for (int i = 0; i < MAX_THREADS; i++) {
                if (i == MAX_THREADS - 1) {
                    start = i * eachSize;
                    end = fileLength - 1;
                } else {
                    start = i * eachSize;
                    end = (i + 1) * eachSize - 1;
                }
                buffer.putLong(start);
                buffer.putLong(end);
            }
        } finally {
            Utils.closeQuietly(channel);
            Utils.closeQuietly(rRecord);
            Utils.closeQuietly(rFile);
        }
    }

    void saveFile(Subscriber<? super DownProgress> subscriber, int i, long start, long end,
                  File tempFile, File saveFile, ResponseBody response) {
        RandomAccessFile record = null;
        FileChannel recordChannel = null;

        RandomAccessFile save = null;
        FileChannel saveChannel = null;

        InputStream inStream = null;
        try {
            try {
                ViseLog.i(Thread.currentThread().getName() + " start download from " + start + " to " + end + "!");
                int readLen;
                byte[] buffer = new byte[8192];
                DownProgress status = new DownProgress();

                record = new RandomAccessFile(tempFile, "rws");
                recordChannel = record.getChannel();
                MappedByteBuffer recordBuffer = recordChannel.map(READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);
                long totalSize = recordBuffer.getLong(RECORD_FILE_TOTAL_SIZE - 8) + 1;
                status.setTotalSize(totalSize);

                save = new RandomAccessFile(saveFile, "rws");
                saveChannel = save.getChannel();
                MappedByteBuffer saveBuffer = saveChannel.map(READ_WRITE, start, end - start + 1);

                inStream = response.byteStream();
                while ((readLen = inStream.read(buffer)) != -1) {
                    saveBuffer.put(buffer, 0, readLen);
                    recordBuffer.putLong(i * EACH_RECORD_SIZE, recordBuffer.getLong(i * EACH_RECORD_SIZE) + readLen);

                    status.setDownloadSize(totalSize - getResidue(recordBuffer));
                    subscriber.onNext(status);
                }
                ViseLog.i(Thread.currentThread().getName() + " complete download! Download size is " +
                        response.contentLength() + " bytes");
                subscriber.onCompleted();
            } finally {
                Utils.closeQuietly(record);
                Utils.closeQuietly(recordChannel);
                Utils.closeQuietly(save);
                Utils.closeQuietly(saveChannel);
                Utils.closeQuietly(inStream);
                Utils.closeQuietly(response);
            }
        } catch (IOException e) {
            ViseLog.i(Thread.currentThread().getName() + " download failed or cancel!");
            subscriber.onError(e);
        }
    }

    boolean downloadNotComplete(File tempFile) throws IOException {
        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, "rws");
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);
            long startByte;
            long endByte;
            for (int i = 0; i < MAX_THREADS; i++) {
                startByte = buffer.getLong();
                endByte = buffer.getLong();
                if (startByte <= endByte) {
                    return true;
                }
            }
            return false;
        } finally {
            Utils.closeQuietly(channel);
            Utils.closeQuietly(record);
        }
    }

    boolean tempFileDamaged(File tempFile, long fileLength) throws IOException {
        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, "rws");
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);
            long recordTotalSize = buffer.getLong(RECORD_FILE_TOTAL_SIZE - 8) + 1;
            return recordTotalSize != fileLength;
        } finally {
            Utils.closeQuietly(channel);
            Utils.closeQuietly(record);
        }
    }

    DownRange readDownloadRange(File tempFile, int i) throws IOException {
        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, "rws");
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(READ_WRITE, i * EACH_RECORD_SIZE, (i + 1) * EACH_RECORD_SIZE);
            long startByte = buffer.getLong();
            long endByte = buffer.getLong();
            return new DownRange(startByte, endByte);
        } finally {
            Utils.closeQuietly(channel);
            Utils.closeQuietly(record);
        }
    }

    String getLastModify(File file) throws IOException {
        RandomAccessFile record = null;
        try {
            record = new RandomAccessFile(file, "rws");
            record.seek(0);
            return Utils.longToGMT(record.readLong());
        } finally {
            Utils.closeQuietly(record);
        }
    }

    private void createDirectories(String... directoryPaths) throws IOException {
        for (String each : directoryPaths) {
            File file = new File(each);
            if (file.exists() && file.isDirectory()) {
                ViseLog.d("Directory exists. Do not need create. Path = " + each);
            } else {
                boolean flag = file.mkdir();
                if (flag) {
                    ViseLog.d("Directory create succeed! Path = " + each);
                } else {
                    ViseLog.d("Directory create failed! Path = " + each);
                    throw new IOException("Directory create failed!");
                }
            }
        }
    }

    private void writeLastModify(File file, String lastModify) throws IOException, ParseException {
        RandomAccessFile record = null;
        try {
            record = new RandomAccessFile(file, "rws");
            record.setLength(8);
            record.seek(0);
            record.writeLong(Utils.GMTToLong(lastModify));
        } finally {
            Utils.closeQuietly(record);
        }
    }

    /**
     * 还剩多少字节没有下载
     *
     * @param recordBuffer buffer
     * @return 剩余的字节
     */
    private long getResidue(MappedByteBuffer recordBuffer) {
        long residue = 0;
        for (int j = 0; j < MAX_THREADS; j++) {
            long startTemp = recordBuffer.getLong(j * EACH_RECORD_SIZE);
            long endTemp = recordBuffer.getLong(j * EACH_RECORD_SIZE + 8);
            long temp = endTemp - startTemp + 1;
            residue += temp;
        }
        return residue;
    }

    public static class Utils {
        public static String longToGMT(long lastModify) {
            Date d = new Date(lastModify);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            return sdf.format(d);
        }

        public static long GMTToLong(String GMT) throws ParseException {
            if (GMT == null || "".equals(GMT)) {
                return new Date().getTime();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(GMT);
            return date.getTime();
        }

        public static void close(Closeable closeable) throws IOException {
            if (closeable != null) {
                closeable.close();
            }
        }

        public static void closeQuietly(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (RuntimeException rethrown) {
                    throw rethrown;
                } catch (Exception ignored) {
                }
            }
        }

        public static void unSubscribe(Subscription subscription) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        public static String lastModify(Response<?> response) {
            return response.headers().get("Last-Modified");
        }

        public static long contentLength(Response<?> response) {
            return OkHeaders.contentLength(response.headers());
        }

        public static boolean isChunked(Response<?> response) {
            return "chunked".equals(transferEncoding(response));
        }

        public static boolean notSupportRange(Response<?> response) {
            return TextUtils.isEmpty(contentRange(response)) || contentLength(response) == -1 || isChunked(response);
        }

        public static boolean serverFileChanged(Response<Void> resp) {
            return resp.code() == 200;
        }

        public static boolean serverFileNotChange(Response<Void> resp) {
            return resp.code() == 206;
        }

        public static boolean requestRangeNotSatisfiable(Response<Void> resp) {
            return resp.code() == 416;
        }

        private static String transferEncoding(Response<?> response) {
            return response.headers().get("Transfer-Encoding");
        }

        private static String contentRange(Response<?> response) {
            return response.headers().get("Content-Range");
        }
    }
}
