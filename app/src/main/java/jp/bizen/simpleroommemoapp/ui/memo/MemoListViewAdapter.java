package jp.bizen.simpleroommemoapp.ui.memo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import jp.bizen.simpleroommemoapp.R;
import jp.bizen.simpleroommemoapp.entity.Memo;

/**
 * メモのリストに表示するViewを管理している
 */
final class MemoListViewAdapter extends RecyclerView.Adapter {
    private List<Memo> mData;
    private Listener mListener;

    MemoListViewAdapter() {
        mData = Collections.emptyList();
    }

    void setData(List<Memo> data) {
        mData = data;
    }

    void setListener(Listener listener) {
        mListener = listener;
    }

    /**
     * リストに表示するアイテムの雛形を作成する
     *
     * @param viewGroup viewGroup
     * @param i         viewType
     * @return ViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_memo_content, viewGroup, false);
        return new ContentViewHolder(view, new ContentViewHolder.Listener() {
            @Override
            public void onEditClick(Memo memo) {
                if (mListener != null) mListener.onEditClick(memo);
            }

            @Override
            public void onDeleteClick(Memo memo) {
                if (mListener != null) mListener.onDeleteClick(memo);
            }
        });
    }

    /**
     * リストのアイテムにデータをセットする
     *
     * @param viewHolder viewHolder
     * @param i          表示するindex
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ContentViewHolder) {
            ((ContentViewHolder) viewHolder).bind(mData.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    interface Listener {
        void onEditClick(Memo memo);

        void onDeleteClick(Memo memo);
    }

    /**
     * リストのアイテム単体を管理するクラス
     */
    static class ContentViewHolder extends RecyclerView.ViewHolder {
        private final Button mEditButton;
        private final Button mDeleteButton;
        private final TextView mText;
        private final Listener mListener;

        ContentViewHolder(@NonNull View itemView, Listener listener) {
            super(itemView);

            mEditButton = itemView.findViewById(R.id.btn_edit);
            mDeleteButton = itemView.findViewById(R.id.btn_delete);
            mText = itemView.findViewById(R.id.text);
            mListener = listener;
        }

        void bind(final Memo memo) {
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onDeleteClick(memo);
                }
            });
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onEditClick(memo);
                }
            });

            mText.setText(memo.getText());
        }

        interface Listener {
            void onEditClick(Memo memo);

            void onDeleteClick(Memo memo);
        }
    }
}
