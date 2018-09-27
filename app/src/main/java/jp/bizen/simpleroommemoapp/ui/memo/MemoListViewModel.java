package jp.bizen.simpleroommemoapp.ui.memo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jp.bizen.simpleroommemoapp.MainApplication;
import jp.bizen.simpleroommemoapp.entity.Memo;

/**
 * メモを任意のタイミングでDBから取得してくるのが主な機能
 */
@SuppressWarnings("WeakerAccess")
public final class MemoListViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Memo>> mMemos = new MutableLiveData<>();

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    // Constructor
    public MemoListViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<List<Memo>> getMemos() {
        return mMemos;
    }

    // Fragment#onResume で呼ばれる
    void onResume() {
        fetchAllMemo();
    }

    /**
     * DataStoreから格納されている全てのメモを取得する
     */
    private void fetchAllMemo() {
        MainApplication app = MainApplication.get(getApplication());
        if (app == null) return;

        Disposable disposable = app.getMemoDataStore().findAllMemo()
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(new Consumer<List<Memo>>() {
                    @Override
                    public void accept(List<Memo> memos) {
                        mMemos.postValue(memos);
                    }
                });
        mDisposable.add(disposable);
    }

    /**
     * ViewModeが破棄される時に呼ばれる
     */
    @Override
    protected void onCleared() {
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onCleared();
    }

    void onMemoUpdateEvent() {
        fetchAllMemo();
    }
}
