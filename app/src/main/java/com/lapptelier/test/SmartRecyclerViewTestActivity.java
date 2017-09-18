package com.lapptelier.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

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
public class SmartRecyclerViewTestActivity extends AppCompatActivity {

    @BindView(R.id.test_smart_recycler_view)
    SmartRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_smart_recycler_view);
        ButterKnife.bind(this);

        final List<String> elements = Arrays.asList("test 1", "test 2", "test 3");

        //configuration de la liste
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                KLog.d("FRESH-FRESH-FRESH !");
            }
        });

        // Configuration de l'adapter
        final MultiGenericAdapter adapter = new MultiGenericAdapter(String.class, SmartRecyclerViewTestViewHolder.class, R.layout.cell_smart_recycler_view_test);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //on sette le texte de la vue vide
        mRecyclerView.setLoadingLayout(R.layout.activity_test_smart_recycler_view_empty);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(elements);
            }
        }, 4000);


    }

}
