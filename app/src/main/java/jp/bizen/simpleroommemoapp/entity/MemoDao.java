package jp.bizen.simpleroommemoapp.entity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

/**
 * データベースとやり取りするためのDAOインターフェース
 */
@Dao
public interface MemoDao {
    /**
     * 保存されている全てのMemoを返す
     *
     * @return List<Memo>
     */
    @Query("SELECT * FROM Memo")
    Single<List<Memo>> findAllMemo();

    /**
     * 保存されているMemoの個数を返す
     *
     * @return count
     */
    @Query("SELECT COUNT(id) FROM Memo")
    Single<Integer> count();

    /**
     * データの追加や更新
     * OnConflictStrategy (今回は@PrimaryKeyが付与されているMemo#idが被った場合の処理)
     *
     * @param memo memo
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Memo memo);

    /**
     * 保存されているMemoを削除する
     *
     * @param id id
     */
    @Query("DELETE FROM Memo WHERE id = :id")
    void deleteById(long id);
}
