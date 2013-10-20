package com.buggycoder.domo.ui.adapter.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.Advice;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by shirish on 19/10/13.
 */
@EViewGroup(R.layout.row_response)
public class ResponseItemView extends FrameLayout {

    @ViewById
    TextView content;

    Context context;

    public ResponseItemView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Advice ad) {
        content.setText(ad.getAdviceResponse());
    }
}