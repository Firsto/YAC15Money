package ru.firsto.yac15money;

import java.util.ArrayList;

/**
 * Created by razor on 09.08.15.
 */
public class TreeElement implements ITreeElement{
    private String id;
    private String outlineTitle;
    private boolean hasParent;
    private boolean hasChild;
    private ITreeElement parent;
    private int level;
    private ArrayList<ITreeElement> childList;
    private boolean expanded;

    public TreeElement(String id, String outlineTitle) {
        super();
        this.childList = new ArrayList<ITreeElement>();
        this.id = id;
        this.outlineTitle = outlineTitle;
        this.level = 0;
        this.hasParent = true;
        this.hasChild = false;
        this.parent = null;
    }

    public TreeElement(String id, String outlineTitle, boolean hasParent, boolean hasChild, TreeElement parent, int level, boolean expanded) {
        super();
        this.childList = new ArrayList<ITreeElement>();
        this.id = id;
        this.outlineTitle = outlineTitle;
        this.hasParent = hasParent;
        this.hasChild = hasChild;
        this.parent = parent;
        if(parent != null) {
            this.parent.getChildList().add(this);
        }
        this.level = level;
        this.expanded = expanded;
    }

    @Override
    public void addChild(ITreeElement child) {
        this.getChildList().add(child);
        this.setHasParent(false);
        this.setHasChild(true);
        child.setParent(this);
        child.setLevel(this.getLevel() + 1);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getOutlineTitle() {
        return this.outlineTitle;
    }

    @Override
    public void setOutlineTitle(String outlineTitle) {
        this.outlineTitle = outlineTitle;
    }

    @Override
    public boolean isHasParent() {
        return this.hasParent;
    }

    @Override
    public void setHasParent(boolean hasParent) {
        this.hasParent = hasParent;
    }

    @Override
    public boolean isHasChild() {
        return this.hasChild;
    }

    @Override
    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean isExpanded() {
        return this.expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public ArrayList<ITreeElement> getChildList() {
        return this.childList;
    }

    @Override
    public ITreeElement getParent() {
        return this.parent;
    }

    @Override
    public void setParent(ITreeElement parent) {
        this.parent = parent;
    }
}
