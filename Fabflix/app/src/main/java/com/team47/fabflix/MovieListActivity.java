package com.team47.fabflix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Intent intent = getIntent();
        String jsonArray = intent.getStringExtra("jsonArray");
        final ArrayList<Movie> movieList = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(jsonArray);

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                String title = jsonObject.getString("movie_title");
                String year = jsonObject.getString("movie_year");
                String director = jsonObject.getString("movie_dir");


                JSONArray stars = jsonObject.getJSONArray("movie_star");
                final ArrayList<String> stars_array = new ArrayList<>();
                for (int j = 0; j < stars.length(); j++ ) {
                    stars_array.add(stars.get(j).toString());
                }

                JSONArray genres = jsonObject.getJSONArray("movie_genres");
                final ArrayList<String> genres_array = new ArrayList<>();
                for (int j = 0; j < genres.length(); j++ ) {
                    genres_array.add(genres.get(j).toString());
                }

                movieList.add(new Movie(title, year, director, stars_array, genres_array));
            }

            MovieListViewAdapter adapter = new MovieListViewAdapter(movieList, this);

            ListView listView = (ListView) findViewById(R.id.movieList);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie = movieList.get(position);
                    Intent intent = new Intent(getApplicationContext(), SingleMovieActivity.class);
                    intent.putExtra("title",movie.getTitle());
                    intent.putExtra("director",movie.getDirector());
                    intent.putExtra("year",movie.getYear());


                    String stars_text = "";
                    ArrayList<String> stars_array = movie.getStars();
                    for (int i = 0; i < stars_array.size(); ++i) {
                        stars_text += stars_array.get(i) + ", ";
                    }

                    String genres_text = "";
                    ArrayList<String> genres_array = movie.getGenres();
                    for (int i = 0; i < genres_array.size(); ++i) {
                        genres_text += genres_array.get(i) + ",";
                    }

                    intent.putExtra("stars",stars_text);
                    intent.putExtra("genres",genres_text);
                    startActivity(intent);
                }
            });

        }
        catch (JSONException error) {
            error.printStackTrace();
            Log.i("JSON Exception", error.toString());
        }

    }
}
