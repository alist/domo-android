package com.buggycoder.domo.lib;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by shirish on 14/6/13.
 */

public class RequestManager {

    private static RequestManager mRequestManager = null;
    private static final int MAX_IMAGE_CACHE_ENTRIES = 100;

    private RequestQueue mRequestQueue = null;
    private static ImageLoader mImageLoader;

    private RequestManager(Context c) {
        mRequestQueue = Volley.newRequestQueue(c);
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(MAX_IMAGE_CACHE_ENTRIES));
    }

    public static void init(Context context) {
        if (mRequestManager == null) {
            Logger.d("RequestManager.init");
            mRequestManager = new RequestManager(context);
        }
    }

    public static RequestQueue getRequestQueue() {
        Logger.d("RequestManager.getRequestQueue");
        if (mRequestManager == null) {
            throw new IllegalStateException("RequestQueue not initialized");
        }
        return mRequestManager.mRequestQueue;
    }

    public static ImageLoader getImageLoader() {
        if (mRequestManager == null) {
            throw new IllegalStateException("ImageLoader not initialized");
        }
        return mRequestManager.mImageLoader;
    }
}
