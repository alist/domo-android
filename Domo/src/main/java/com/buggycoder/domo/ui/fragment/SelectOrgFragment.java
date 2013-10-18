package com.buggycoder.domo.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.lib.UIUtils;
import com.buggycoder.domo.ui.adapter.AutoCompleteOrgAdapter;
import com.buggycoder.domo.ui.base.BaseDialogFragment;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;

/**
 * Created by shirish on 17/10/13.
 */

@EFragment(R.layout.dialog_sel_org)
public class SelectOrgFragment extends BaseDialogFragment {

    @ViewById
    LinearLayout dialogContainer;

    @ViewById
    AutoCompleteTextView acOrgCode;

    @ViewById
    Button btnAskCode, btnCheckCode;

    @ViewById
    LinearLayout formOrg, formCode;

    @ViewById
    EditText etOrgCode;

    @ViewById
    TextView tvOrgDisplayName;

    @Bean
    AutoCompleteOrgAdapter orgAdapter;

    @Bean
    Config config;

    @InstanceState
    String selOrgURL = null, selOrgDisplayName = null;

    @InstanceState
    boolean isOrgSelected = false, isOrgSelComplete = false;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @AfterViews
    protected void afterViews() {

        if(isOrgSelComplete) {
            UIUtils.crossfade(formOrg, formCode, 100);
            etOrgCode.requestFocus();
            return;
        }

        btnAskCode.setVisibility(View.GONE);
        btnCheckCode.setVisibility(View.GONE);
        formCode.setVisibility(View.GONE);

        setupOrgAutocomplete();
        acOrgCode.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnAskCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOrgSelected = isOrgSelComplete = true;
                validateOrgSel();
                UIUtils.crossfade(formOrg, formCode, 200);
                etOrgCode.requestFocus();
            }
        });

        btnCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOrgCode(selOrgURL, etOrgCode.getText().toString());
            }
        });

        tvOrgDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToOrgSel();
            }
        });

    }


    @AfterTextChange(R.id.etOrgCode)
    void onTextChangeOrgCode(Editable text, TextView etOrgCode) {
        if (etOrgCode.length() > 3) {
            btnCheckCode.setVisibility(View.VISIBLE);
        } else {
            btnCheckCode.setVisibility(View.GONE);
        }
    }


    @AfterTextChange(R.id.acOrgCode)
    void onTextChangeOrgName(Editable editable, TextView orgCode) {

        final String newText = editable.toString();
        if(newText == null || selOrgDisplayName == null) {
            return;
        }

        if(newText.equalsIgnoreCase(selOrgDisplayName)) {
            isOrgSelected = true;
        } else {
            isOrgSelected = false;
        }

        validateOrgSel();
    }

    public void backToOrgSel() {
        tvOrgDisplayName.setText("");
        etOrgCode.setText("");
        isOrgSelComplete = false;
        UIUtils.crossfade(formCode, formOrg, 200);
    }

    public void resetOrgSel() {
        selOrgDisplayName = selOrgURL = null;
        acOrgCode.setText("");
        isOrgSelected = false;
        backToOrgSel();
    }


    protected void validateOrgSel() {
        if(isOrgSelected) {
            tvOrgDisplayName.setText(selOrgDisplayName);
            btnAskCode.setVisibility(View.VISIBLE);
        } else {
            tvOrgDisplayName.setText("");
            btnAskCode.setVisibility(View.GONE);
        }
    }


    @Background
    protected void checkOrgCode(String orgURL, String orgCode) {
        try {
            OrganizationAPI.checkCode(config, orgURL, orgCode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        resetOrgSel();
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    protected void setupOrgAutocomplete() {
        acOrgCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Organization selOrg = (Organization) adapterView.getItemAtPosition(position);
                selOrgDisplayName = selOrg.getDisplayName();
                selOrgURL = selOrg.getOrgURL();
                isOrgSelected = true;
                validateOrgSel();
            }
        });

        acOrgCode.setAdapter(orgAdapter);
        acOrgCode.requestFocus();
    }


    public boolean isOrgSelComplete() {
        return isOrgSelComplete;
    }
}
