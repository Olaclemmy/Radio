package org.oucho.radio2.tunein.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;


abstract class BaseLoader<D> extends AsyncTaskLoader<D> {

    private D mData;

    BaseLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {

        if (mData != null) {
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        mData = null;
        onStopLoading();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    public void deliverResult(D data) {
        if (!isReset()) {
            super.deliverResult(data);
        }
    }

}
