package jp.bizen.simpleroommemoapp.entity;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * DataStoreのテスト
 * Memoの読み書きに関して問題がないかをチェックするもの
 */
@SuppressWarnings("SpellCheckingInspection")
@RunWith(AndroidJUnit4.class)
public class RoomMemoDataStoreTest {
    private RoomMemoDatabase mDb;
    private RoomMemoDataStore mDataStore;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room
                .inMemoryDatabaseBuilder(context, RoomMemoDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDataStore = new RoomMemoDataStore(mDb, Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        mDb.close();
    }

    @Test
    public void testInsertAndFind() {
        // 最初は空
        List<Memo> result = mDataStore.findAllMemo().toList().blockingGet();
        assertThat(result.size(), is(0));

        // 一つ目挿入
        final String memo1Text = "あいうえおかきくけこ";
        final Memo memo = new Memo();
        memo.setText(memo1Text);

        mDataStore.insertOrUpdate(memo).subscribe();

        result = mDataStore.findAllMemo().toList().blockingGet();
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getId(), is(0L));
        assertThat(result.get(0).getText(), is(memo1Text));

        // 二つ目挿入
        final String memo2Text = "ABCDEFGHIJKLMN";
        final Memo memo2 = new Memo();
        memo2.setText(memo2Text);

        mDataStore.insertOrUpdate(memo2).subscribe();

        result = mDataStore.findAllMemo().toList().blockingGet();
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getId(), is(0L));
        assertThat(result.get(0).getText(), is(memo1Text));
        assertThat(result.get(1).getId(), is(1L));
        assertThat(result.get(1).getText(), is(memo2Text));
    }

    @Test
    public void testDeleteById() {
        final long memoId = 20;
        final Memo memo = new Memo();
        memo.setId(memoId);
        memo.setText("abcdef");
        mDataStore.insertOrUpdate(memo).subscribe();

        assertThat(mDataStore.count(), is(1));

        mDataStore.deleteById(memoId).subscribe();

        assertThat(mDataStore.count(), is(0));
    }

    @Test
    public void testDeleteByIdButEmptyData() {
        assertThat(mDataStore.count(), is(0));

        mDataStore.deleteById(10).subscribe();

        assertThat(mDataStore.count(), is(0));
    }
}
