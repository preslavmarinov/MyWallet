package com.example.mywallet.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mywallet.R;
import com.example.mywallet.common.CommonDialogFragment;
import com.example.mywallet.common.contracts.DialogListener;
import com.example.mywallet.common.enums.OperationType;
import com.example.mywallet.database.models.Category;
import com.example.mywallet.database.repositories.CategoryRepository;
import com.example.mywallet.main.Adapter.CategoryAdapter;
import com.example.mywallet.main.Contracts.DeleteListener;
import com.example.mywallet.main.Contracts.UpdateListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment implements DialogListener, DeleteListener, UpdateListener {
    private CategoryRepository categoryRepository;
    private List<Category> categories;

    private CategoryAdapter categoryAdapter;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryRepository = new CategoryRepository(requireContext());
        categories = categoryRepository.getAllCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View categoryFragment = inflater.inflate(R.layout.fragment_category, container, false);

        RecyclerView recyclerView = categoryFragment.findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        if (categoryAdapter == null) {
            categoryAdapter = new CategoryAdapter(categories);
            categoryAdapter.setDeleteListener(this);
            categoryAdapter.setUpdateListener(this);
            recyclerView.setAdapter(categoryAdapter);
        }

        FloatingActionButton addCategoryBtn = categoryFragment.findViewById(R.id.fabCategoryAdd);
        setAddCategoryListener(addCategoryBtn);

        return categoryFragment;
    }

    @Override
    public void onConfirmClicked(String[] values, OperationType operationType, long id) {
        if(operationType == OperationType.CREATE) {
            Category category = new Category(values[0], values[1]);
            long newId = categoryRepository.createCategory(category);
            category.setId(newId);
            if(categoryAdapter != null) {
                categoryAdapter.addCategory(category);
            }

        } else {
            Category category = new Category(values[0], values[1]);
            category.setId(id);
            categoryRepository.updateCategory(category);

            if(categoryAdapter != null) {
                categoryAdapter.updateCategory(category);
            }
        }
    }

    @Override
    public void onDeleteItem(long id) {
        categoryRepository.deleteCategory(id);
    }

    @Override
    public void onUpdateItem(long id) {
        Bundle bundle = new Bundle();
        bundle.putString("title", "Update Category");
        bundle.putInt("numFields", 2);
        bundle.putStringArray("hints", new String[] {"Name", "Description"});
        bundle.putLong("id", id);

        CommonDialogFragment categoryCreateDialog = CommonDialogFragment.newInstance(bundle, null);
        categoryCreateDialog.setDialogListener(this, OperationType.UPDATE);
        categoryCreateDialog.show(getChildFragmentManager(), "CommonDialog");
    }

    private void setAddCategoryListener(FloatingActionButton btn) {
        btn.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Create New Category");
            bundle.putInt("numFields", 2);
            bundle.putStringArray("hints", new String[] {"Name", "Description"});

            CommonDialogFragment categoryCreateDialog = CommonDialogFragment.newInstance(bundle, null);
            categoryCreateDialog.setDialogListener(this, OperationType.CREATE);
            categoryCreateDialog.show(getChildFragmentManager(), "CommonDialog");
        });
    }
}