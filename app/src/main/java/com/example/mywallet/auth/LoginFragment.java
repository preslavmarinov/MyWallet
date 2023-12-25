package com.example.mywallet.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mywallet.R;
import com.example.mywallet.common.Utils;
import com.example.mywallet.database.repositories.UserRepository;
import com.example.mywallet.main.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private UserRepository userRepository;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userRepository = new UserRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        Button signupNavBtn = loginView.findViewById(R.id.buttonSignupNav);
        Button loginBtn = loginView.findViewById(R.id.buttonLogin);
        EditText usernameField = loginView.findViewById(R.id.editTextUsername);
        EditText passwordField = loginView.findViewById(R.id.editTextPassword);

        this.signupListener(signupNavBtn);
        this.loginListener(loginBtn, usernameField, passwordField);


        return loginView;
    }

    private void signupListener(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuthActivity) requireActivity()).navigateToSignupFragment();
            }
        });
    }

    private void loginListener(Button button, EditText username, EditText password) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long userId = userRepository.loginUser(
                        username.getText().toString(),
                        password.getText().toString()
                );

                if(userId != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(Utils.USER_ID_KEY, userId);

                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                } else {
                    Utils.showToast(requireContext(), "Invalid Credentials", Color.BLACK, Utils.TOAST_ERROR_BG_COLOR);
                }
            }
        });
    }
}