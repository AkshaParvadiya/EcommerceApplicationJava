package com.example.aksha_parvadiya_project2.OtherCalss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainClassData {
    public static MainClassData mainData;

    public static MainClassData getInstance() {
        if (mainData == null) {
            mainData = new MainClassData( );
        }
        return mainData;
    }

    public String categoryId;
    public Map<String, String> favoriteProductsMap = new HashMap<>();
}
