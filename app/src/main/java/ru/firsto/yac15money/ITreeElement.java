package ru.firsto.yac15money;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by razor on 09.08.15.
 */
public interface ITreeElement extends Serializable {
    public void addChild(ITreeElement child);
    public String getId();
    public void setId(String id);
    public String getOutlineTitle();
    public void setOutlineTitle(String outlineTitle);
    public boolean isHasParent();
    public void setHasParent(boolean hasParent);
    public boolean isHasChild();
    public void setHasChild(boolean hasChild);
    public int getLevel();
    public void setLevel(int level);
    public boolean isExpanded();
    public void setExpanded(boolean expanded);
    public ArrayList<ITreeElement> getChildList();
    public ITreeElement getParent();
    public void setParent(ITreeElement parent);
}
