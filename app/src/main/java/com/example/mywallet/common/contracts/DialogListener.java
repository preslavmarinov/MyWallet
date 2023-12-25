package com.example.mywallet.common.contracts;

import com.example.mywallet.common.enums.OperationType;

public interface DialogListener {
    void onConfirmClicked(String[] values, OperationType operationType, long id);
}

