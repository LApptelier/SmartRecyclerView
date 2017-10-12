package com.lapptelier.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lapptelier.smartrecyclerview.MyViewHolder;
import com.lapptelier.smartrecyclerview.swipe.SwipeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
class TestViewHolder extends RecyclerView.ViewHolder implements MyViewHolder<String> {
    @BindView(R.id.text)
    TextView textView;

    @BindView(R.id.swipe)
    SwipeLayout swipeLayout;

    String cellText;

    public TestViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

    }

    @Override
    public void setItem(final String text) {
        cellText = text;
        textView.setText(text);

    }

}
