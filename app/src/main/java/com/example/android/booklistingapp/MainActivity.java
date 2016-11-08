package com.example.android.booklistingapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static String urlString;

    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHORS = "authors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    private class ParseJSON extends AsyncTask<String, Void, Book> {
        @Override
        protected Book doInBackground(String... urls) {

            String url = urlString;
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Loading results, please wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Book book) {
            if (book == null) {
                return;
            }

            updateUI(book);
        }
    }

    private void updateUI(Book book) {

        TextView titleTextView = (TextView) findViewById(R.id.book_title);
        titleTextView.setText(book.title);

        TextView authorsTextView = (TextView) findViewById(R.id.book_authors);
        authorsTextView.setText(book.authors);

    }


}//End of MainActivity



