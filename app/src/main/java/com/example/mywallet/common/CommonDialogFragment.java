package com.example.mywallet.common;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mywallet.R;
import com.example.mywallet.common.contracts.DialogListener;
import com.example.mywallet.common.enums.OperationType;
import com.example.mywallet.database.models.Category;

import java.util.List;


public class CommonDialogFragment extends DialogFragment {

    private DialogListener dialogListener;
    private OperationType operationType;

    private List<Category> categories;

    private long selectedCategoryId;

    public CommonDialogFragment(List<Category> categories) {
        // Required empty public constructor
        this.categories = categories;
    }

    public static CommonDialogFragment newInstance(Bundle bundle, List<Category> categories) {
        CommonDialogFragment fragment = new CommonDialogFragment(categories);
        Bundle args = new Bundle();
        args.putBundle(Utils.DIALOG_ARGUMENTS, bundle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View commonDialogFragment = inflater.inflate(R.layout.fragment_common_dialog, container, false);

        TextView titleDialog = commonDialogFragment.findViewById(R.id.dialogTitle);
        LinearLayout editTextsContainer = commonDialogFragment.findViewById(R.id.editTextsContainer);
        Button cancelButton = commonDialogFragment.findViewById(R.id.btnCancel);
        Button confirmButton = commonDialogFragment.findViewById(R.id.btnConfirm);

        if(getArguments() != null) {
            Bundle arguments = getArguments().getBundle(Utils.DIALOG_ARGUMENTS);
            String title = arguments.getString("title");
            int numFields = arguments.getInt("numFields");
            String[] hints = arguments.getStringArray("hints");

            titleDialog.setText(title);

            for(int i=0; i<numFields; i++) {
                EditText editText = new EditText(requireContext());
                editTextsContainer.addView(editText);

                editText.setHint(hints[i]);
                editText.setTextColor(Color.BLACK);

                editText.setBackgroundResource(R.drawable.dialog_edittext_bg);
            }

            if(this.categories != null) {
                Spinner categorySpinner = new Spinner(requireContext());
                setUpCategorySpinner(categorySpinner);
                editTextsContainer.addView(categorySpinner);
            }
        }

        cancelBtnListener(cancelButton);
        confirmBtnListener(confirmButton, editTextsContainer);

        return commonDialogFragment;
    }

    public void setDialogListener(DialogListener dialogListener, OperationType operationType) {
        this.dialogListener = dialogListener;
        this.operationType = operationType;
    }

    private void cancelBtnListener(Button btn) {
        btn.setOnClickListener(view -> {
            dismiss();
        });
    }

    private void confirmBtnListener(Button btn, LinearLayout editsTextsContainer) {
        btn.setOnClickListener(view -> {
            if(dialogListener != null) {
                String[] values = getEditTextValues(editsTextsContainer);
                Bundle arguments = getArguments().getBundle(Utils.DIALOG_ARGUMENTS);
                long id = arguments.containsKey("id") ? arguments.getLong("id") : -1;

                if(!validateData(values)) {
                    Utils.showToast(requireContext(), "Invalid field/s", Color.WHITE, Utils.TOAST_ERROR_BG_COLOR);
                    return;
                }

                dialogListener.onConfirmClicked(values, operationType, id);
                dismiss();
            }
        });
    }

    private String[] getEditTextValues(LinearLayout editsTextsContainer) {
        int numFields = getArguments().getBundle(Utils.DIALOG_ARGUMENTS).getInt("numFields");
        String[] values = new String[2];

        for (int i = 0; i < numFields; i++) {
                EditText editText = (EditText) editsTextsContainer.getChildAt(i);
                values[i] = editText.getText().toString();
        }
        if(categories != null) {
            values[1] = String.valueOf(selectedCategoryId);
        }

        return values;
    }

    private boolean validateData(String[] values) {
        for(int i=0; i<values.length; i++) {
            if(values[i].length() == 0) {
                return false;
            }
        }

        if(categories != null) {
            try {
                Double.parseDouble(values[0]);
            } catch (NumberFormatException e) {
                return false;
            }

            if("-1".equals(values[1])) return false;
        }

        return true;
    }

    private void setUpCategorySpinner(Spinner spinner) {
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(requireContext(), android.R.layout.simple_spinner_item, categories) {
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

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

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

}