package jp.bizen.simpleroommemoapp.event;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

/**
 * アプリ内で利用するイベント処理の管理をする
 */
public final class EventBus {
    private static final MutableLiveData<Event> mBUS = new MutableLiveData<>();

    private EventBus() {

    }

    public static LiveData<Event> getObserver() {
        return mBUS;
    }

    public static void postEvent(Event event) {
        mBUS.postValue(event);
    }
}
