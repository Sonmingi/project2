package com.example.user.jsouptest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by user on 2018-06-01.
 */

public class TimeSetting extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        // 현재 시간을 초기 값으로 사용
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK, this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));


        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("TimePickerDialog Title");
        tvTitle.setBackgroundColor(Color.parseColor("#ffeee8aa"));
        tvTitle.setPadding(5, 3, 5, 3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        TextView tv = (TextView) getActivity().findViewById(R.id.settingtime);

        String hour = String.valueOf(hourOfDay);
        String min = String.valueOf(minute);

        tv.setText("설정된 시간 : ");
        tv.setText(tv.getText() + hour + "시" + min + "분\n");

        SharedprefereneceUtil.putData(getContext(), "SharedHour", hour);
        SharedprefereneceUtil.putData(getContext(), "SharedMin", min);


        // Toggle On/Off에 사용할 시간
        SharedprefereneceUtil.putData(getContext(), "SaveHour", hour);
        SharedprefereneceUtil.putData(getContext(), "SaveMin", min);
    }

}