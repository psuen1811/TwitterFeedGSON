package com.example.twitterfeedgson;

/**
 * Created by paksuen on 9/18/14.
 */
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.squareup.okhttp.OkHttpClient;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class MainActivity extends Activity {

    private RetrieveTwitterFeedTask task = null;
    OkHttpClient client = new OkHttpClient();
    private final static String URL_STRING = "http://www.themasterpak.com/twitteroauth/pak.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parsegson);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        this.task = new RetrieveTwitterFeedTask();
        this.task.execute();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if( task != null )
        {
            task.cancel(true);
        }
    }

    private class RetrieveTwitterFeedTask extends AsyncTask<Void, Void, MyArrayAdapter> {

        private List<String> buildFeedList(ArrayList<TwitterFeedContainer> container)
        {
            List<String> twitterFeeds = new ArrayList<String>();
            for(TwitterFeedContainer feed : container)
            {
                twitterFeeds.add(feed.getText());
            }
            return twitterFeeds;
        }

        private List<String> getFeedsWithoutRetrofit() throws MalformedURLException, IOException
        {
            Gson gson = new Gson();
            URL url = new URL(URL_STRING);
            // Use OkHttp API
            GetURLOkHttp urlOkHttp = new GetURLOkHttp();
            InputStream inputStream = new ByteArrayInputStream(urlOkHttp.get(url).getBytes());

            Type listType = new TypeToken<ArrayList<TwitterFeedContainer>>() {
            }.getType();
            final ArrayList<TwitterFeedContainer> container = gson.fromJson(new InputStreamReader(inputStream), listType);

            return buildFeedList(new ArrayList<TwitterFeedContainer>(container));
        }

        private List<String> getFeedsWithRetrofit()
        {
            TwitterFeedService service = AdapterService.getInstance().getTheMasterPakAdapter().create(TwitterFeedService.class);
            return buildFeedList(service.getFeeds());
        }

        protected MyArrayAdapter doInBackground(Void... object) {

            MyArrayAdapter result = null;
            List<String> twitterFeeds = new ArrayList<String>();

            try {
                //twitterFeeds = getFeedsWithoutRetrofit();
                twitterFeeds = getFeedsWithRetrofit();
                result = new MyArrayAdapter(MainActivity.this, twitterFeeds);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(MyArrayAdapter adapter) {
            if(!isCancelled()) {
                final ListView listView = (ListView) findViewById(R.id.gson_text);
                if( adapter != null && !adapter.isEmpty())
                {
                    listView.setAdapter(adapter);
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }
            }
        }
    }
    private class MyArrayAdapter extends ArrayAdapter {
        private List<String> values;
        public MyArrayAdapter(Context context, List<String> values) {
            super(context, R.layout.item, values);
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if( convertView == null ) {
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.item_text);
            textView.setText(this.values.get(position));
            return convertView;
        }
    }
}

