package com.krisoflies.lilbudgeteer.model;

import com.krisoflies.lilbudgeteer.controller.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Created by Paolo on 3/13/2015. */
public class CategoryListContent {
    public static List<CategoryItem> ITEMS = new ArrayList<>();
    public static Map<Integer, CategoryItem> ITEM_MAP = new HashMap<>();

    public CategoryListContent(String path) {// Aqui se adhieren los usos unicos y basicos de la aplicacion.
        List<String> categories = TransactionManager.obtainCategories(path);
        for (int i = 0; i < categories.size(); i++)
            addItem(new CategoryItem(i + 1, categories.get(i)));
    }

    private static void addItem(CategoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    public static class CategoryItem {
        private int id;
        private String content;

        public CategoryItem(int id, String content) {
            this.setId(id);
            this.setContent(content);
        }

        @Override
        public String toString() {
            return getContent();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
