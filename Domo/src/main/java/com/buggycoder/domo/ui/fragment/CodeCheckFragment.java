package com.buggycoder.domo.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.buggycoder.domo.R;
import com.buggycoder.domo.lib.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by shirish on 8/9/13.
 */

@EFragment(R.layout.frag_codecheck)
public class CodeCheckFragment extends SherlockFragment {

    @ViewById
    EditText etOrgCode;

    @ViewById
    Button btnOrgCodeCheck;

    @AfterViews
    protected void afterViews() {
        etOrgCode.requestFocus();
        btnOrgCodeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("here");
            }
        });
    }
}
