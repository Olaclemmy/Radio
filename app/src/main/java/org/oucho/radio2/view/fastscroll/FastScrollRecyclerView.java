package org.oucho.radio2.view.fastscroll;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.oucho.radio2.R;
import org.oucho.radio2.view.fastscroll.FastScroller.SectionIndexer;


public class FastScrollRecyclerView extends RecyclerView {

    private FastScroller mFastScroller;

    public FastScrollRecyclerView(Context context) {
        super(context);
        layout(context, null);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        layout(context, attrs);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof SectionIndexer) {
            setSectionIndexer((SectionIndexer) adapter);
        } else if (adapter == null) {
            setSectionIndexer(null);
        }
    }


    private void setSectionIndexer(SectionIndexer sectionIndexer) {
        mFastScroller.setSectionIndexer(sectionIndexer);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFastScroller.attachRecyclerView(this);

        ViewParent parent = getParent();

        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            viewGroup.addView(mFastScroller);
            mFastScroller.setLayoutParams(viewGroup);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mFastScroller.detachRecyclerView();
        super.onDetachedFromWindow();
    }

    private void layout(Context context, AttributeSet attrs) {
        mFastScroller = new FastScroller(context, attrs);
        mFastScroller.setId(R.id.fastscroller);
    }
}
