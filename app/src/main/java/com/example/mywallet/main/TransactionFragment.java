package com.example.mywallet.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.health.connect.datatypes.StepsCadenceRecord;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mywallet.R;
import com.example.mywallet.common.CommonDialogFragment;
import com.example.mywallet.common.Utils;
import com.example.mywallet.common.contracts.DialogListener;
import com.example.mywallet.common.enums.OperationType;
import com.example.mywallet.database.models.Category;
import com.example.mywallet.database.models.Transaction;
import com.example.mywallet.database.repositories.CategoryRepository;
import com.example.mywallet.database.repositories.TransactionRepository;
import com.example.mywallet.main.Adapter.TransactionAdapter;
import com.example.mywallet.main.Contracts.DeleteListener;
import com.example.mywallet.main.Contracts.UpdateListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TransactionFragment extends Fragment implements DialogListener, DeleteListener, UpdateListener {
    private CategoryRepository categoryRepository;
    private List<Category> categories;

    private TransactionRepository transactionRepository;

    private List<Transaction> transactions;

    private TransactionAdapter transactionAdapter;

    private String startDate;

    private String endDate;

    private long selectedCategoryId;

    private long userId;

    private String selectedType;


    public TransactionFragment() {
        // Required empty public constructor
    }

    public static TransactionFragment newInstance() {
        TransactionFragment fragment = new TransactionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryRepository = new CategoryRepository(requireContext());
        categories = categoryRepository.getAllCategories();
        Category defaultSpinnerCategory = new Category("Choose a Category", "Choose a Category");
        defaultSpinnerCategory.setId(-1);
        categories.add(0, defaultSpinnerCategory);

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        this.userId = preferences.getLong(Utils.USER_ID_KEY, -1);

        transactionRepository = new TransactionRepository(requireContext());
        transactions = transactionRepository.getFilteredTransactions(
                this.userId,
                -1,
                Utils.getFirstDateMonth(Calendar.getInstance().get(Calendar.MONTH)),
                Utils.getLastDateMonth(Calendar.getInstance().get(Calendar.MONTH)),
                "Choose type");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View transactionFragment = inflater.inflate(R.layout.fragment_transaction, container, false);

        Spinner monthSpinner = transactionFragment.findViewById(R.id.monthSpinner);
        Spinner categorySpinner = transactionFragment.findViewById(R.id.categorySpinner);
        Spinner typeSpinner = transactionFragment.findViewById(R.id.typeSpinner);

        Button filterBtn = transactionFragment.findViewById(R.id.filterBtn);
        Button addTransactionBtn = transactionFragment.findViewById(R.id.addTransactionBtn);

        setUpMonthSpinner(monthSpinner);
        setUpCategorySpinner(categorySpinner);
        setUpTypeSpinner(typeSpinner);

        setUpFilterListener(filterBtn);
        setUpAddListener(addTransactionBtn);

        RecyclerView transactionRecyclerView = transactionFragment.findViewById(R.id.transactionRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        transactionRecyclerView.setLayoutManager(layoutManager);

        if(transactionAdapter == null) {
            transactionAdapter = new TransactionAdapter(transactions, categories);
            transactionAdapter.setDeleteListener(this);
            transactionAdapter.setUpdateListener(this);
            transactionRecyclerView.setAdapter(transactionAdapter);
        }

        return transactionFragment;
    }

    @Override
    public void onConfirmClicked(String[] values, OperationType operationType, long id) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = format.format(date);

        Transaction transaction = new Transaction(
                Double.parseDouble(values[0]),
                formattedDate,
                formattedDate,
                Long.parseLong(values[1]),
                this.userId
        );

        if(operationType == OperationType.CREATE) {
            long transactionId = transactionRepository.createTransaction(transaction);
            transaction.setId(transactionId);
            transactionAdapter.addTransaction(transaction);
        } else {
            transaction.setId(id);
            transactionRepository.updateTransaction(transaction);
            transactionAdapter.updateTransaction(transaction);
        }
    }

    @Override
    public void onDeleteItem(long id) {
        transactionRepository.deleteTransaction(id);
    }

    @Override
    public void onUpdateItem(long id) {
        Bundle bundle = new Bundle();
        bundle.putString("title", "Update Transaction");
        bundle.putInt("numFields", 1);
        bundle.putStringArray("hints", new String[] {"Amount"});
        bundle.putLong("id", id);

        CommonDialogFragment transactionCreateDialog = CommonDialogFragment.newInstance(bundle, categories);
        transactionCreateDialog.setDialogListener(this, OperationType.UPDATE);
        transactionCreateDialog.show(getChildFragmentManager(), "CommonDialog");
    }

    private void setUpMonthSpinner(Spinner spinner) {
        String[] months = getResources().getStringArray(R.array.months_array);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, months);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(monthAdapter);
        spinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String firstDateStr = Utils.getFirstDateMonth(i);
                String lastDateStr = Utils.getLastDateMonth(i);
                startDate = firstDateStr;
                endDate = lastDateStr;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpCategorySpinner(Spinner spinner) {
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(requireContext(), android.R.layout.simple_spinner_item, categories) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(categories.get(position).getName());
                return textView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setText(categories.get(position).getName());
                return textView;
            }
        };

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(categoryAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategoryId = categories.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpTypeSpinner(Spinner spinner) {
        String[] types = getResources().getStringArray(R.array.type_array);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(typeAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = types[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpFilterListener(Button btn) {
        btn.setOnClickListener(view -> {
            transactions = transactionRepository.getFilteredTransactions(userId, selectedCategoryId, startDate, endDate, selectedType);
            transactionAdapter.setTransactions(transactions);
        });
    }

    private void setUpAddListener(Button btn) {
        btn.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Create new Transaction");
            bundle.putInt("numFields", 1);
            bundle.putStringArray("hints", new String[] {"Amount"});

            CommonDialogFragment transactionCreateDialog = CommonDialogFragment.newInstance(bundle, categories);
            transactionCreateDialog.setDialogListener(this, OperationType.CREATE);
            transactionCreateDialog.show(getChildFragmentManager(), "CommonDialog");
        });
    }

//    private List<Transaction> sampleData() {
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(new Transaction(1, "2023-12-10", "2023-12-10", 14, 1));
//        transactions.add(new Transaction(2, "2023-12-10", "2023-12-10", 15, 1));
//        transactions.add(new Transaction(-3, "2023-12-10", "2023-12-10", 16, 1));
//        transactions.add(new Transaction(4, "2023-12-10", "2023-12-10", 17, 1));
//        transactions.add(new Transaction(5, "2023-12-10", "2023-12-10", 18, 1));
//        transactions.add(new Transaction(-6, "2023-12-10", "2023-12-10", 19, 1));
//        transactions.add(new Transaction(7, "2023-12-10", "2023-12-10", 20, 1));
//        transactions.add(new Transaction(8, "2023-12-10", "2023-12-10", 21, 1));
//        transactions.add(new Transaction(9, "2023-12-10", "2023-12-10", 14, 1));
//        transactions.add(new Transaction(-10, "2023-12-10", "2023-12-10", 15, 1));
//        transactions.add(new Transaction(11, "2023-12-10", "2023-12-10", 16, 1));
//        transactions.add(new Transaction(12, "2023-12-10", "2023-12-10", 17, 1));
//        transactions.add(new Transaction(-13, "2023-12-10", "2023-12-10", 18, 1));
//        transactions.add(new Transaction(14, "2023-12-10", "2023-12-10", 19, 1));
//
//        return  transactions;
//    }
}