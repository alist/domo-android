package com.buggycoder.domo.ui.fragment;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;

import com.actionbarsherlock.app.SherlockFragment;
import com.buggycoder.domo.R;
import com.buggycoder.domo.api.request.OrganizationAPI;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.lib.Logger;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

/**
 * Created by shirish on 8/9/13.
 */

@EFragment(R.layout.frag_codecheck)
public class CodeCheckFragment extends SherlockFragment {

    @ViewById
    EditText etOrgCode;

    @ViewById
    Button btnOrgCodeCheck;

    @ViewById
    AutoCompleteTextView acOrgCode;

    @OrmLiteDao(helper = DatabaseHelper.class, model = Organization.class)
    Dao<Organization, String> orgDao;


    String selOrgURL = null;

    Cursor cursor = null;
    CloseableIterator<Organization> iterator = null;

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
            OrganizationAPI.checkCode(orgURL, orgCode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void setupOrgAutocomplete() {

        final QueryBuilder<Organization, String> qb = orgDao.queryBuilder();

        try {
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
                android.R.layout.simple_expandable_list_item_1,
                cursor,
                new String[]{"displayName"},
                new int[]{android.R.id.text1}
        );

        adapter.setCursorToStringConverter(new android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex("displayName"));
            }
        });

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence nameFilter) {

                try {
                    qb.selectColumns("displayName", "orgURL").where().like("displayName", "%" + nameFilter + "%");
                    iterator = orgDao.iterator(qb.prepare());
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
