package com.example.mywallet.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.mywallet.R;
import com.example.mywallet.common.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long userId = getIntent().getExtras().getLong(Utils.USER_ID_KEY);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(Utils.USER_ID_KEY, userId);
        editor.apply();

        if(savedInstanceState == null) {
            loadFragment(0);
        }
    }

    public void loadFragment(int index) {
        Fragment fragment = getDesiredFragment(index);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .replace(R.id.fragmentContainerMain, fragment)
                .addToBackStack(null)
                .commit();
    }

    private Fragment getDesiredFragment(int index) {
        switch (index) {
            case 0:
                return new TransactionFragment();
            case 1:
                return new CategoryFragment();
            case 2:
                return new StatisticsFragment();
        }

        return new ProfileFragment();
    }

}