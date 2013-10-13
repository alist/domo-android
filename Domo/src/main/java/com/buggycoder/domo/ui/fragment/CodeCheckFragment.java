package com.buggycoder.domo.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.OrganizationAPI;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.ui.GetAdviceActivity;
import com.buggycoder.domo.ui.GetAdviceActivity_;
import com.buggycoder.domo.ui.base.BaseFragment;
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

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by shirish on 8/9/13.
 */

@EFragment(R.layout.frag_codecheck)
public class CodeCheckFragment extends BaseFragment {

    @ViewById
    EditText etOrgCode;
    @ViewById
    Button btnOrgCodeCheck;
    @ViewById
    AutoCompleteTextView acOrgCode;

    String selOrgURL = null;
    Cursor cursor = null;
    CloseableIterator<Organization> iterator = null;

    @Bean
    Config config;


    @AfterViews
    protected void afterViews() {

        btnOrgCodeCheck.setEnabled(false);

        etOrgCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateForm();
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
                validateForm();
            }
        });

        btnOrgCodeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orgCode = etOrgCode.getText().toString();
                checkOrgCode(selOrgURL, orgCode);
            }
        });

        setupOrgAutocomplete();
    }

    protected void validateForm() {
        if (etOrgCode.length() == 0 || acOrgCode.length() == 0 || selOrgURL == null || selOrgURL.length() == 0) {
            btnOrgCodeCheck.setEnabled(false);
        } else {
            btnOrgCodeCheck.setEnabled(true);
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

    protected void onEventMainThread(OrganizationEvents.CheckOrgCodeResult o) {
        Logger.d("F:CheckOrgCodeResult");

        if (o.result.hasError) {
            Crouton.makeText(getSherlockActivity(), o.result.errors.get(0), Style.ALERT).show();
            return;
        }

        MyOrganization myOrg = o.result.getResponse();
        Logger.dump(myOrg);

        if (myOrg.getCode().length() > 0) {
            Crouton.makeText(getSherlockActivity(), "Valid code!", Style.INFO).show();

            Bundle bundle = new Bundle();
            bundle.putString(GetAdviceActivity.KEY_ORGURL, myOrg.getOrgURL());
            bundle.putString(GetAdviceActivity.KEY_ORGCODE, myOrg.getCode());
            bundle.putString(GetAdviceActivity.KEY_ORGDISPLAYNAME, myOrg.getDisplayName());

            openActivity(GetAdviceActivity_.class, bundle, true);
        }
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
                    Logger.d("Query has " + cursor.getCount() + " rows of description for " + nameFilter);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return cursor;
            }
        });

        acOrgCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor selRow = (Cursor) adapterView.getItemAtPosition(position);
                selOrgURL = selRow.getString(selRow.getColumnIndex("orgURL"));
                Logger.d("selOrgURL: " + selOrgURL + " | id: " + id + " | Cursor? : " + (selRow == null));
            }
        });

        acOrgCode.setAdapter(adapter);
        acOrgCode.requestFocus();
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

    @Override
    public void onDestroy() {
        cleanup();
        super.onDestroy();
    }
}
