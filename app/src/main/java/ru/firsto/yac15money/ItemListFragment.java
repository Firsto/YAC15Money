package ru.firsto.yac15money;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by razor on 09.08.15.
 */
public class ItemListFragment extends Fragment {

    public static final String TAG = "ITEM_FRAGMENT";

    private DBHelper mHelper;
    private ListView lv;

    private TreeViewClassifAdapter treeViewClassifAdapter;

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

        List<ITreeElement> elements = getITreeElements(0, new ArrayList<ITreeElement>());

        treeViewClassifAdapter = new TreeViewClassifAdapter(getActivity(), elements);
        lv.setAdapter(treeViewClassifAdapter);

//        String[] from = new String[] { "title", "_id" };
//        int[] to = new int[] { R.id.item_title, R.id.item_id };
//
//        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item, cursor, from, to);
//        lv.setAdapter(scAdapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                FragmentManager fm = getActivity().getSupportFragmentManager();
////                Fragment fragment = fm.findFragmentByTag(TAG + i);
////
////                if (fragment == null) {
////                    fragment = new ItemListFragment();
////                    fm.beginTransaction()
////                            .add(R.id.listContainer, fragment, TAG + i)
////                            .commit();
////                }
//                Toast.makeText(getActivity(), "CLICKED " + i + " - " + l, Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }

    public void reloadList() {
        Log.d("TAG", "reload");
        treeViewClassifAdapter.notifyDataSetChanged();
        if (lv.getAdapter() instanceof TreeViewClassifAdapter) {
            ((TreeViewClassifAdapter) lv.getAdapter()).notifyDataSetChanged();
            ((TreeViewClassifAdapter) lv.getAdapter()).notifyDataSetInvalidated();
            Log.d("TAG", "adaptered");
        }
    }

    private List<ITreeElement> getITreeElements(int parent, List<ITreeElement> elements) {
        DBHelper.ItemCursor cursor = mHelper.queryChildItems(0);

//        List<ITreeElement> elements = new ArrayList<>();
        cursor = mHelper.queryChildItems(parent);
        while (cursor.moveToNext()) {
            Item item = cursor.getItem();
            TreeElement element = new TreeElement(String.valueOf(item.getId()), item.getTitle());

//            ArrayList<ITreeElement> childElements = new ArrayList<ITreeElement>();
            List<ITreeElement> childElements = getITreeElements(item.getId(), new ArrayList<ITreeElement>());
            for (ITreeElement childElement : childElements) {
                Log.d("tag", "Child element id - " + childElement.getId());
                element.addChild(childElement);
            }

            elements.add(element);
            Log.d("TAG", "ITEM ID " + element.getId());
        }
        return elements;
    }
}
