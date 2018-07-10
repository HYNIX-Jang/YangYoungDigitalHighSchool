package hynixlabs.com.yydigital.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import hynixlabs.com.yydigital.R;

public class NoticeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //FindViewByID
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);



    }
}
