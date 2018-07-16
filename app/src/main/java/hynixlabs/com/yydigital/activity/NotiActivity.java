package hynixlabs.com.yydigital.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hynixlabs.com.yydigital.R;
import hynixlabs.com.yydigital.adapter.NoticeAdapter;
import hynixlabs.com.yydigital.vo.NoticeItem;

public class NotiActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Context context;
    private List<NoticeItem> noticeItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("가정통신문");

        //FindViewByID
        recyclerView = findViewById(R.id.recyclerView);

        context = getApplicationContext();

        //RecyclerView 설정
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        JsoupNoticeAsyncTask jsoupNoticeAsyncTask = new JsoupNoticeAsyncTask();
        jsoupNoticeAsyncTask.execute();


    }

    public class JsoupNoticeAsyncTask extends AsyncTask<Void, Void, Void> {
        private String URL = "http://y-y.hs.kr/board.list?mcode=1712";
        private NoticeItem vo;


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(URL).get();
                noticeItems = new ArrayList<>();
                System.out.println("---------------------------------------------------------------");

                for (int i = 0; i < 20; i++) {
                    vo = new NoticeItem();
                    Elements titles = doc.select("td.title>a").eq(i);
                    Elements author = doc.select("a[href='#none']").eq(i + 1);
                    Elements view = doc.select("td.count").eq(i);
                    Elements date = doc.select("td.date").eq(i);
                    vo.setTitle(titles.text());

                    vo.setURL("http://y-y.hs.kr" + titles.attr("href"));
                    vo.setAuthor("작성자: " + author.text());
                    vo.setView("조회수: " + view.text());
                    vo.setDate("등록일: " + date.text());
                    noticeItems.add(vo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            NoticeAdapter adapter = new NoticeAdapter(context, noticeItems);
            recyclerView.setAdapter(adapter);
        }
    }

}
