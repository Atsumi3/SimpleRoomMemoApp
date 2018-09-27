package jp.bizen.simpleroommemoapp.ui.memo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import jp.bizen.simpleroommemoapp.R;
import jp.bizen.simpleroommemoapp.entity.Memo;
import jp.bizen.simpleroommemoapp.event.Event;
import jp.bizen.simpleroommemoapp.event.EventBus;
import jp.bizen.simpleroommemoapp.event.MemoUpdateEvent;
import jp.bizen.simpleroommemoapp.ui.top.TopViewModel;

/**
 * メモのリストを持っているFragment
 */
public final class MemoListFragment extends Fragment {
    private MemoListViewModel mViewModel;
    private TopViewModel mTopViewModel;

    private final MemoListViewAdapter.Listener mAdapterListener = new MemoListViewAdapter.Listener() {
        @Override
        public void onEditClick(Memo memo) {
            mTopViewModel.onEditMemoClick(memo);
        }

        @Override
        public void onDeleteClick(Memo memo) {
            mTopViewModel.onDeleteClick(memo);
        }
    };

    /**
     * RecyclerView のスクロールに関するリスナー
     * 変化量が 0より低い(上にスクロール) 時: FABを表示
     * 変化量が 0より高い(下にスクロール) 時: FABを非表示
     */
    private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (mTopViewModel.getFabVisible().getValue() == null) return;

            boolean isFabVisible = mTopViewModel.getFabVisible().getValue();

            if (dy < 0 && !isFabVisible) mTopViewModel.setFabVisible(true);
            else if (dy > 0 && isFabVisible) mTopViewModel.setFabVisible(false);
        }
    };

    public static MemoListFragment newInstance() {
        return new MemoListFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memo_list, container, false);
        mViewModel = ViewModelProviders.of(this).get(MemoListViewModel.class);
        mTopViewModel = ViewModelProviders.of(getActivity()).get(TopViewModel.class);
        setupRecyclerView((RecyclerView) view.findViewById(R.id.list));
        setupEventObserver();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.onResume();
    }

    @SuppressWarnings("ConstantConditions")
    private void setupRecyclerView(RecyclerView recyclerView) {
        MemoListViewAdapter adapter = new MemoListViewAdapter();
        adapter.setListener(mAdapterListener);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(mScrollListener);
        // 縦方向1列のレイアウト (LinearLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupEventObserver() {
        // メモを取得した時のイベントを受け取ってAdapterへ流す
        mViewModel.getMemos().observe(this, new Observer<List<Memo>>() {
            @Override
            public void onChanged(@Nullable List<Memo> data) {
                if (getView() != null) {
                    RecyclerView recyclerView = getView().findViewById(R.id.list);
                    MemoListViewAdapter adapter = (MemoListViewAdapter) recyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.setData(data == null ? Collections.<Memo>emptyList() : data);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // メモが更新されたイベントを受け取ってViewModekへ流す (更新する)
        EventBus.getObserver().observe(this, new Observer<Event>() {
            @Override
            public void onChanged(@Nullable Event event) {
                if (event instanceof MemoUpdateEvent) {
                    mViewModel.onMemoUpdateEvent();
                }
            }
        });
    }
}
