package hynixlabs.com.yydigital.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hynixlabs.com.yydigital.R;

public class MainActivity extends AppCompatActivity {


    private TextView txtMeal;
    private TextView txtDday;
    private TextView txtMealTitle;
    private CardView mealCardView;
    private CardView facebookCardView;
    private CardView schoolCardView;
    private CardView noticeCardView;
    private CardView ddayCardView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean weeks;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FindViewByID
        mSwipeRefreshLayout = findViewById(R.id.mainCardview);
        txtMeal = findViewById(R.id.txtMeal);
        txtDday = findViewById(R.id.txtDday);
        txtMealTitle = findViewById(R.id.txtMealTitle);
        mealCardView = findViewById(R.id.mealCardView);
        facebookCardView = findViewById(R.id.facebookCardView);
        schoolCardView = findViewById(R.id.schoolCardView);
        noticeCardView = findViewById(R.id.noticeCardView);
        ddayCardView = findViewById(R.id.ddayCardView);


        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDday();            // 실행할 시 D-DAY정보 가져옴
        isConnected();        // 실행할 시 인터넷 유무 확인 후 적절한 기능 실행

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary)); //새로고침 ProgressIndicator 색 설정
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isConnected(); //인터넷 연결 재확인 후 기능 실행
            }
        });

        mealCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weeks) { //현재 오늘일 때 클릭하면 내일급식을 가져옴
                    getMeal("tommorow");
                } else { //현재 내일일 때 클릭하면 오늘급식을 가져옴
                    getMeal("today");
                }
            }
        });

        facebookCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook();
            }
        });
        schoolCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "http://y-y.hs.kr/";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
            }
        });

        noticeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
                startActivity(intent);
            }
        });


    }

    private void isConnected() {
        // 인터넷 연결 확인
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            getMeal("today");            // 실행할 시 오늘날짜 급식 가져옴
        } else { //인터넷 연결이 안되어있을시
            Toast.makeText(this, "네트워크 연결을 확인해주세요!", Toast.LENGTH_LONG).show();
            txtMeal.setText("네트워크 연결을 확인해주세요!");
            txtMeal.setTextColor(Color.RED);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    //D-DAY를 가져오는 메소드
    @SuppressLint("SetTextI18n")
    private void getDday() {
        Calendar today = Calendar.getInstance();
        Calendar d_day = Calendar.getInstance();

        d_day.set(2018, Calendar.JULY, 20);
        long l_dday = d_day.getTimeInMillis() / (24 * 60 * 60 * 1000);
        long l_today = today.getTimeInMillis() / (24 * 60 * 60 * 1000);
        int substract = (int) (l_today - l_dday);
        txtDday.setText("D" + Integer.toString(substract));
    }

    //해당 요일 급식 가져오는 메소드
    private void getMeal(String week) {
        mSwipeRefreshLayout.setRefreshing(true);
        JsoupMealAsyncTask jsoupMealAsyncTask = new JsoupMealAsyncTask(week);
        jsoupMealAsyncTask.execute();
        if (week.equals("today")) {
            txtMealTitle.setText("오늘의 급식 식단표");
            weeks = true;
        } else {
            txtMealTitle.setText("내일의 급식 식단표");
            weeks = false;
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    //학생회 페이스북앱으로 이동
    private void openFacebook() {
        String URL = "https://m.facebook.com/yyhsstudent";
        String URI = "fb://facewebmodal/f?href=" + URL;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class JsoupMealAsyncTask extends AsyncTask<Void, Void, Void> {
        private String URL = "http://y-y.hs.kr/lunch.view?date=";
        private String meal = "";
        private String status;

        JsoupMealAsyncTask(String status) {
            this.status = status;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(System.currentTimeMillis())); //현재 시간
                switch (status) {
                    case "today":
                        URL += new SimpleDateFormat("yyyyMMdd", Locale.KOREA)
                                .format(calendar.getTime());
                        break;
                    case "tommorow":
                        calendar.add(Calendar.DATE, 1);
                        URL += new SimpleDateFormat("yyyyMMdd", Locale.KOREA)
                                .format(calendar.getTime());
                        break;
                }
                Document doc = Jsoup.connect(URL).get();
                Elements titles = doc.select(".menuName span");
                System.out.println("---------------------------------------------------------------");
                for (Element e : titles) {
                    System.out.println("Meal: " + e.text());
                    meal = e.text().trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (meal.isEmpty()) {
                System.out.println("No Meal");
                txtMeal.setText("오늘은 급식이 없습니다");
            } else {
                System.out.println("Print Meal");
                String[] mealSplit = meal.split("\\.");
                StringBuilder sum = new StringBuilder();
                System.out.println(mealSplit.length);
                for (String aMealSplit : mealSplit) {
                    sum.append(aMealSplit).append("\n");
                }
                System.out.println(sum);

                txtMeal.setText(sum.toString().trim());
            }
        }
    }
}