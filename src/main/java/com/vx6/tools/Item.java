package com.vx6.tools;

import java.util.TreeSet;

public class Item {
    public boolean modified;
    public TreeSet<String> data;

    public Item(TreeSet<String> data) {
        modified = false;
        this.data = data;
    }

    public boolean add(String str) {
        boolean added = data.add(str);
        modified = modified || added;
        return added;
    }
}
