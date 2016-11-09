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

    private void updateUI(Book book) {

        TextView titleTextView = (TextView) findViewById(R.id.book_title);
        titleTextView.setText(book.title);

        TextView authorsTextView = (TextView) findViewById(R.id.book_authors);
        authorsTextView.setText(book.authors);
    }

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


                    // looping through all books
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);
                        String title = c.getString("title");
                        String authors;

                        JSONObject VolumeDetails= jsonObj.getJSONObject("volumeInfo");

                        if (VolumeDetails.has("authors")){
                            authors = (VolumeDetails.getString("authors"));
                            authors = authors.replace("[", "");
                            authors = authors.replace("]", "");
                        }
                        else{
                            authors = "";

                        }

                        HashMap<String, String> bookInfo = new HashMap<>();

                        // adding each child node to HashMap key => value
                        bookInfo.put("title", title);
                        bookInfo.put("authors", authors);


                        // adding book to listing
                        bookList.add(bookInfo);
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
                                "Couldn't get json from server. Check LogCat for possible errors!",
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
        protected void onPostExecute(Void book) {
            super.onPostExecute(book);

            ListAdapter adapter = new SimpleAdapter(MainActivity.this,
                    bookList,
                    R.layout.activity_main,
                    new String[]{TAG_TITLE, TAG_AUTHORS},
                    new int[]{ R.id.book_title, R.id.book_authors}
            );
            lv.setAdapter(adapter);

            /*if (book == null) {
                return;
            }*/
            //updateUI(book);
        }
    }


}//End of MainActivity





