package com.example.aksha_parvadiya_project2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.aksha_parvadiya_project2.Adapters.TabFragAdapter;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {

    TabLayout tab_layout;
    ViewPager2 pager;
    TabFragAdapter tabFragAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tab_layout=findViewById(R.id.tab_layout);
        pager=findViewById(R.id.pager);

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.hometab));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.bag));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.favouritetab));
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.usertab));

        int defaultColor = ContextCompat.getColor(this, android.R.color.white);
        int selectedColor = ContextCompat.getColor(this, R.color.primary);

        TabLayout.Tab tab;
        tab=tab_layout.getTabAt(0);
        DrawableCompat.setTint(tab.getIcon(), selectedColor);
        for (int i = 0; i < tab_layout.getTabCount(); i++) {
             tab = tab_layout.getTabAt(i);
            if (tab != null) {
                if (tab.getIcon() != null) {
                    DrawableCompat.setTint(tab.getIcon(), defaultColor);

                }
                View tabView = ((ViewGroup) tab_layout.getChildAt(0)).getChildAt(i);
                tabView.setBackground(ContextCompat.getDrawable(this, R.drawable.tab_background));
            }
        }

        FragmentManager fragmentManager=getSupportFragmentManager();
        tabFragAdapter= new TabFragAdapter(fragmentManager,getLifecycle());
        pager.setAdapter(tabFragAdapter);
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
                if (tab.getIcon() != null) {
                    DrawableCompat.setTint(tab.getIcon(), selectedColor);
                }

                View tabView = ((ViewGroup) tab_layout.getChildAt(0)).getChildAt(tab.getPosition());
                tabView.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.tab_background));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null) {
                    DrawableCompat.setTint(tab.getIcon(), defaultColor);
                }

                View tabView = ((ViewGroup) tab_layout.getChildAt(0)).getChildAt(tab.getPosition());
                tabView.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.tab_background));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tab_layout.selectTab(tab_layout.getTabAt(position));
                DrawableCompat.setTint(tab_layout.getTabAt(position).getIcon(), selectedColor);

            }
        });
    }
}