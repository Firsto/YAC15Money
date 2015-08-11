package ru.firsto.yac15money;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by razor on 10.08.15.
 */
public class ListViewFragment extends Fragment {
    public static final String TAG = "LIST_FRAGMENT";

    private static final String GROUPS_KEY = "groups_key";

    private DBHelper mHelper;

    private MyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mHelper = new DBHelper(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // code copied from https://developer.android.com/training/material/lists-cards.html
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.content_recyclerview);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerView.getChildPosition(v);
                mAdapter.toggleGroup(position);
//                mAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        List<Item> elements = getItems();
        if (elements.isEmpty()) {
            Toast.makeText(getActivity(), "DB IS EMPTY, RELOAD DATA TO FILL!", Toast.LENGTH_SHORT).show();
        }
        mAdapter.addAll(elements);

        if (savedInstanceState != null) {
            List<Integer> groups = savedInstanceState.getIntegerArrayList(GROUPS_KEY);
            mAdapter.restoreGroups(groups);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(GROUPS_KEY, mAdapter.saveGroups());
        super.onSaveInstanceState(outState);
    }

    private List<Item> getItems() {
        List<Item> comments = new ArrayList<Item>();
        DBHelper.ItemCursor cursor =  mHelper.queryItems();

        HashMap<Integer, Integer> indexMap = new HashMap<>();

        while (cursor.moveToNext()) {
            Item item = cursor.getItem();
            comments.add(item);
            indexMap.put(item.getId(), comments.size() - 1);
        }
        int parent = 0;
        for (int i = 0; i < comments.size(); i++) {
            if ((parent = comments.get(i).getParent_id()) != 0) {
                comments.get(indexMap.get(parent)).addChild(comments.get(i));
            }
        }

        return comments;
    }
}