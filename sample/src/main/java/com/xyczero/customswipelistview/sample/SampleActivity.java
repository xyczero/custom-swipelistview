package com.xyczero.customswipelistview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.xyczero.customswipelistview.CustomSwipeListView;
import com.xyczero.customswipelistview.CustomSwipeUndoDialog;

import java.util.ArrayList;
import java.util.List;

public class SampleActivity extends Activity implements
        CustomSwipeListView.RemoveItemCustomSwipeListener {
    private CustomSwipeListView mSampleListView;
    private SampleAdapter mSampleAdapter;
    private List<SampleModel> mSampleModels = new ArrayList<SampleModel>();
    private CustomSwipeUndoDialog mUndoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity);

        makeData();

        mSampleListView = (CustomSwipeListView) findViewById(R.id.sample_list);

        // Use the constructor to initialize the SampleAdapter.
        mSampleAdapter = new SampleAdapter(this, mSampleModels);

        // create a undoDialog and set listener to it.
        mUndoDialog = new CustomSwipeUndoDialog(this);
        mUndoDialog.setUndoActionListener(mSampleAdapter);

        mSampleListView.setAdapter(mSampleAdapter);
        // set itemClickListener to the CustomSwipeListView.
        mSampleListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(
                        SampleActivity.this,
                        "Click item-" + mSampleModels.get(position).getTestDate(),
                        Toast.LENGTH_SHORT).show();
            }

        });

        // set removeItemListener to the CustomSwipeListView.
        mSampleListView.setRemoveItemCustomSwipeListener(this);

        // set some property for the CustomSwipeListView.
        mSampleListView.setSwipeItemLeftEnable(true);
        mSampleListView.setSwipeItemRightEnable(true);
        mSampleListView.setAnimationLeftDuration(300);
        mSampleListView.setAnimationRightDuration(300);
        // mSampleListView.setSwipeItemLeftTriggerDeltaX(50);
        // mSampleListView.setSwipeItemRightTriggerDeltaX(50);
    }

    /**
     * Generate some data that is shown in the CustomSwipeListview.
     */
    private void makeData() {
        for (int i = 0; i < 10; i++) {
            SampleModel model = new SampleModel();
            model.setTestDate("2015-01-0" + i);
            model.setTestTitle("TestItem" + i);
            mSampleModels.add(model);
        }
    }

    /**
     * implement the interface which is used to listen the remove item event.
     */
    @Override
    public void onRemoveItemListener(int selectedPostion) {
        // get the object which has been deleted.
        SampleModel model = mSampleAdapter.removeItemByPosition(selectedPostion);
        // set some message and show the undoDialog
        mUndoDialog.setMessage("Delete" + model.getTestTitle() + ".")
                .showUndoDialog();
    }
}
