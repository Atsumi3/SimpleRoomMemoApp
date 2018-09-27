package jp.bizen.simpleroommemoapp.entity;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * 格納されているデータとやり取りするためのインターフェース
 * <p>
 * 実際にActivityなどから利用するときはこのクラスが使われる
 * RoomMemoDataStoreに実装がある
 * この実装次第で、どこから取ってくるかを制御できる。(今回はローカルのDBからのみ)
 * </p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface MemoDataStore {
    Observable<Memo> findAllMemo();

    int count();

    Completable insertOrUpdate(Memo memo);

    Completable deleteById(long id);
}
