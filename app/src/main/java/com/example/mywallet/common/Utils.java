package com.example.mywallet.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywallet.R;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static final String TOAST_ERROR_BG_COLOR = "#FF3D00";

    public static final String TOAST_SUCCESS_BG_COLOR = "#00966C";

    private static final float BORDER_RADIUS = 36;

    public static final String USER_ID_KEY = "userId";

    public static final String DIALOG_ARGUMENTS = "arguments";

    public static void showToast(Context context, String message, int textColor, String backgroundColor) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View toastLayout = inflater.inflate(R.layout.custom_toast, null);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(BORDER_RADIUS);
        shape.setColor(Color.parseColor(backgroundColor));

        TextView text = toastLayout.findViewById(R.id.toast_message);
        text.setText(message);
        text.setTextColor(textColor);
        text.setBackground(shape);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }

    public static String getFirstDateMonth(int monthIndex) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDate = calendar.get(Calendar.DAY_OF_MONTH);

        String month = monthIndex+1 < 10 ? "0"+(monthIndex+1) : String.valueOf(monthIndex+1);

        String firstDateStr = currentYear + "-" + month + "-" + "0" + firstDate;

        return firstDateStr;
    }

    public static String getLastDateMonth(int monthIndex) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int lastDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        String month = monthIndex+1 < 10 ? "0"+(monthIndex+1) : String.valueOf(monthIndex+1);

        String lastDateStr = currentYear + "-" + month + "-" + lastDate;

        return lastDateStr;
    }


}
