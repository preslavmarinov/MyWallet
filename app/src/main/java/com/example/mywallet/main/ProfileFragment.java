package com.example.mywallet.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mywallet.R;
import com.example.mywallet.auth.AuthActivity;
import com.example.mywallet.common.Utils;
import com.example.mywallet.database.models.User;
import com.example.mywallet.database.repositories.UserRepository;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private UserRepository userRepository;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        long userId = preferences.getLong(Utils.USER_ID_KEY, 0);

        userRepository = new UserRepository(requireContext());

        user = userRepository.getUserById(userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View profileFragment = inflater.inflate(R.layout.fragment_profile, container, false);
        ((TextView) profileFragment.findViewById(R.id.fnameText)).setText(user.getFirstName());
        ((TextView) profileFragment.findViewById(R.id.lnameText)).setText(user.getLastName());
        ((TextView) profileFragment.findViewById(R.id.unameText)).setText(user.getUserName());

        Button logoutBtn = profileFragment.findViewById(R.id.logoutBtn);

        setLogoutListener(logoutBtn);
        
        return profileFragment;
    }

    private void setLogoutListener(Button btn) {
        btn.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}