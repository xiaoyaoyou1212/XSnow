package com.vise.xsnow.database;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collection;
import java.util.List;

/**
 * @Description: 数据操库作接口
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-12-29 16:51
 */
public interface IDatabase<M, K> {
    boolean insert(@NotNull M m);

    boolean insertOrReplace(@NotNull M m);

    boolean insertInTx(@NotNull List<M> list);

    boolean insertOrReplaceInTx(@NotNull List<M> list);

    boolean delete(@NotNull M m);

    boolean deleteByKey(@NotNull K key);

    boolean deleteInTx(@NotNull List<M> list);

    boolean deleteByKeyInTx(@NotNull K... key);

    boolean deleteAll();

    boolean update(@NotNull M m);

    boolean updateInTx(@NotNull M... m);

    boolean updateInTx(@NotNull List<M> list);

    M load(@NotNull K key);

    List<M> loadAll();

    boolean refresh(@NotNull M m);

    void runInTx(@NotNull Runnable runnable);

    AbstractDao<M, K> getAbstractDao();

    QueryBuilder<M> queryBuilder();

    List<M> queryRaw(@NotNull String where, @NotNull String... selectionArg);

    Query<M> queryRawCreate(@NotNull String where, @NotNull Object... selectionArg);

    Query<M> queryRawCreateListArgs(@NotNull String where, @NotNull Collection<Object> selectionArg);

}
