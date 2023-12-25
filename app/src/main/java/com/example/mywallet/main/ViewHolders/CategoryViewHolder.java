package com.example.mywallet.main.ViewHolders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public ImageButton editIcon;
    public ImageButton deleteIcon;
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.categoryName);
        editIcon = itemView.findViewById(R.id.categoryIconEdit);
        deleteIcon = itemView.findViewById(R.id.categoryIconDelete);
    }
}
