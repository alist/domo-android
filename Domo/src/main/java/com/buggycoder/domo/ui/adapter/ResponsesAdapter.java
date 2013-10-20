package com.buggycoder.domo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.buggycoder.domo.api.response.Advice;
import com.buggycoder.domo.ui.adapter.view.ResponseItemView;
import com.buggycoder.domo.ui.adapter.view.ResponseItemView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by shirish on 19/10/13.
 */
@EBean
public class ResponsesAdapter extends ArrayAdapter<Advice> {

    @RootContext
    Context context;


    public ResponsesAdapter(Context context) {
        super(context, -1);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ResponseItemView responseItemView;
        if (convertView == null) {
            responseItemView = ResponseItemView_.build(context);
        } else {
            responseItemView = (ResponseItemView) convertView;
        }

        responseItemView.bind(getItem(position));
        return responseItemView;
    }

}