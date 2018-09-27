package jp.bizen.simpleroommemoapp.ui.top;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jp.bizen.simpleroommemoapp.R;
import jp.bizen.simpleroommemoapp.entity.Memo;
import jp.bizen.simpleroommemoapp.event.Event;
import jp.bizen.simpleroommemoapp.event.EventBus;
import jp.bizen.simpleroommemoapp.event.MemoUpdateEvent;
import jp.bizen.simpleroommemoapp.ui.dialog.ConfirmationDialogFragment;
import jp.bizen.simpleroommemoapp.ui.dialog.EditMemoDialogFragment;
import jp.bizen.simpleroommemoapp.ui.memo.MemoListFragment;

/**
 * トップに表示される画面
 * この中のfragment_containerでFragmentを表示する
 * 全体に左右するものとか、ある程度処理の安全性が求められるようなものはここで処理する
 */
public final class TopActivity extends AppCompatActivity implements EditMemoDialogFragment.Listener, ConfirmationDialogFragment.Listener {
    private static final int REQUEST_ID_DELETE_MEMO = 1000;
    private static final String BUNDLE_MEMO = "memo";

    private final TopViewModel.Listener mListener = new TopViewModel.Listener() {
        @Override
        public void onCreateMemoClick() {
            if (getSupportFragmentManager() != null) {
                EditMemoDialogFragment.newCreateDialogInstance().show(getSupportFragmentManager(), "new");
            }
        }

        @Override
        public void onEditMemoClick(Memo memo) {
            if (getSupportFragmentManager() != null) {
                EditMemoDialogFragment.newEditDialogInstance(memo).show(getSupportFragmentManager(), "edit");
            }
        }

        @Override
        public void onDeleteMemoClick(Memo memo) {
            if (getSupportFragmentManager() != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_MEMO, memo);
                ConfirmationDialogFragment.newInstance(REQUEST_ID_DELETE_MEMO, bundle).show(getSupportFragmentManager(), "delete");
            }
        }
    };
    private CoordinatorLayout mRootLayout;
    private TopViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top);

        mRootLayout = findViewById(R.id.layout);

        mViewModel = ViewModelProviders.of(this).get(TopViewModel.class);
        mViewModel.setListener(mListener);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.onCreateMemoClick();
            }
        });

        setupEventObserver();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, MemoListFragment.newInstance(), "MemoList")
                .commit();
    }

    private void setupEventObserver() {
        // Fabの表示/隠す指示があった場合に呼ばれる
        mViewModel.getFabVisible().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFabVisible) {
                if (isFabVisible == null) return;
                FloatingActionButton fab = findViewById(R.id.fab);
                if (isFabVisible) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });

        // Memoの状態が変わったらFABを強制表示
        EventBus.getObserver().observe(this, new Observer<Event>() {
            @Override
            public void onChanged(@Nullable Event event) {
                if (event instanceof MemoUpdateEvent) {
                    mViewModel.setFabVisible(true);
                }
            }
        });
    }

    // EditMemoDialogFragment Listener
    @Override
    public void onEditMemoFinish(Memo memo) {
        mViewModel.onUpsertMemo(memo);
    }

    @Override
    public void onEditMemoDialogCancel() {
        Snackbar.make(mRootLayout, "編集がキャンセルされました", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onConfirmOk(int requestId, @Nullable Bundle bundle) {
        switch (requestId) {
            case REQUEST_ID_DELETE_MEMO:
                if (bundle == null) return;
                Memo memo = bundle.getParcelable(BUNDLE_MEMO);
                mViewModel.onDeleteMemo(memo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfirmCancel(int requestId, @Nullable Bundle bundle) {
        switch (requestId) {
            case REQUEST_ID_DELETE_MEMO:
                Snackbar.make(mRootLayout, "削除がキャンセルされました", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
