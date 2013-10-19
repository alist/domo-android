package com.buggycoder.domo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.ui.adapter.view.MyQuestionItemView;
import com.buggycoder.domo.ui.adapter.view.MyQuestionItemView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by shirish on 19/10/13.
 */
@EBean
public class MyQuestionsAdapter extends ArrayAdapter<AdviceRequest> {

    @RootContext
    Context context;


    public MyQuestionsAdapter(Context context) {
        super(context, -1);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        MyQuestionItemView myQuestionItemView;
        if (convertView == null) {
            myQuestionItemView = MyQuestionItemView_.build(context);
        } else {
            myQuestionItemView = (MyQuestionItemView) convertView;
        }

        myQuestionItemView.bind(getItem(position));
        return myQuestionItemView;
    }

}