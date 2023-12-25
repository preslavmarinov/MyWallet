package com.example.mywallet.main.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.R;
import com.example.mywallet.database.models.Category;
import com.example.mywallet.main.Contracts.DeleteListener;
import com.example.mywallet.main.Contracts.UpdateListener;
import com.example.mywallet.main.ViewHolders.CategoryViewHolder;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private List<Category> categories;
    private DeleteListener deleteListener;
    private UpdateListener updateListener;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.name.setText(category.getName());

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateListener.onUpdateItem(category.getId());
            }
        });

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int removedPosition = getItemPosition(category.getId());
                categories.remove(removedPosition);
                notifyItemRemoved(removedPosition);

                deleteListener.onDeleteItem(category.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private int getItemPosition(long id) {
        for(int i=0; i<categories.size(); i++) {
            if(categories.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public void addCategory(Category category) {
        categories.add(category);
        notifyItemInserted(categories.size() - 1);
    }

    public void updateCategory(Category category) {
        int pos = -1;
        for(int i=0; i<categories.size(); i++) {
            if(category.getId() == categories.get(i).getId()) {
                pos = i;
                break;
            }
        }

        categories.set(pos, category);
        notifyItemChanged(pos);
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }
}
