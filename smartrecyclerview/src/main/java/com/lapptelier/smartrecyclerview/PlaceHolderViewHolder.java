package com.lapptelier.smartrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.PlaceHolderViewHolder
 * <p/>
 * ViewHolder of a generic placeholder
 *
 * Sub class of {@link RecyclerView.ViewHolder}
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
public class PlaceHolderViewHolder extends RecyclerView.ViewHolder implements MyViewHolder<PlaceHolderCell>{

    public PlaceHolderViewHolder(View view){
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    public void setItem(final PlaceHolderCell placeHolderCell) {

    }

}
