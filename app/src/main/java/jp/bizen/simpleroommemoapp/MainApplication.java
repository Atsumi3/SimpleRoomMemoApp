package jp.bizen.simpleroommemoapp;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import io.reactivex.schedulers.Schedulers;
import jp.bizen.simpleroommemoapp.entity.RoomMemoDataStore;
import jp.bizen.simpleroommemoapp.entity.RoomMemoDatabase;

/**
 * アプリのほぼ全体からアクセスできるもの
 * 今回はデータストアを保持する
 */
public final class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    private RoomMemoDataStore mMemoDataStore;

    /**
     * アプリのいろいろなところからMainApplicationを参照するためのメソッド
     * このままApplicationクラスを変えて、別のアプリでいろいろ再利用できる
     *
     * @param context context
     * @return MainApplication
     */
    @Nullable
    public static MainApplication get(Context context) {
        if (context != null) {
            if (context instanceof MainApplication) {
                return (MainApplication) context;
            } else if (context.getApplicationContext() instanceof MainApplication) {
                return (MainApplication) context.getApplicationContext();
            } else if (context instanceof Activity && ((Activity) context).getApplication() instanceof MainApplication) {
                return (MainApplication) ((Activity) context).getApplication();
            } else if (context instanceof Service && ((Service) context).getApplication() instanceof MainApplication) {
                return (MainApplication) ((Service) context).getApplication();
            } else {
                Log.w(TAG, "Context is not MainApplication.");
            }
        } else {
            Log.w(TAG, "Context is null.");
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        RoomMemoDatabase db = Room
                .databaseBuilder(this, RoomMemoDatabase.class, "RoomMemoDatabase")
                .build();
        mMemoDataStore = new RoomMemoDataStore(db, Schedulers.io());
    }

    /**
     * メモのデータベースにアクセスするためのgetter
     *
     * @return RoomMemoDataStore
     */
    public RoomMemoDataStore getMemoDataStore() {
        return mMemoDataStore;
    }
}
