package com.buggycoder.domo.ui.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.ui.base.BaseFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by shirish on 8/9/13.
 */

@EFragment(R.layout.frag_error)
public class ErrorFragment extends BaseFragment {

    public static final String KEY_ERROR = "error";

    @ViewById
    TextView tvError;

    @AfterViews
    protected void afterViews() {
        Bundle args = getArguments();
        tvError.setText(args.getString(KEY_ERROR, "An unexpected error occurred."));
    }
}
