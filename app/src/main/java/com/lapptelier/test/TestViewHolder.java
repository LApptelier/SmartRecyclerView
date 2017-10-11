package com.lapptelier.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.lapptelier.smartrecyclerview.MyViewHolder;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    String cellText;

    public TestViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

    }


    @Override
    public void setItem(final String text) {

        cellText = text;

        textView.setText(text);

        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                KLog.d("onclose");
                setBus(false);

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                KLog.d("onUpdate");
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                KLog.d("startOpen");
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                KLog.d("onOpen");
                setBus(true);
                EventBus.getDefault().post(text);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                KLog.d("onSmartClose");
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                KLog.d("onHandRelease");
            }
        });
    }

    public void setBus(boolean subscribe) {
        if (subscribe) {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } else {
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe
    public void onMessageEvent(String message) {
        if (!cellText.equals(message))
            swipeLayout.close(true);
    }
}
