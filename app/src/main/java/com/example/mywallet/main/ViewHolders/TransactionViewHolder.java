package com.example.mywallet.main.ViewHolders;

import android.media.Image;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.R;

public class TransactionViewHolder extends RecyclerView.ViewHolder {
    public TextView amount;
    public TextView transactionCategory;

    public ImageButton editIcon;
    public ImageButton deleteIcon;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);

        amount = itemView.findViewById(R.id.amount);
        transactionCategory = itemView.findViewById(R.id.transactionCategory);
        editIcon = itemView.findViewById(R.id.transactionIconEdit);
        deleteIcon = itemView.findViewById(R.id.transactionIconDelete);
    }
}
