package com.lapptelier.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.lapptelier.smartrecyclerview.DrawableDividerItemDecoration;
import com.lapptelier.smartrecyclerview.MultiGenericAdapter;
import com.lapptelier.smartrecyclerview.SmartRecyclerView;
import com.socks.library.KLog;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
public class TestActivity extends AppCompatActivity  {

    @BindView(R.id.test_smart_recycler_view)
    SmartRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        final List<String> elements = Arrays.asList("test 1", "test 2", "test 3", "test 4", "test 5", "test 6","test 7", "test 8");

        //configuration de la liste
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                KLog.d("FRESH-FRESH-FRESH !");
                mRecyclerView.displaySwipeToRefresh(false);
            }
        });

        // Configuration de l'adapter
        final MultiGenericAdapter adapter = new MultiGenericAdapter(String.class, TestViewHolder.class, R.layout.cell_test);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DrawableDividerItemDecoration(getDrawable(R.drawable.divider), null, true));

        //on sette le texte de la vue vide
        mRecyclerView.setLoadingLayout(R.layout.empty);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(elements);
            }
        }, 1000);


    }
}
