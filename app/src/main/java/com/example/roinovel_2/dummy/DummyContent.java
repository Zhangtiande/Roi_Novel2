package com.example.roinovel_2.dummy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    public DummyContent(ArrayList<String> list) {
        for (String s : list) {
            DummyItem item = new DummyItem(s);
            addItem(item);
        }
    }

    private void addItem(DummyItem item) {
        this.ITEMS.add(item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String content;

        public DummyItem(String content) {
            this.content = content;
        }

        @NotNull
        @Override
        public String toString() {
            return content;
        }
    }
}