package ru.firsto.yac15money;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * An example of how to extend MultiLevelExpIndListAdapter.
 * Some of this code is copied from https://developer.android.com/training/material/lists-cards.html
 */
public class MyAdapter extends MultiLevelExpIndListAdapter {

    /**
     * This is called when the user click on an item or group.
     */
    private final View.OnClickListener mListener;

    private final Context mContext;

    /**
     * Unit of indentation.
     */
    private final int mPaddingDP = 5;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private View colorBand;
        public TextView itemTitleTextView;
        public TextView itemInternalIdTextView;
        public TextView hiddenCountTextView;
        private View view;

        private static final String[] indColors = {"#000000", "#3366FF", "#E65CE6",
                "#E68A5C", "#00E68A", "#CCCC33"};

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            itemTitleTextView = (TextView) itemView.findViewById(R.id.item_title_textview);
            itemInternalIdTextView = (TextView) itemView.findViewById(R.id.item_internal_id);
            colorBand = itemView.findViewById(R.id.color_band);
            hiddenCountTextView = (TextView) itemView.findViewById(R.id.hidden_count_textview);
        }

        public void setColorBandColor(int indentation) {
            int color = Color.parseColor(indColors[indentation]);
            colorBand.setBackgroundColor(color);
        }

        public void setPaddingLeft(int paddingLeft) {
            view.setPadding(paddingLeft,0,0,0);
        }
    }

    public MyAdapter(Context context, View.OnClickListener listener) {
        super();
        mContext = context;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_item, parent, false);
        v.setOnClickListener(mListener);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemViewHolder cvh = (ItemViewHolder) viewHolder;
        Item item = (Item) getItemAt(position);

        cvh.itemTitleTextView.setText(item.getTitle() + " " + (item.isGroup() ? "→" : (item.getChildren().size() > 0 ? "↓" : "")));
        if (item.getInternal_id() != 0) {
            cvh.itemInternalIdTextView.setVisibility(View.VISIBLE);
            cvh.itemInternalIdTextView.setText(String.valueOf(item.getInternal_id()));
        } else {
            cvh.itemInternalIdTextView.setVisibility(View.INVISIBLE);
        }

//        Log.d("TAG", "item " + item.getTitle() + " -- id " + item.getId() + " -- int_id " + item.getInternal_id() + " -- position " + position + " -- parent " + item.getParent_id());

        if (item.getIndentation() == 0) {
            cvh.colorBand.setVisibility(View.GONE);
            cvh.setPaddingLeft(0);
        } else {
            cvh.colorBand.setVisibility(View.VISIBLE);
            cvh.setColorBandColor(item.getIndentation());
            int leftPadding = Utils.getPaddingPixels(mContext, mPaddingDP) * (item.getIndentation() - 1);
            cvh.setPaddingLeft(leftPadding);
        }

        if (item.isGroup()) {
            cvh.hiddenCountTextView.setVisibility(View.VISIBLE);
            cvh.hiddenCountTextView.setText(Integer.toString(item.getGroupSize()));
        } else {
            cvh.hiddenCountTextView.setVisibility(View.INVISIBLE);
        }
    }
}
