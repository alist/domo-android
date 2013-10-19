package com.buggycoder.domo.ui.adapter.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.api.response.MyOrganization;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by shirish on 19/10/13.
 */
@EViewGroup(R.layout.row_myquest)
public class MyQuestionItemView extends FrameLayout {

    @ViewById
    TextView content;

    Context context;

    public MyQuestionItemView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(AdviceRequest ar) {
        content.setText(ar.getAdviceRequest());
    }
}