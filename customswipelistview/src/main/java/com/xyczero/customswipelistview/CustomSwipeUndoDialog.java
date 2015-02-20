/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, xyczero <xiayuncheng1991@gmail.com>
 *  
 *  	http://www.xyczero.com/
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    CustomSwipeUndoDialog.java
 *  @brief   Custom Swipe Undo Dialog
 *  
 *  @version 1.0     
 *  @author  xyczero
 *  @date    2015/01/12    
 */
package com.xyczero.customswipelistview;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Control the initialization,showing and closing of an inner class UndoDialog.
 *
 * @author xyczero
 */
public class CustomSwipeUndoDialog {

    private Context mContext;

    /**
     * The message will been shown in the dialog.
     */
    private String mMessage;

    private UndoDialog mUndoDialog;

    /**
     * The listener that receives notifications when the mUndoBtn has been
     * clicked or the dialog is dismissed.
     */
    private OnUndoActionListener mUndoActionListener;

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

    /**
     * set the message that will been shown in the dialog.
     *
     * @param textId resource id of the string
     * @return
     */
    public CustomSwipeUndoDialog setMessage(int textId) {
        mMessage = mContext.getResources().getString(textId);
        return this;
    }

    /**
     * same as {@link #setMessage(int)}
     *
     * @param textString message string
     * @return
     */
    public CustomSwipeUndoDialog setMessage(String textString) {
        mMessage = textString;
        return this;
    }

    /**
     * Register a callback to be invoked when the mUndoBtn has been clicked or
     * the dialog is dismissed.
     *
     * @param undoActionListener
     */
    public void setUndoActionListener(OnUndoActionListener undoActionListener) {
        mUndoActionListener = undoActionListener;
    }

    /**
     * A custom dialog inner class. Used to show some custom message by
     * executing the removing action.
     *
     * @author xyczero
     */
    private class UndoDialog extends Dialog implements OnClickListener {

        /**
         * The length of the distance on both sides in dip.
         */
        private static final int LAYOUT_MARGIN_SIDES = 10;

        private Button mUndoBtn;
        private TextView mUndoMessage;

        public UndoDialog(Context context) {
            super(context, R.style.CommonDialog);
            setContentView(R.layout.customswipe_undodialog_view);
            getWindow().setGravity(Gravity.BOTTOM);
            getWindow().setWindowAnimations(R.style.dialog_inout_anim);

            // TODO:To be optimized provide custom interface.
            // Custom the location and size of the dialog.
            WindowManager.LayoutParams p = this.getWindow().getAttributes();
            p.width = (int) (CustomSwipeUtils.getScreenWidth(context) - 2 * CustomSwipeUtils
                    .convertDptoPx(context, LAYOUT_MARGIN_SIDES));
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

        /**
         * If {@link #mUndoActionListener} is not null ,it will perform
         * noExecuteUndoAction() before calling dismiss().
         */
        @Override
        public void dismiss() {
            if (mUndoActionListener != null) {
                mUndoActionListener.noExecuteUndoAction();
            }
            super.dismiss();
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.undo_dialog_btn) {
                if (mUndoActionListener != null)
                    mUndoActionListener.executeUndoAction();
            }
            dismiss();
        }
    }
}
