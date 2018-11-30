package com.awn.app.movietoday;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//import com.awn.app.databasemovie.reminder.PreferenceCondition;

public class PreferenceActivityzzzz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceCondition()).commit();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}