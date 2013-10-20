package com.buggycoder.domo.ui;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.Advice;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.events.SupporteeEvents;
import com.buggycoder.domo.ui.adapter.ResponsesAdapter;
import com.buggycoder.domo.ui.base.BaseFragmentActivity;
import com.j256.ormlite.dao.Dao;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by shirish on 15/10/13.
 */
@EActivity(R.layout.activity_myquest)
@WindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
public class MyQuestionActivity extends BaseFragmentActivity {

    @ViewById
    TextView title;

    @ViewById
    ImageView menuToggle;

    @ViewById
    TextView content;

    @ViewById
    TextView responseCount;

    @ViewById
    ListView responseList;

    @ViewById(android.R.id.empty)
    TextView emptyView;

    @Extra
    String adviceRequestId;

    @Bean
    Config config;

    @Bean
    ResponsesAdapter responsesAdapter;


    @AfterViews
    protected void afterViews() {
        getSlidingMenuHelper().setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        menuToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSlidingMenuHelper().toggleSlidingMenu();
            }
        });

        responseList.setEmptyView(emptyView);
        responseList.setAdapter(responsesAdapter);

        loadMyQuestion();
    }

    @Background
    protected void loadMyQuestion() {

        AdviceRequest ar = null;
        try {
            Dao<AdviceRequest, String> adviceRequestDao = DatabaseHelper.getDaoManager().getDao(AdviceRequest.class);
            ar = adviceRequestDao.queryForId(adviceRequestId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (ar == null) {
            return;
        }

        final AdviceRequest finalAr = ar;

        postToUi(new Runnable() {
            @Override
            public void run() {
                content.setText(finalAr.getAdviceRequest());
            }
        });


        final Collection<Advice> responses = ar.getResponses();
        final int respCount = responses.size();

        postToUi(new Runnable() {
            @Override
            public void run() {

                responsesAdapter.clear();

                for (final Advice ad : responses) {
                    responsesAdapter.add(ad);
                }

                if (respCount == 1) {
                    responseCount.setText("1 Response");
                } else {
                    responseCount.setText(respCount + " Responses");
                }

                responsesAdapter.notifyDataSetChanged();
            }
        });

    }

    protected void onEvent(SupporteeEvents.AdviceRequestsUpdated o) {
        loadMyQuestion();
    }
}
