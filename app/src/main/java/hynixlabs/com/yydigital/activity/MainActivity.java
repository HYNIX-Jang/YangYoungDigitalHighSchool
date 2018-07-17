package hynixlabs.com.yydigital.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hynixlabs.com.yydigital.R;
import hynixlabs.com.yydigital.fragment.DatePickerFragment;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private TextView txtMeal;
    private TextView txtDday;
    private TextView txtMealTitle;
    private CardView mealCardView;
    private CardView facebookCardView;
    private CardView schoolCardView;
    private CardView noticeCardView;
    private CardView notiCardView;
    private CardView ddayCardView;
    private CardView bambooFacebookCardView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean weeks;
    private int substract; //D-DAY
    private String dateString;

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
        notiCardView = findViewById(R.id.notiCardView);
        ddayCardView = findViewById(R.id.ddayCardView);
        bambooFacebookCardView = findViewById(R.id.bambooFacebookCardView);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_full);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // 네비게이션메뉴 오픈
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                float slideX = drawerView.getWidth() * slideOffset;
                CoordinatorLayout content = findViewById(R.id.appBar);
                content.setTranslationX(slideX);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_info:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        getDday();            // 실행할 시 D-DAY정보 가져옴
        isConnected();        // 실행할 시 인터넷 유무 확인 후 적절한 기능 실행

        // 새로고침 SwipeRefreshLayout 설정
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary)); //새로고침 ProgressIndicator 색 설정
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isConnected(); //인터넷 연결 재확인 후 기능 실행
            }
        });

        // Listeners
        facebookCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook("https://m.facebook.com/yyhsstudent"); //학생회 페이지
            }
        });

        bambooFacebookCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook("https://www.facebook.com/YYbambo0/"); //대나무숲 페이지
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

        mealCardView.setOnLongClickListener(new View.OnLongClickListener() { //길게 터치하면 DatePicker 나옴
            @Override
            public boolean onLongClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "DatePicker");
                return true;
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
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));

            }
        });

        notiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotiActivity.class));
            }
        });

        ddayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "2018년 여름방학까지 " + Math.abs(substract) + "일 남았습니다.";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    //아래는 전부 NavigationBar설정
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 인터넷 연결 확인
    private void isConnected() {
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
        substract = (int) (l_today - l_dday);
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

    // 페이스북앱으로 이동
    private void openFacebook(String url) {
        String URI = "fb://facewebmodal/f?href=" + url;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    //DatePicker 데이터 설정
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
        dateString = dateFormat.format(calendar.getTime());
        getMeal("other");
    }


    // 급식 AsyncTask
    @SuppressLint("StaticFieldLeak")
    public class JsoupMealAsyncTask extends AsyncTask<Void, Void, Void> {
        private String URL = "http://y-y.hs.kr/lunch.view?date=";
        private String meal = "";
        private String status;

        JsoupMealAsyncTask(String status) {
            this.status = status;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Calendar calendar = Calendar.getInstance();
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
                    case "other":
                        URL += dateString;
                        txtMealTitle.setText(dateString.substring(2) + "의 급식 식단표");
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
                txtMeal.setText("급식이 없습니다");
            } else {
                System.out.println("Print Meal");
                String[] mealSplit = meal.split("\\.");
                StringBuilder sum = new StringBuilder();
                System.out.println(mealSplit.length);
                for (String aMealSplit : mealSplit) sum.append(aMealSplit).append("\n");
                System.out.println(sum);
                txtMeal.setText(sum.toString().trim());
            }
        }
    }
}