package jp.bizen.simpleroommemoapp.entity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

/**
 * DataStoreが実際にどのようにデータを取得するかの実装クラス
 * <p>
 * 今回はローカルDBからの呼び出しのみのため、各実装クラスはMemoDaoの関数しか呼んでいない
 * 実運用の時は例えば以下のような制御をする
 * </p>
 * <ul>
 * <li>DBにデータがあればDBから取得</li>
 * <li>DBにデータがなければインターネット上から取得</li>
 * </ul>
 */
public final class RoomMemoDataStore implements MemoDataStore {
    private final MemoDao mMemoDao;
    private final Scheduler mScheduler;

    public RoomMemoDataStore(RoomMemoDatabase database, Scheduler scheduler) {
        mMemoDao = database.getMemoDao();
        mScheduler = scheduler;
    }

    @Override
    public Observable<Memo> findAllMemo() {
        return mMemoDao
                .findAllMemo()
                .subscribeOn(mScheduler)
                .onErrorReturn(new Function<Throwable, List<Memo>>() {
                    @Override
                    public List<Memo> apply(Throwable throwable) {
                        return new ArrayList<>();
                    }
                })
                .toObservable()
                .flatMapIterable(new Function<List<Memo>, Iterable<? extends Memo>>() {
                    @Override
                    public Iterable<? extends Memo> apply(List<Memo> memos) {
                        return memos;
                    }
                });
    }

    @Override
    public int count() {
        try {
            return mMemoDao.count().subscribeOn(mScheduler).blockingGet();
        } catch (IllegalStateException ignore) {
        }
        return 0;
    }

    @Override
    public Completable insertOrUpdate(final Memo memo) {
        if (memo.getId() < 1) {
            memo.setId(count());
        }
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                mMemoDao.insertOrUpdate(memo);
            }
        }).subscribeOn(mScheduler);
    }

    @Override
    public Completable deleteById(final long id) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                mMemoDao.deleteById(id);
            }
        }).subscribeOn(mScheduler);
    }
}
