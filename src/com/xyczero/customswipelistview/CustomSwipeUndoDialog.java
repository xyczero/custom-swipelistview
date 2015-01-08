package com.xyczero.customswipelistview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xyczero.customlistview.R;

public class CustomSwipeUndoDialog {
    private OnUndoActionListener mUndoActionListener;
    private UndoDialog mUndoDialog;
    private Context mContext;
    private String mMessage;

    public CustomSwipeUndoDialog(Context context) {
        mContext = context;
    }

    public void showUndoDialog() {
        if (mUndoDialog == null) {
            mUndoDialog = new UndoDialog(mContext);
        }
        mUndoDialog.show(mMessage);
    }

    protected void closeUndoDialog() {
        if (mUndoDialog != null) {
            mUndoDialog.dismiss();
        }
    }

    public CustomSwipeUndoDialog setMessage(int textId) {
        mMessage = mContext.getResources().getString(textId);
        return this;
    }

    public CustomSwipeUndoDialog setMessage(String textString) {
        mMessage = textString;
        return this;
    }

    public void setUndoActionListener(OnUndoActionListener undoActionListener) {
        mUndoActionListener = undoActionListener;
    }

    private class UndoDialog extends Dialog implements OnClickListener {
        private static final int LAYOUT_MARGIN_SIDES = 10;// dip

        private Button mUndoBtn;
        private TextView mUndoMessage;

        public UndoDialog(Context context) {
            super(context, R.style.CommonDialog);
            setContentView(R.layout.undodialog);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setWindowAnimations(R.style.dialog_inout_anim);
            WindowManager.LayoutParams p = this.getWindow().getAttributes();
            p.width = (int) (CustomSwipeUtils.getScreenWidth(context) - 2 * CustomSwipeUtils
                    .convertDiptoPixel(context, LAYOUT_MARGIN_SIDES));
            getWindow().setAttributes(p);
            initView();
        }

        public void initView() {
            mUndoMessage = (TextView) findViewById(R.id.undo_dialog_message);
            mUndoBtn = (Button) findViewById(R.id.undo_dialog_btn);
            mUndoBtn.setOnClickListener(this);
        }

        private void show(String title) {
            mUndoMessage.setText(title);
            show();
        }

        @Override
        public void dismiss() {
            if (mUndoActionListener != null)
                mUndoActionListener.noExecuteUndoAction();
            super.dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.undo_dialog_btn:
                if (mUndoActionListener != null)
                    mUndoActionListener.executeUndoAction();
                break;
            default:
                break;
            }
            dismiss();
        }
    }
}
