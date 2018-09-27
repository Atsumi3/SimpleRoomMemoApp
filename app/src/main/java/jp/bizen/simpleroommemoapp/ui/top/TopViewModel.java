package jp.bizen.simpleroommemoapp.ui.top;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import jp.bizen.simpleroommemoapp.MainApplication;
import jp.bizen.simpleroommemoapp.entity.Memo;
import jp.bizen.simpleroommemoapp.event.EventBus;
import jp.bizen.simpleroommemoapp.event.MemoUpdateEvent;

/**
 * UIイベントの仲介処理を主に請け負っている
 * FABの表示制御のフラグも管理している
 */
public final class TopViewModel extends AndroidViewModel {

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> mFabVisible = new MutableLiveData<>();
    @Nullable
    private Listener mListener;

    // Constructor
    public TopViewModel(@NonNull Application application) {
        super(application);

        mFabVisible.setValue(true);
    }

    void setListener(Listener listener) {
        mListener = listener;
    }

    public LiveData<Boolean> getFabVisible() {
        return mFabVisible;
    }

    public void setFabVisible(boolean isVisible) {
        mFabVisible.postValue(isVisible);
    }

    // FABを押した時
    void onCreateMemoClick() {
        if (mListener != null) mListener.onCreateMemoClick();
    }

    // Memo の 編集ボタンを押した時
    public void onEditMemoClick(Memo memo) {
        if (mListener != null) mListener.onEditMemoClick(memo);
    }

    // EditMemoDialogFragment より メモが更新された時
    void onUpsertMemo(Memo memo) {
        MainApplication app = MainApplication.get(getApplication());
        if (app == null) return;

        Disposable disposable = app.getMemoDataStore().insertOrUpdate(memo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        EventBus.postEvent(new MemoUpdateEvent());
                    }
                });
        mDisposable.add(disposable);
    }

    // ConfirmationDialogFragment で 削除を許可した時
    void onDeleteMemo(Memo memo) {
        MainApplication app = MainApplication.get(getApplication());
        if (app == null) return;

        Disposable disposable = app.getMemoDataStore().deleteById(memo.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        EventBus.postEvent(new MemoUpdateEvent());
                    }
                });
        mDisposable.add(disposable);
    }

    // Memo の 削除ボタンを押した時
    public void onDeleteClick(Memo memo) {
        if (mListener != null) mListener.onDeleteMemoClick(memo);
    }

    // ViewModelが破棄される時
    @Override
    protected void onCleared() {
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onCleared();
    }

    public interface Listener {
        void onCreateMemoClick();

        void onEditMemoClick(Memo memo);

        void onDeleteMemoClick(Memo memo);
    }
}
