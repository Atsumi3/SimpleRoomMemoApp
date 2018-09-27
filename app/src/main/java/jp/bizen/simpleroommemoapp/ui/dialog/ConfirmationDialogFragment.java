package jp.bizen.simpleroommemoapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/**
 * 単純に Yes or No を尋ねるためのダイアログ
 */
public final class ConfirmationDialogFragment extends DialogFragment {
    private static final String EXTRA_REQUEST_ID = "request_id";
    private static final String EXTRA_BUNDLE = "bundle";

    @Nullable
    private Listener mListener;

    public static ConfirmationDialogFragment newInstance(int requestId, @Nullable Bundle bundle) {
        Bundle argument = new Bundle();
        argument.putInt(EXTRA_REQUEST_ID, requestId);
        argument.putBundle(EXTRA_BUNDLE, bundle);
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
        fragment.setArguments(argument);
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle("本当によろしいですか?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null)
                            mListener.onConfirmOk(getRequestId(), getBundle());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null)
                            mListener.onConfirmCancel(getRequestId(), getBundle());
                    }
                })
                .create();
    }

    private int getRequestId() {
        if (getArguments() == null) return -1;
        return getArguments().getInt(EXTRA_REQUEST_ID);
    }

    @Nullable
    private Bundle getBundle() {
        if (getArguments() == null) return null;
        return getArguments().getBundle(EXTRA_BUNDLE);
    }

    public interface Listener {
        void onConfirmOk(int requestId, @Nullable Bundle bundle);

        void onConfirmCancel(int requestId, @SuppressWarnings("unused") @Nullable Bundle bundle);
    }
}
