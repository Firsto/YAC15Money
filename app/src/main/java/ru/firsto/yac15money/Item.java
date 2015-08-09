package ru.firsto.yac15money;

/**
 * Created by razor on 09.08.15.
 */
public class Item {
    private int id;
    private int parent_id;
    private int internal_id;
    private String title;

    public Item(int id, int parent_id, int internal_id, String title) {
        this.id = id;
        this.parent_id = parent_id;
        this.internal_id = internal_id;
        this.title = title;
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
}
