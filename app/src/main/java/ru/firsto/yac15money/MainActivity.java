package ru.firsto.yac15money;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "money";

    private static final String PREFS_FILE = "goods";
    private static final String PREF_FIRST_START = "first_start";

    public static final String API_URL = "https://money.yandex.ru/api/categories-list";

    private DBHelper mHelper;
    private SharedPreferences mPrefs;

    Button reloadButton;

    FragmentManager fm;
    Fragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new DBHelper(getApplicationContext());
        mPrefs = getApplicationContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        boolean isFirstStart = mPrefs.getBoolean(PREF_FIRST_START, true);

        fm = getSupportFragmentManager();
        listFragment = fm.findFragmentByTag(ListViewFragment.TAG);

        if (isFirstStart) {
            loadList();

            mPrefs.edit().putBoolean(PREF_FIRST_START, false).commit();
        } else {
            initFragment();
        }

        reloadButton = (Button) findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "reloadClick");
                loadList();
            }
        });

        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.clearDatabase();

                if (listFragment != null) {
                    fm.beginTransaction().remove(listFragment).commit();
                }
                Toast.makeText(getApplicationContext(), "DB HAS BEEN CLEARED!", Toast.LENGTH_SHORT).show();
            }
        });
        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "refresh");
                refreshList();
            }
        });

    }

    private void refreshList() {
        if (listFragment != null) {
            fm.beginTransaction().remove(listFragment).commit();
        }
        listFragment = null;
        initFragment();
    }

    private void initFragment() {
        Log.d("TAG", "initFragment()");
        if (listFragment == null) {
            listFragment = new ListViewFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, listFragment, ListViewFragment.TAG)
                    .commit();
        }
    }

    private void loadList() {
        Log.d("TAG", "loadList()");
        mHelper.clearDatabase();
        APILoader ymdata = new APILoader();
        ymdata.execute();
    }


    public class APILoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String jsonString = getUrl(API_URL);
                Log.d("TAG", jsonString);
                parseJSON(jsonString, 0);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refreshList();
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

    private void parseJSON(String jsonString, int parent) throws JSONException {
        JSONArray entries = new JSONArray(jsonString);

        int id = 0;

        for (int i = 0; i < entries.length(); i++) {
            JSONObject post = entries.getJSONObject(i);

            Item item = new Item(0, parent, post.optInt("id", 0), post.getString("title"));
            id = (int) mHelper.insertItem(item);

            JSONArray subs = post.optJSONArray("subs");

            if (subs != null) {
                parseJSON(subs.toString(), id);
            }
        }
    }
}
