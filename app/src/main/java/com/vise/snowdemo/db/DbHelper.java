package com.vise.snowdemo.db;

import android.content.Context;

import com.vise.snowdemo.mode.AuthorModel;
import com.vise.xsnow.database.DBManager;

import org.greenrobot.greendao.AbstractDao;

/**
 * @Description: 数据库操作类，由于greenDao的特殊性，不能在框架中搭建，
 * 所有数据库操作都可以参考该类实现自己的数据库操作管理类，不同的Dao实现
 * 对应的getAbstractDao方法就行。
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:18.
 */
public class DbHelper {
    private static final String DB_NAME = "vise.db";//数据库名称
    private static DbHelper instance;
    private static DBManager<AuthorModel, Long> author;
    private DaoMaster.DevOpenHelper mHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private DbHelper() {

    }

    public static DbHelper getInstance() {
        if (instance == null) {
            synchronized (DbHelper.class) {
                if (instance == null) {
                    instance = new DbHelper();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public void init(Context context, String dbName) {
        mHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DBManager<AuthorModel, Long> author() {
        if (author == null) {
            author = new DBManager<AuthorModel, Long>(){
                @Override
                public AbstractDao<AuthorModel, Long> getAbstractDao() {
                    return mDaoSession.getAuthorModelDao();
                }
            };
        }
        return author;
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public void clear() {
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public void close() {
        clear();
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }
}
