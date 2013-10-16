package com.buggycoder.domo.ui.fragment;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.buggycoder.domo.R;
import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.lib.UIUtils;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

/**
 * Created by shirish on 17/10/13.
 */

@EFragment(R.layout.dialog_sel_org)
public class SelectOrgFragment extends SherlockDialogFragment {

    @ViewById
    LinearLayout dialogContainer;

    @ViewById
    AutoCompleteTextView acOrgCode;

    @ViewById
    TextView tvOrgDisplayName;

    @ViewById
    Button btnAskCode, btnCheckCode;

    @ViewById
    LinearLayout formOrg, formCode;

    @ViewById
    EditText etOrgCode;

    @Bean
    Config config;

    String selOrgURL = null;
    String selOrgDisplayName = null;
    boolean isOrgSelected = false;

    Cursor cursor = null;
    CloseableIterator<Organization> iterator = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @AfterViews
    protected void afterViews() {
        btnAskCode.setVisibility(View.GONE);
        btnCheckCode.setVisibility(View.GONE);

        formCode.setVisibility(View.GONE);

        setupOrgAutocomplete();
        acOrgCode.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btnAskCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.crossfade(formOrg, formCode, 250);
                etOrgCode.requestFocus();
            }
        });

        btnCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOrgCode(selOrgURL, etOrgCode.getText().toString());
            }
        });

        etOrgCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateOrgCode();
            }
        });
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
    public void onDestroy() {
        cleanup();
        super.onDestroy();
    }


    protected void setupOrgAutocomplete() {
        Dao<Organization, String> orgDao = null;
        QueryBuilder<Organization, String> qb = null;

        try {
            orgDao = DatabaseHelper.getDaoManager().getDao(Organization.class);
            qb = orgDao.queryBuilder();
            qb.selectColumns("displayName", "orgURL").query();
            iterator = orgDao.iterator(qb.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
            cursor = results.getRawCursor();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (cursor == null) {
            return;
        }

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getSherlockActivity(),
                R.layout.row_ac_org,
                cursor,
                new String[]{"displayName"},
                new int[]{R.id.text1}
        );

        adapter.setCursorToStringConverter(new android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex("displayName"));
            }
        });

        final QueryBuilder<Organization, String> finalQb = qb;
        final Dao<Organization, String> finalOrgDao = orgDao;

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence nameFilter) {

                try {
                    finalQb.selectColumns("displayName", "orgURL").where().like("displayName", "%" + nameFilter + "%");
                    iterator = finalOrgDao.iterator(finalQb.prepare());
                    AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
                    cursor = results.getRawCursor();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return cursor;
            }
        });

        acOrgCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
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
        });

        acOrgCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor selRow = (Cursor) adapterView.getItemAtPosition(position);
                selOrgDisplayName = selRow.getString(selRow.getColumnIndex("displayName"));
                selOrgURL = selRow.getString(selRow.getColumnIndex("orgURL"));
                isOrgSelected = true;
                validateOrgSel();
            }
        });

        acOrgCode.setAdapter(adapter);
        acOrgCode.requestFocus();
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

    protected void validateOrgCode() {
        if (etOrgCode.length() == 0) {
            btnCheckCode.setVisibility(View.GONE);
        } else {
            btnCheckCode.setVisibility(View.VISIBLE);
        }
    }

    protected void cleanup() {
        try {
            if (iterator != null) {
                iterator.closeQuietly();
            }
            if (cursor != null) {
                cursor.close();
            }
        } finally {

        }
    }

}
