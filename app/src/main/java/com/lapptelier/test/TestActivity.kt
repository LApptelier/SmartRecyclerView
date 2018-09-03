package com.lapptelier.test

import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.lapptelier.smartrecyclerview.*
import java.util.*

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
internal class TestActivity : AppCompatActivity(), ViewHolderInteractionListener {

    private var mRecyclerView: SmartRecyclerView? = null

    private lateinit var adapter: MultiGenericAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        mRecyclerView = findViewById(R.id.test_smart_recycler_view)

        val elements = Arrays.asList("test 1", "test 2", "test 3", "test 4", "test 5", "test 6", "test 7", "test 8")

        //configuration de la liste
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        mRecyclerView!!.setRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            Log.d("test", "FRESH-FRESH-FRESH !")
            mRecyclerView!!.displaySwipeToRefresh(false)
        })

        // Configuration de l'adapter
        adapter = MultiGenericAdapter(String::class.java, TestViewHolder::class.java, R.layout.cell_test, this)
        mRecyclerView!!.setAdapter(adapter)
        mRecyclerView!!.addItemDecoration(DrawableDividerItemDecoration(ContextCompat.getDrawable(baseContext, R.drawable.divider), null, true))
        mRecyclerView!!.emptyLoadingViewEnabled = false
        mRecyclerView!!.setAnimateLayoutChange(false)

        //on sette le texte de la vue vide
//        mRecyclerView!!.setLoadingLayout(R.layout.empty)

        Handler().postDelayed({ adapter.addAll(elements) }, 500)


    }


    override fun onItemAction(item: Any, viewId: Int, action: ViewHolderInteraction) {
        adapter.removeAt(adapter.getObjectIndex(item))
    }
}
