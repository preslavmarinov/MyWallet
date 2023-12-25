package com.example.mywallet.main.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.R;
import com.example.mywallet.database.models.Category;
import com.example.mywallet.database.models.Transaction;
import com.example.mywallet.main.Contracts.DeleteListener;
import com.example.mywallet.main.Contracts.UpdateListener;
import com.example.mywallet.main.ViewHolders.TransactionViewHolder;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    private List<Transaction> transactions;

    private List<Category> categories;

    private DeleteListener deleteListener;

    private UpdateListener updateListener;

    public TransactionAdapter(List<Transaction> transactions, List<Category> categories) {
        this.transactions = transactions;
        this.categories = categories;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.amount.setText(String.valueOf(transaction.getAmount()));
        holder.transactionCategory.setText(getCategoryName(transaction.getCategoryId()));

        int backgroundColor = transaction.getAmount() > 0 ? R.color.light_green : R.color.light_red;
        holder.itemView.setBackgroundTintList(holder.itemView.getContext().getColorStateList(backgroundColor));

        holder.editIcon.setBackgroundTintList(holder.itemView.getContext().getColorStateList(backgroundColor));
        holder.deleteIcon.setBackgroundTintList(holder.itemView.getContext().getColorStateList(backgroundColor));

        holder.editIcon.setOnClickListener(v -> {
            updateListener.onUpdateItem(transaction.getId());
        });

        holder.deleteIcon.setOnClickListener(v -> {
            int removedPosition = getItemPosition(transaction.getId());
            transactions.remove(removedPosition);
            notifyItemRemoved(removedPosition);

            deleteListener.onDeleteItem(transaction.getId());
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(0, transaction);
        notifyItemInserted(0);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public void updateTransaction(Transaction transaction) {
        int pos = -1;
        for(int i=0; i<transactions.size(); i++) {
            if(transaction.getId() == transactions.get(i).getId()) {
                pos = i;
                break;
            }
        }

        transactions.set(pos, transaction);
        notifyItemChanged(pos);
    }

    private String getCategoryName(long id) {
        for(Category category : categories) {
            if(category.getId() == id) return category.getName();
        }

        return "";
    }

    private int getItemPosition(long id) {
        for(int i=0; i<transactions.size(); i++) {
            if(transactions.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }
}
