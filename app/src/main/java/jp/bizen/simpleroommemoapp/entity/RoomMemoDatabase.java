package jp.bizen.simpleroommemoapp.entity;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Roomがデータベースを作成するためのクラス
 * <p>
 * abstractになっているが、実際に実装するのはRoom側で行う
 * そのため、このクラスは@Database AnnotationとDao提供のみ
 * </p>
 */
@Database(entities = {
        Memo.class
}, version = 1, exportSchema = false)
public abstract class RoomMemoDatabase extends RoomDatabase {
    abstract MemoDao getMemoDao();
}
