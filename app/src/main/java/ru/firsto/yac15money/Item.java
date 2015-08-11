package ru.firsto.yac15money;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by razor on 09.08.15.
 */
public class Item implements MultiLevelExpIndListAdapter.ExpIndData {
    private int id;
    private int parent_id;
    private int internal_id;
    private String title;

    private int mIndentation;
    private List<Item> mChildren;
    private boolean mIsGroup;
    private int mGroupSize;

    public Item(int id, int parent_id, int internal_id, String title) {
        this.id = id;
        this.parent_id = parent_id;
        this.internal_id = internal_id;
        this.title = title;

        mChildren = new ArrayList<Item>();

        setIndentation(0);
    }

    public int getId() {
        return id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public int getInternal_id() {
        return internal_id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public List<? extends MultiLevelExpIndListAdapter.ExpIndData> getChildren() {
        return mChildren;
    }

    @Override
    public boolean isGroup() {
        return mIsGroup;
    }

    @Override
    public void setIsGroup(boolean value) {
        mIsGroup = value;
    }

    @Override
    public void setGroupSize(int groupSize) {
        mGroupSize = groupSize;
    }

    public int getGroupSize() {
        return mGroupSize;
    }

    public void addChild(Item child) {
        mChildren.add(child);
        child.setIndentation(getIndentation() + 1);
    }

    public int getIndentation() {
        return mIndentation;
    }

    private void setIndentation(int indentation) {
        mIndentation = indentation;
    }
}
