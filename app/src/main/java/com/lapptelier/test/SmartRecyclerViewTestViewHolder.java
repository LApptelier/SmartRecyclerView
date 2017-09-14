package com.lapptelier.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lapptelier.smartrecyclerview.MyViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
class SmartRecyclerViewTestViewHolder extends RecyclerView.ViewHolder implements MyViewHolder<String> {
    @BindView(R.id.cell_smart_recycler_view_test_text)
    TextView textView;

    public SmartRecyclerViewTestViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void setItem(String text) {
        textView.setText(text);
    }
}
