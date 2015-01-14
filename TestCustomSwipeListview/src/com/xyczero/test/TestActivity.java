package com.xyczero.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.xyczero.customlistview.R;
import com.xyczero.customswipelistview.CustomSwipeListView;
import com.xyczero.customswipelistview.CustomSwipeListView.RemoveItemCustomSwipeListener;
import com.xyczero.customswipelistview.CustomSwipeUndoDialog;

public class TestActivity extends Activity implements
        RemoveItemCustomSwipeListener {
    private CustomSwipeListView mTestListView;
    private TestAdapter mTestAdapter;
    private List<TestModel> mTestModels = new ArrayList<TestModel>();
    private CustomSwipeUndoDialog mUndoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        makeData();

        mTestListView = (CustomSwipeListView) findViewById(R.id.list);

        // Use the constructor to initialize the TestAdapter.
        mTestAdapter = new TestAdapter(this, mTestModels);

        // create a undoDialog and set listener to it.
        mUndoDialog = new CustomSwipeUndoDialog(this);
        mUndoDialog.setUndoActionListener(mTestAdapter);

        mTestListView.setAdapter(mTestAdapter);
        // set itemClickListener to the CustomSwipeListView.
        mTestListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Toast.makeText(
                        TestActivity.this,
                        "Click item-" + mTestModels.get(position).getTestDate(),
                        Toast.LENGTH_SHORT).show();
            }

        });

        // set removeItemListener to the CustomSwipeListView.
        mTestListView.setRemoveItemCustomSwipeListener(this);

        // set some property for the CustomSwipeListView.
        mTestListView.setSwipeItemLeftEnable(true);
        mTestListView.setSwipeItemRightEnable(true);
        mTestListView.setAnimationLeftDuration(300);
        mTestListView.setAnimationRightDuration(300);
        // mTestListView.setSwipeItemLeftTriggerDeltaX(50);
        // mTestListView.setSwipeItemRightTriggerDeltaX(50);
    }

    /**
     * Generate some data that is shown in the CustomSwipeListview.
     */
    private void makeData() {
        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setTestDate("2015-01-0" + i);
            model.setTestTitle("TestItem" + i);
            mTestModels.add(model);
        }
    }

    /**
     * implement the interface which is used to listen the remove item event.
     */
    @Override
    public void onRemoveItemListener(int selectedPostion) {
        // get the object which has been deleted.
        TestModel model = mTestAdapter.removeItemByPosition(selectedPostion);
        // set some message and show the undoDialog
        mUndoDialog.setMessage("Delete" + model.getTestTitle() + ".")
                .showUndoDialog();
    }
}
