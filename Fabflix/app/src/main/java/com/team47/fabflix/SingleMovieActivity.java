package com.team47.fabflix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        Bundle bundle = getIntent().getExtras();
        ((TextView) findViewById(R.id.movieTitle)).setText("Title: " + bundle.getString("title"));
        ((TextView) findViewById(R.id.movieYear)).setText("Year: " + bundle.getString("year"));
        ((TextView) findViewById(R.id.movieDirector)).setText("Director: " + bundle.getString("director"));
        ((TextView) findViewById(R.id.movieStars)).setText("Stars: " + bundle.getString("stars"));
        ((TextView) findViewById(R.id.movieGenres)).setText("Genres: " + bundle.getString("genres"));

    }
}
