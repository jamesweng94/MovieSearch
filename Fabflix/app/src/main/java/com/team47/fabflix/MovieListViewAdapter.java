package com.team47.fabflix;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movieList;

    public MovieListViewAdapter(ArrayList<Movie> movieList, Context context) {
        super(context, R.layout.layout_movielist_row, movieList);
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_movielist_row, parent, false);

        Movie movie = movieList.get(position);

        TextView title = (TextView)view.findViewById(R.id.movieTitle);
        TextView year = (TextView)view.findViewById(R.id.movieYear);
        TextView director = (TextView)view.findViewById(R.id.movieDirector);
        TextView stars = (TextView)view.findViewById(R.id.movieStars);
        TextView genres = (TextView)view.findViewById(R.id.movieGenres);

        title.setText("Title: " + movie.getTitle());
        year.setText("Year: " + movie.getYear());
        director.setText("Director: " + movie.getDirector());

        String stars_text = "Stars: ";
        ArrayList<String> stars_array = movie.getStars();
        for (int i = 0; i < stars_array.size(); ++i) {
            stars_text += stars_array.get(i) + ", ";
        }
        stars.setText(stars_text.trim());

        String genres_text = "Genres: ";
        ArrayList<String> genres_array = movie.getGenres();
        for (int i = 0; i < genres_array.size(); ++i) {
            genres_text += genres_array.get(i) + ",";
        }
        genres.setText(genres_text);

        return view;
    }
}
