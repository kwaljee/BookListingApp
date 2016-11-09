package com.example.android.booklistingapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
    ListAdapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private EditText input;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        input = (EditText) findViewById(R.id.search_txt);
        bookList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.book_list);


        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        lv.setEmptyView(mEmptyStateTextView);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String query = input.getText().toString().replace(" ", "+");
                urlString = "https://www.googleapis.com/books/v1/volumes?q="+query+"&orderBy=newest";
                new ParseJSON().execute(urlString);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bookList.clear();
            mEmptyStateTextView.setText(R.string.no_data_msg);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new SimpleAdapter(MainActivity.this, bookList,
                    R.layout.list_item, new String[]{"title","authors"},
                    new int[]{R.id.book_title, R.id.book_authors});
            lv.setAdapter(adapter);

        }
    }
}//End of MainActivity