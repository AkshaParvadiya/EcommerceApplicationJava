package com.example.aksha_parvadiya_project2.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aksha_parvadiya_project2.Fragments.FragmentCart;
import com.example.aksha_parvadiya_project2.Fragments.FragmentFavourite;
import com.example.aksha_parvadiya_project2.Fragments.FragmentHome;
import com.example.aksha_parvadiya_project2.Fragments.FragmentProduct;

public class TabFragAdapter extends FragmentStateAdapter {

    public TabFragAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       if (position==0){
           return new FragmentHome();
       }else if (position==1){
            return new FragmentCart();
       }else if(position==2){
            return new FragmentFavourite();
       }else if(position==3){
           return new FragmentProduct();
       }
        return new FragmentHome();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
