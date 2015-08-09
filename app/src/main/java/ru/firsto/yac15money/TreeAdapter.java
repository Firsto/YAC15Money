package ru.firsto.yac15money;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by razor on 09.08.15.
 */
public class TreeAdapter extends BaseAdapter {
    private static final int TREE_ELEMENT_PADDING_VAL = 25;
    private List<ITreeElement> fileList;
    private Context context;
    private Bitmap iconCollapse;
    private Bitmap iconExpand;
//    private Dialog dialog;
//    private EditText textLabel;
//    private XTreeViewClassif treeView;

    public TreeAdapter(Context context, List<ITreeElement> fileList) {
        this.context = context;
        this.fileList = fileList;
//        this.dialog = dialog;
//        this.textLabel = textLabel;
//        this.treeView = treeView;
        iconCollapse = BitmapFactory.decodeResource(context.getResources(), R.drawable.expander_ic_minimized);
        iconExpand = BitmapFactory.decodeResource(context.getResources(), R.drawable.expander_ic_maximized);
    }

    public List<ITreeElement> getListData() {
        return this.fileList;
    }

    @Override
    public int getCount() {
        return this.fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convertView = View.inflate(context, R.layout.list_item, null);
        holder = new ViewHolder();
        holder.setTextView((TextView) convertView.findViewById(R.id.text));
        holder.setImageView((ImageView) convertView.findViewById(R.id.icon));
        convertView.setTag(holder);

        final ITreeElement elem = (ITreeElement) getItem(position);

        int level = elem.getLevel();
        holder.getIcon().setPadding(TREE_ELEMENT_PADDING_VAL * (level + 1), holder.icon.getPaddingTop(), 0, holder.icon.getPaddingBottom());
        holder.getText().setText(elem.getOutlineTitle());
        if (elem.isHasChild() && (elem.isExpanded() == false)) {
            holder.getIcon().setImageBitmap(iconCollapse);
        } else if (elem.isHasChild() && (elem.isExpanded() == true)) {
            holder.getIcon().setImageBitmap(iconExpand);
        } else if (!elem.isHasChild()) {
            holder.getIcon().setImageBitmap(iconCollapse);
            holder.getIcon().setVisibility(View.INVISIBLE);
        }

        IconClickListener iconListener = new IconClickListener(this, position);
        TextClickListener txtListener = new TextClickListener((ArrayList<ITreeElement>) this.getListData(), position);
        holder.getIcon().setOnClickListener(iconListener);
        holder.getText().setOnClickListener(txtListener);
        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView text;

        public TextView getText() {
            return this.text;
        }

        public void setTextView(TextView text) {
            this.text = text;
        }

        public ImageView getIcon() {
            return this.icon;
        }

        public void setImageView(ImageView icon) {
            this.icon = icon;
        }
    }

    /**
     * Listener For TreeElement Text Click
     */
    private class TextClickListener implements View.OnClickListener {
        private ArrayList<ITreeElement> list;
        private int position;

        public TextClickListener(ArrayList<ITreeElement> list, int position) {
            this.list = list;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
//            treeView.setXValue(String.valueOf(list.get(position).getId()));
            Toast.makeText(context, "ID = " + String.valueOf(list.get(position).getId()), Toast.LENGTH_SHORT).show();
//            dialog.dismiss();
        }
    }

    /**
     * Listener for TreeElement "Expand" button Click
     */
    private class IconClickListener implements View.OnClickListener {
        private ArrayList<ITreeElement> list;
        private TreeAdapter adapter;
        private int position;

        public IconClickListener(TreeAdapter adapter, int position) {
            this.list = (ArrayList<ITreeElement>) adapter.getListData();
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (!list.get(position).isHasChild()) {
                return;
            }

            if (list.get(position).isExpanded()) {
                list.get(position).setExpanded(false);
                ITreeElement element = list.get(position);
                ArrayList<ITreeElement> temp = new ArrayList<ITreeElement>();

                for (int i = position + 1; i < list.size(); i++) {
                    if (element.getLevel() >= list.get(i).getLevel()) {
                        break;
                    }
                    temp.add(list.get(i));
                }
                list.removeAll(temp);
                adapter.notifyDataSetChanged();
            } else {
                ITreeElement obj = list.get(position);
                obj.setExpanded(true);
                int level = obj.getLevel();
                int nextLevel = level + 1;

                for (ITreeElement element : obj.getChildList()) {
                    element.setLevel(nextLevel);
                    element.setExpanded(false);
                    list.add(position + 1, element);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}