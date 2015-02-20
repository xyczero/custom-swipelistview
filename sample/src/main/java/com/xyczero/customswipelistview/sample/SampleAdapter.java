package com.xyczero.customswipelistview.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xyczero.customswipelistview.CustomSwipeBaseAdapter;

import java.util.List;

public class SampleAdapter extends CustomSwipeBaseAdapter<SampleModel> {
    private LayoutInflater mLayout;

    public SampleAdapter(Context context, List<SampleModel> docList) {
        super(context);
        this.mLayout = LayoutInflater.from(context);
        // Use setAdapterData(...) which is from the CustomSwipeBaseAdapter to
        // set data.
        setAdapterData(docList);
    }

    @Override
    public int getCount() {
        return getAdapterData().size();
    }

    @Override
    public SampleModel getItem(int position) {
        return getAdapterData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void bindItemView(View view, Context context, int position) {

        TextView textViewTitle = (TextView) view.findViewById(R.id.test_title);
        TextView textViewDate = (TextView) view.findViewById(R.id.test_date);
        textViewTitle.setText(getAdapterData().get(position).getTestTitle());
        textViewDate.setText(getAdapterData().get(position).getTestDate());
    }

    @Override
    public void bindSwipeLeftView(View view, final Context context,
                                  final int position) {
        Button buttonFb = (Button) view.findViewById(R.id.test_fb_btn);
        buttonFb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(
                        context,
                        "Facebook-"
                                + getAdapterData().get(position).getTestTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //register the listen
        Button buttonMail = (Button) view.findViewById(R.id.test_mail_btn);
        buttonMail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(
                        context,
                        "Mail-" + getAdapterData().get(position).getTestTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View newItemView(Context context, int position, ViewGroup parent) {
        final View mView = mLayout.inflate(R.layout.sample_listview_item_view,
                parent, false);
        return mView;
    }

    @Override
    public View newSwipeLeftView(Context context, int position, ViewGroup parent) {
        final View mView = mLayout.inflate(R.layout.sample_listview_swipe_view,
                parent, false);
        return mView;
    }
}
