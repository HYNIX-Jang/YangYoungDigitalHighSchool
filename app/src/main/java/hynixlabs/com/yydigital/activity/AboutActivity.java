package hynixlabs.com.yydigital.activity;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import hynixlabs.com.yydigital.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About");
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.yylogo)
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Contact")
                .addEmail("hynix_1@hynixlabs.com")
                .addWebsite("https://hynixlabs.com")
                .addFacebook("hynixJKY")
                .addTwitter("HYNIX_525")
                .addInstagram("superkinggod")
                .setDescription("Developed by HYNIX Jang\nLibrary: Jsoup")
                .addGitHub("HYNIX-JANG")
                .create();

        setContentView(aboutPage);
    }
}