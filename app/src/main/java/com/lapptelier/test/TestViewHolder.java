package com.lapptelier.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.lapptelier.smartrecyclerview.MyViewHolder;

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

    @BindView(R.id.bottom_wrapper)
    LinearLayout bottom;

    public TestViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void setItem(String text) {
        textView.setText(text);

        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
//        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, bottom);

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                Toast.makeText(itemView.getContext(), "onclose", Toast.LENGTH_SHORT);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Toast.makeText(itemView.getContext(), "onUpdate", Toast.LENGTH_SHORT);
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Toast.makeText(itemView.getContext(), "startOpen", Toast.LENGTH_SHORT);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                Toast.makeText(itemView.getContext(), "onOpen", Toast.LENGTH_SHORT);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                Toast.makeText(itemView.getContext(), "onSmartClose", Toast.LENGTH_SHORT);
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                Toast.makeText(itemView.getContext(), "onHandRelease", Toast.LENGTH_SHORT);
            }
        });
    }
}
