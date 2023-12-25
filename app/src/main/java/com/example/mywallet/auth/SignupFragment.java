package com.example.mywallet.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mywallet.R;
import com.example.mywallet.common.Utils;
import com.example.mywallet.database.models.User;
import com.example.mywallet.database.repositories.UserRepository;
import com.example.mywallet.main.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    private UserRepository userRepository;
    private User user;

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        View signupView = inflater.inflate(R.layout.fragment_signup, container, false);

        Button signupNavBtn = signupView.findViewById(R.id.buttonSignup);
        EditText firstNameView = signupView.findViewById(R.id.editTextFnameSignup);
        EditText lastNameView = signupView.findViewById(R.id.editTextLnameSignup);
        EditText usernameView = signupView.findViewById(R.id.editTextUsernameSignup);
        EditText passwordView = signupView.findViewById(R.id.editTextPasswordSignup);

        signupListener(
                signupNavBtn,
                firstNameView,
                lastNameView,
                usernameView,
                passwordView);

        return signupView;
    }

    private void signupListener(
            Button signupBtn,
            EditText fname,
            EditText lname,
            EditText username,
            EditText password) {
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User(
                        fname.getText().toString(),
                        lname.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString()
                );

                if(!validateSignupForm(user)) return;

                long userId = userRepository.createUser(user);

                Bundle bundle = new Bundle();
                bundle.putLong(Utils.USER_ID_KEY, userId);

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });
    }

    private boolean validateSignupForm(User user) {
        if(
            user.getFirstName().length() == 0 ||
            user.getLastName().length() == 0 ||
            user.getUserName().length() == 0 ||
            user.getPassword().length() == 0
        ) {
            Utils.showToast(requireContext(), "Fill required fields", Color.BLACK, Utils.TOAST_ERROR_BG_COLOR);
            return false;
        }

        boolean usernameExists = userRepository.checkUsernameExists(user.getUserName());

        if(usernameExists) {
            Utils.showToast(requireContext(), "Username Exists", Color.BLACK, Utils.TOAST_ERROR_BG_COLOR);
            return false;
        }

        if(user.getPassword().length() < 6) {
            Utils.showToast(requireContext(), "Password must be at least 6 characters", Color.BLACK, Utils.TOAST_ERROR_BG_COLOR);
            return false;
        }

        return true;
    }
}