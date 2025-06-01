package com.khata.accountType.util;

import java.util.List;

public class AccountCategoryConstants {

    public static final List<String> DEFAULT_CATEGORIES = List.of(
            "ASSETS",
            "LIABILITIES",
            "EQUITY",
            "INCOME",
            "EXPENSE");

    public static boolean isDefaultCategory(String categoryName){
        return DEFAULT_CATEGORIES.contains(categoryName);
    }
}
