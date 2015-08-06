package ru.firsto.yac15money;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String API_URL = "https://money.yandex.ru/api/categories-list";

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.jsonText);

        APILoader ymdata = new APILoader();
        ymdata.execute();
    }

    public class APILoader extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            String text = "";

            try {
                String jsonString = getUrl(API_URL);

                JSONArray entries = new JSONArray(jsonString);


                text = "JSON parsed.\nThere are [" + entries.length() + "]\n\n";

                int i;
                for (i=0;i<entries.length();i++)
                {
                    JSONObject post = entries.getJSONObject(i);
                    text += "------------\n";
                    text += "Title:" + post.getString("title") + "\n";
//                    text += "Post:" + post.getString("text") + "\n\n";
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return text;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            tv.setText(string);
        }
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
