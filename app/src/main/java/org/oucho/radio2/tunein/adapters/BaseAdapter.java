package org.oucho.radio2.tunein.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class BaseAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    protected void triggerOnItemClickListener(int position, View view) {
        if(mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(position, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }


}
