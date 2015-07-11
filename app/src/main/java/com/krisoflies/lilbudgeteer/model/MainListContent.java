package com.krisoflies.lilbudgeteer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainListContent {

    public static List<MainItem> ITEMS = new ArrayList<>();
    public static Map<String, MainItem> ITEM_MAP = new HashMap<>();

    static {
        // Aqui se adhieren los usos unicos y basicos de la aplicacion.
        addItem(new MainItem("1", "Transaction", "Save your daily transactions (loss or gains), for tracking how you expend your money."));
        addItem(new MainItem("2", "Configuration", "The parameters of the application are managed here. Passwords, warnings and other configurations are managed here"));
        addItem(new MainItem("3", "Report", "Generates the balance register up to today. Produces an Excel file."));
        addItem(new MainItem("4", "Erase Transaction", "Erases the transactions up to the last 100. Must be erased one by one and with the use of a password."));
        addItem(new MainItem("5", "Economic Advice", "View economical advices of today, it might be helpful for you."));
        addItem(new MainItem("6", "Your Spending", "View statistic by theme, and overall economical life."));
        addItem(new MainItem("7", "Your Money Cycle", "Study by a range of dates your economical life. Better knowledge, better decisions. Make a safe bet."));
    }

    private static void addItem(MainItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    public static class MainItem {
        private String id;
        private String content;
        private String description;

        public MainItem(String id, String content, String description) {
            this.setId(id);
            this.setContent(content);
            this.setDescription(description);
        }


        @Override
        public String toString() {
            return getContent();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}