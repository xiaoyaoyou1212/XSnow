package com.vise.xsnow.download.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vise.xsnow.download.core.DownTask;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.download.mode.DownRecord;
import com.vise.xsnow.download.mode.DownStatus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Description: 下载数据库操作管理
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 22:09.
 */
public class DownDbManager {
    private volatile static DownDbManager singleton;
    private final Object databaseLock = new Object();
    private DownDbOpenHelper downDbOpenHelper;
    private volatile SQLiteDatabase readableDatabase;
    private volatile SQLiteDatabase writableDatabase;

    private DownDbManager(Context context) {
        downDbOpenHelper = new DownDbOpenHelper(context);
    }

    public static DownDbManager getSingleton(Context context) {
        if (singleton == null) {
            synchronized (DownDbManager.class) {
                if (singleton == null) {
                    singleton = new DownDbManager(context);
                }
            }
        }
        return singleton;
    }

    public boolean recordNotExists(String url) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(DownDb.RecordTable.TABLE_NAME, new String[]{DownDb.RecordTable.COLUMN_ID}, "url=?",
                    new String[]{url}, null, null, null);
            return cursor.getCount() == 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long insertRecord(DownTask task) {
        return getWritableDatabase().insert(DownDb.RecordTable.TABLE_NAME, null, DownDb.RecordTable.insertTask(task));
    }

    public long updateRecord(String url, DownProgress progress) {
        return getWritableDatabase().update(DownDb.RecordTable.TABLE_NAME, DownDb.RecordTable.updateProgress(progress), "url=?", new String[]{url});
    }

    public long updateRecord(String url, int status) {
        return getWritableDatabase().update(DownDb.RecordTable.TABLE_NAME, DownDb.RecordTable.updateStatus(status), "url=?", new String[]{url});
    }

    public int deleteRecord(String url) {
        return getWritableDatabase().delete(DownDb.RecordTable.TABLE_NAME, "url=?", new String[]{url});
    }

    public DownRecord readSingleRecord(String url) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("select * from " + DownDb.RecordTable.TABLE_NAME + " where url=?", new String[]{url});
            cursor.moveToFirst();
            return DownDb.RecordTable.readRecord(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public DownProgress readProgress(String url) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(
                    DownDb.RecordTable.TABLE_NAME, new String[]{DownDb.RecordTable.COLUMN_DOWNLOAD_SIZE, DownDb.RecordTable.COLUMN_TOTAL_SIZE, DownDb.RecordTable.COLUMN_IS_CHUNKED},
                    "url=?", new String[]{url}, null, null, null);
            if (cursor.getCount() == 0) {
                return new DownProgress();
            } else {
                cursor.moveToFirst();
                return DownDb.RecordTable.readProgress(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void closeDataBase() {
        synchronized (databaseLock) {
            readableDatabase = null;
            writableDatabase = null;
            downDbOpenHelper.close();
        }
    }

    public Observable<List<DownRecord>> readAllRecords() {
        return Observable.create(new Observable.OnSubscribe<List<DownRecord>>() {
            @Override
            public void call(Subscriber<? super List<DownRecord>> subscriber) {
                Cursor cursor = null;
                try {
                    cursor = getReadableDatabase().rawQuery("select * from " + DownDb.RecordTable.TABLE_NAME, new String[]{});
                    List<DownRecord> result = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        result.add(DownDb.RecordTable.readRecord(cursor));
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public long repairErrorStatus() {
        return getWritableDatabase().update(DownDb.RecordTable.TABLE_NAME, DownDb.RecordTable.updateStatus(DownStatus.PAUSED.getStatus()),
                DownDb.RecordTable.COLUMN_DOWNLOAD_STATUS + "=? or " + DownDb.RecordTable.COLUMN_DOWNLOAD_STATUS + "=?",
                new String[]{DownStatus.WAITING.getStatus() + "", DownStatus.STARTED.getStatus() + ""});
    }

    public Observable<DownRecord> readRecord(final String url) {
        return Observable.create(new Observable.OnSubscribe<DownRecord>() {
            @Override
            public void call(Subscriber<? super DownRecord> subscriber) {
                Cursor cursor = null;
                try {
                    cursor = getReadableDatabase().rawQuery("select * from " + DownDb.RecordTable.TABLE_NAME +
                            " where " + "url=?", new String[]{url});
                    while (cursor.moveToNext()) {
                        subscriber.onNext(DownDb.RecordTable.readRecord(cursor));
                    }
                    subscriber.onCompleted();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = writableDatabase;
        if (db == null) {
            synchronized (databaseLock) {
                db = writableDatabase;
                if (db == null) {
                    db = writableDatabase = downDbOpenHelper.getWritableDatabase();
                }
            }
        }
        return db;
    }

    private SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = readableDatabase;
        if (db == null) {
            synchronized (databaseLock) {
                db = readableDatabase;
                if (db == null) {
                    db = readableDatabase = downDbOpenHelper.getReadableDatabase();
                }
            }
        }
        return db;
    }
}
