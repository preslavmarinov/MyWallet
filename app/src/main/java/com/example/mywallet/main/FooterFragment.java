package com.example.mywallet.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.mywallet.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FooterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FooterFragment extends Fragment {

    private final int[] iconDrawables = new int[] {
            R.drawable.ic_transaction,
            R.drawable.ic_category,
            R.drawable.ic_statistics,
            R.drawable.ic_person
    };
    private final int[] activeIconDrawables = new int[] {
            R.drawable.ic_transaction_active,
            R.drawable.ic_category_active,
            R.drawable.ic_statistics_active,
            R.drawable.ic_person_active
    };

    private int activeBtnIndex = 0;
    private ImageButton activeBtn;

    public FooterFragment() {
        // Required empty public constructor
    }

    public static FooterFragment newInstance(String param1, String param2) {
        FooterFragment fragment = new FooterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View footerLayout = inflater.inflate(R.layout.fragment_footer, container, false);

        ImageButton transactionBtn = footerLayout.findViewById(R.id.transactionBtn);
        ImageButton categoriesBtn = footerLayout.findViewById(R.id.categoriesBtn);
        ImageButton statisticsBtn = footerLayout.findViewById(R.id.statisticBtn);
        ImageButton profileBtn = footerLayout.findViewById(R.id.profileBtn);

        activeBtn = transactionBtn;
        transactionBtn.setImageResource(activeIconDrawables[activeBtnIndex]);

        this.imageBtnListener(transactionBtn, 0);
        this.imageBtnListener(categoriesBtn, 1);
        this.imageBtnListener(statisticsBtn, 2);
        this.imageBtnListener(profileBtn, 3);
        return footerLayout;
    }

    private void imageBtnListener(ImageButton btn, int index) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index == activeBtnIndex) {
                    return;
                }

                activeBtn.setImageResource(iconDrawables[activeBtnIndex]);
                btn.setImageResource(activeIconDrawables[index]);
                activeBtnIndex = index;
                activeBtn = btn;

                ((MainActivity) requireActivity()).loadFragment(activeBtnIndex);
            }
        });
    }
}