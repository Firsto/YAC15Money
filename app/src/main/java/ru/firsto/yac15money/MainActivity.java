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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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

        if (isFirstStart) {
            loadList();

            mPrefs.edit().putBoolean(PREF_FIRST_START, false).commit();
        }

        reloadButton = (Button) findViewById(R.id.reloadButton);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "reloadClick");
                loadList();
            }
        });



        String text = "";

        DBHelper.ItemCursor cursor = mHelper.queryItems();
//        DBHelper.ItemCursor cursor = mHelper.queryChildItems(14);
        while (cursor.moveToNext()) {
            Item item = cursor.getItem();
            text += item.getId() + " -- " + item.getInternal_id() + " -- " + item.getTitle() + " --- parent " + item.getParent_id();
            text += "\n-------\n";
        }
//        tv.setText(text);

        ArrayList<Item> items = new ArrayList<>();
        cursor = mHelper.queryChildItems(0);
        while (cursor.moveToNext()) {
            items.add(cursor.getItem());
        }

//        ItemAdapter adapter = new ItemAdapter(getApplicationContext(), 0, items);


        fm = getSupportFragmentManager();
        listFragment = fm.findFragmentByTag(ItemListFragment.TAG);

        initFragment();

        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.clearDatabase();

//                if (listFragment instanceof ItemListFragment) {
//                    ((ItemListFragment) listFragment).reloadList();
//                }
                if (listFragment != null) {
                    fm.beginTransaction().remove(listFragment).commit();
                }
            }
        });
        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "refresh");
                if (listFragment != null) {
                    fm.beginTransaction().remove(listFragment).commit();
                }
                listFragment = null;
                initFragment();
            }
        });

//        String[] from = new String[] { "title", "_id" };
//        int[] to = new int[] { R.id.item_title, R.id.item_id };
//
//        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
//        lv.setAdapter(scAdapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });



//        try {
//            // Check if there are any unsent messages
//            if (cursor == null || cursor.getCount() == 0) {
//                // No queued messages to send
//                return;
//            }
//
//            // Send messages
//            while (cursor.moveToNext()) {
//
//                // Do stuff
//            }
//        }



    }

    private void initFragment() {
        Log.d("TAG", "initFragment()");
        if (listFragment == null) {
            listFragment = new ItemListFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, listFragment, ItemListFragment.TAG)
                    .commit();
        }
    }

    private void loadList() {
        Log.d("TAG", "loadList()");
//        mHelper.clearDatabase();
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

//    private class ItemAdapter extends ArrayAdapter<Item> {
//
//        public ItemAdapter(Context context, int resource, ArrayList<Item> items) {
//            super(context, R.layout.item, items);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            Item item = getItem(position);
//
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext())
//                        .inflate(R.layout.item, null);
//            }
//            ((TextView) convertView.findViewById(R.id.item_title))
//                    .setText(item.getTitle());
//            ((TextView) convertView.findViewById(R.id.item_id))
//                    .setText(String.valueOf(item.getInternal_id()));
//
//            return convertView;
//        }
//    }

}
