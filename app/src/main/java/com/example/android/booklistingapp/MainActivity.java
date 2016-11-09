package com.example.android.booklistingapp;

import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHORS = "authors";
    private static String urlString;
    ArrayList<HashMap<String, String>> bookList;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.book_list);

        Button btn = (Button) findViewById(R.id.search_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.search_txt);
                String query = input.getText().toString().replace(" ", "+");
                urlString = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&orderBy=newest";
                new ParseJSON().execute(urlString);
            }
        });
    }

    /*TO DO:    1) Parse information from JSON and convert to String
                2) Compare search from user in EditText with JSON String conversion updateUI accordingly
                3) Ensure search is done in the background (would be better to use AsyncTaskLoader to avoid memory leaks)
                4) Check for Empty List (Use ProgressBar in XML, hide after search has been conducted)
                5) Add information for user on Empty Screen before search button is pressed and when app is first launched
    */

    private class ParseJSON extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = urlString;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");

                    // looping through all Books
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject books = items.getJSONObject(i);

                        // volumeInfo node is JSON Object
                        JSONObject volumeInfo = books.getJSONObject("volumeInfo");
                        String title = volumeInfo.getString("title");
                        String authors = volumeInfo.getString("authors");

                        if (volumeInfo.has("authors")){
                            authors = authors.replace("[", "");
                            authors = authors.replace("]", "");
                        }
                        else{
                            authors = "Unknown";
                        }


                        // tmp hash map for single book
                        HashMap<String, String> book = new HashMap<>();

                        // adding each child node to HashMap key => value
                        book.put(TAG_TITLE, title);
                        book.put(TAG_AUTHORS, authors);


                        // adding book to book list
                        bookList.add(book);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Sorry, please enter a valid title or author.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Loading results, please wait...", Toast.LENGTH_LONG).show();
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, bookList,
                    R.layout.list_item, new String[]{"title","authors"},
                    new int[]{R.id.book_title, R.id.book_authors});
            lv.setAdapter(adapter);
        }
    }
}//End of MainActivity





