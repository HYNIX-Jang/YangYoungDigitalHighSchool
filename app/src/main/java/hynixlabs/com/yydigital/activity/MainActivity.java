package hynixlabs.com.yydigital.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
    private CardView mealCardView;
    private CardView facebookCardView;
    private CardView schoolCardView;
    private CardView noticeCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FindViewByID
        txtMeal = findViewById(R.id.txtMeal);
        mealCardView = findViewById(R.id.mealCardView);
        facebookCardView = findViewById(R.id.facebookCardView);
        schoolCardView = findViewById(R.id.schoolCardView);
        noticeCardView = findViewById(R.id.noticeCardView);


        //툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 실행할 시 오늘날짜 급식 가져옴
        getMeal("today");

        mealCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMeal("tommorow");
            }
        });

        facebookCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook();
            }
        });
    }

    //해당 요일 급식 가져오는 메소드
    private void getMeal(String week) {
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask(week);
        jsoupAsyncTask.execute();
    }

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
    public class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        private String URL = "http://y-y.hs.kr/lunch.view?date=";
        private String meal = "";
        private String status;

        JsoupAsyncTask(String status) {
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                meal = "안녕하세요.저는.장국영.이라고.합니다";
                String[] mealSplit = meal.split("\\.");
                StringBuilder sum = new StringBuilder();
                System.out.println(mealSplit.length);
                for (int i = 0; i < mealSplit.length; i++) {
                    sum.append(mealSplit[i]).append("\n");
                    System.out.println(i);
                }
                System.out.println(sum);
                txtMeal.setText(sum.toString().trim());
            }
        }
    }
}