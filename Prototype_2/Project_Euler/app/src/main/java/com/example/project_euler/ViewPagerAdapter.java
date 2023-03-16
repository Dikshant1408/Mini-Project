package com.example.project_euler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:return new PythonFragment();
            case 2:return new JavascriptFragment();
            case 0:
            default:return new JavaFragment();
        }
    }

    @Override
    public int getItemCount() {

        return 3;
    }
}
