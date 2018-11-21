package org.oucho.radio2.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

public class AutoScrollableTextView extends AppCompatTextView {

    public AutoScrollableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setSingleLine();
        setHorizontallyScrolling(true);
    }

    public AutoScrollableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setSingleLine();
        setHorizontallyScrolling(true);
    }

    public AutoScrollableTextView(Context context) {
        super(context);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setSingleLine();
        setHorizontallyScrolling(true);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onWindowFocusChanged(boolean focused) {
            super.onWindowFocusChanged(focused);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
