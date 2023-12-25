package com.example.mywallet.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.mywallet.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if(savedInstanceState == null) {
            loadLoginFragment();
        }
    }

    private void loadLoginFragment() {
        loadFragment(new LoginFragment());
    }

    public void navigateToSignupFragment() { loadFragment(new SignupFragment()); }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .replace(R.id.fragmentContainerAuth, fragment)
                .addToBackStack(null)
                .commit();
    }

}