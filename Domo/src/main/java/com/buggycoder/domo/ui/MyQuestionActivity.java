package com.buggycoder.domo.ui;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buggycoder.domo.R;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.ui.adapter.MyQuestionsAdapter;
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
import java.util.List;

/**
 * Created by shirish on 15/10/13.
 */
@EActivity(R.layout.activity_myquest)
@WindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
public class MyQuestionActivity extends BaseFragmentActivity {

    @ViewById
    TextView content;

    @ViewById
    TextView responseCount;

    @ViewById
    ListView responseList;

    @Extra
    String adviceRequestId;

    @Bean
    Config config;


    Dao<AdviceRequest, String> adviceRequestDao;

    @AfterViews
    protected void afterViews() {
        setSlidingMenu(R.layout.frag_menu, SlidingMenu.RIGHT);
        loadMyQuestion();
        loadResponses();
    }

    @Background
    protected void loadMyQuestion() {
        try {
            Dao<AdviceRequest, String> adviceRequestDao = DatabaseHelper.getDaoManager().getDao(AdviceRequest.class);
            AdviceRequest ar = adviceRequestDao.queryForId(adviceRequestId);
            if(ar != null) {
                content.setText(ar.getAdviceRequest());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Background
    protected void loadResponses() {

    }

}
