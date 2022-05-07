package com.example.carpool.register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RegisterViewPagerAdapter extends FragmentPagerAdapter {
    public static final int NUM_PAGER = 3;

    public RegisterViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public RegisterViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 1:
                return RegisterStepThreeFragment.newInstance(1, "CONTACT");

            case 2:
                return RegisterStepFourFragment.newInstance(2, "SETTINGS");

            default:
                return RegisterStepOneFragment.newInstance(0, "HOME");

        }
    }

    @Override
    public int getCount() {
        return NUM_PAGER;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return "CONTACT";
            case 2:
                return "SETTING";
            default:
                return "HOME";
        }
    }
}
