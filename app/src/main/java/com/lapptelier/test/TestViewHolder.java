package com.lapptelier.test;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lapptelier.smartrecyclerview.MyViewHolder;
import com.lapptelier.smartrecyclerview.ViewHolderInteractionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
class TestViewHolder extends RecyclerView.ViewHolder implements MyViewHolder<String> {
    @BindView(R.id.text)
    TextView textView;

    String cellText;

    public TestViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

    }

    @Override
    public void setItem(final String text, @NonNull final ViewHolderInteractionListener listener) {
        cellText = text;
        textView.setText(text);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(text);
            }
        });
        textView.setLongClickable(true);
    }
}
