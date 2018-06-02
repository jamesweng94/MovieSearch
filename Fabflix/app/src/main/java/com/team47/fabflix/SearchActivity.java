package com.team47.fabflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        title = (EditText) findViewById(R.id.searchQuery);
    }


    public void sendSearchRequest (View view) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("title", title.getText().toString());

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest searchRequest = new StringRequest(Request.Method.POST, "https://10.0.2.2:8443/project2/api/android-list",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Intent intent = new Intent(SearchActivity.this, MovieListActivity.class);
                            intent.putExtra("jsonArray",jsonArray.toString());
                            startActivity(intent);

                        }
                        catch (JSONException error) {
                            error.printStackTrace();
                            Log.i("JSON Exception", error.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("security.error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(searchRequest);


    }
}
