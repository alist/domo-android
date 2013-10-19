package com.buggycoder.domo.ui;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.Advice;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.ui.adapter.MyQuestionsAdapter;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.google.android.gms.internal.ad;
import com.j256.ormlite.dao.Dao;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by shirish on 15/10/13.
 */
@EActivity(R.layout.activity_myquests)
@WindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
public class MyQuestionsActivity extends BaseFragmentActivity {

    @ViewById
    TextView title;

    @ViewById
    ImageView menuToggle;

    @ViewById
    ListView myQuestionsList;

    @Bean
    MyQuestionsAdapter myQuestionsAdapter;

    @Bean
    Config config;


    @AfterViews
    protected void afterViews() {
        setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);

        myQuestionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                AdviceRequest ar = (AdviceRequest) adapterView.getItemAtPosition(pos);
                if(ar != null) {
                    MyQuestionActivity_.intent(MyQuestionsActivity.this).adviceRequestId(ar.get_id()).start();
                }
            }
        });

        myQuestionsList.setAdapter(myQuestionsAdapter);
        loadMyQuestions();

        menuToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSlidingMenu();
            }
        });
    }

    @Background
    protected void loadMyQuestions() {
        List<AdviceRequest> adviceRequestList = null;
        try {
            Dao<AdviceRequest, String> adviceRequestDao = DatabaseHelper.getDaoManager().getDao(AdviceRequest.class);
            adviceRequestList = adviceRequestDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (adviceRequestList != null) {

            myQuestionsAdapter.clear();

            for(AdviceRequest ar : adviceRequestList) {
                myQuestionsAdapter.add(ar);
            }

            postToUi(new Runnable() {
                @Override
                public void run() {
                    myQuestionsAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
