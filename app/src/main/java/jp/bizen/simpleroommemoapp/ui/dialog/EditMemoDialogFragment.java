package jp.bizen.simpleroommemoapp.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import jp.bizen.simpleroommemoapp.R;
import jp.bizen.simpleroommemoapp.entity.Memo;

/**
 * メモを編集するダイアログ
 */
public final class EditMemoDialogFragment extends DialogFragment {
    private static final String EXTRA_MEMO = "memo";

    @Nullable
    private Listener mListener;

    // 新しくメモを作る時はこっち
    public static EditMemoDialogFragment newCreateDialogInstance() {
        return new EditMemoDialogFragment();
    }

    // 既存のメモを編集する時はこっち
    public static EditMemoDialogFragment newEditDialogInstance(Memo editTarget) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_MEMO, editTarget);
        EditMemoDialogFragment fragment = new EditMemoDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 親のFragment/Activity に(このダイアログの) Listener があったら保持
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_edit_memo_dialog, null);
        final EditText editText = view.findViewById(R.id.edit);

        final Memo editTarget = getEditTarget();
        if (editTarget != null) {
            editText.setText(editTarget.getText());
        }

        Dialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        if (editTarget == null) { // new
                            Memo newMemo = new Memo();
                            newMemo.setText(editText.getText().toString());
                            if (mListener != null) mListener.onEditMemoFinish(newMemo);
                        } else { // edit
                            editTarget.setText(editText.getText().toString());
                            if (mListener != null) mListener.onEditMemoFinish(editTarget);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) mListener.onEditMemoDialogCancel();
                        dismiss();
                    }
                })
                .setCancelable(false)
                .create();

        // ダイアログの中でキーボードを表示した時に、キーボードで隠れないようにする設定
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Nullable
    private Memo getEditTarget() {
        if (getArguments() == null) return null;
        return getArguments().getParcelable(EXTRA_MEMO);
    }

    /**
     * DialogFragmentから、Fragment/Activityへの通知はこれといった方法がないため
     * このインターフェースをFragment/Activityに実装して、呼び出しに行く
     * <p>
     * ...(LiveDataを使ってもいいけど)
     * </p>
     */
    public interface Listener {
        void onEditMemoFinish(Memo memo);

        void onEditMemoDialogCancel();
    }
}
