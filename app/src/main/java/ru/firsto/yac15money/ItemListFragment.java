package ru.firsto.yac15money;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by razor on 09.08.15.
 */
public class ItemListFragment extends Fragment {

    public static final String TAG = "ITEM_FRAGMENT";

    private DBHelper mHelper;
    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new DBHelper(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.item_list_fragment, container, false);
        lv = (ListView) view.findViewById(R.id.item_list);

        DBHelper.ItemCursor cursor = mHelper.queryChildItems(0);

        String[] from = new String[] { "title", "_id" };
        int[] to = new int[] { R.id.item_title, R.id.item_id };

        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item, cursor, from, to);
        lv.setAdapter(scAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                Fragment fragment = fm.findFragmentByTag(TAG + i);
//
//                if (fragment == null) {
//                    fragment = new ItemListFragment();
//                    fm.beginTransaction()
//                            .add(R.id.listContainer, fragment, TAG + i)
//                            .commit();
//                }
                Toast.makeText(getActivity(), "CLICKED " + i + " - " + l, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
