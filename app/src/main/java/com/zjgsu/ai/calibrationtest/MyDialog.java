package com.zjgsu.ai.calibrationtest;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Double on 09/08/2017.
 */

public class MyDialog extends Dialog implements View.OnClickListener {
    ArrayList mList;
    int which = -1;
    Button pButton;
    Button nButton;

    public MyDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.my_dialog, null);
        setContentView(view);
        pButton = (Button) findViewById(R.id.pButton);
        nButton = (Button) findViewById(R.id.nButton);
        pButton.setOnClickListener(this);
        nButton.setOnClickListener(this);
    }

    public void setRemove(ArrayList list, int i) {
        mList = list;
        Log.i("Calibration-Click",mList.toString());
        which = i;
        this.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pButton:
                mList.remove(which);
                which = -1;
                this.dismiss();
                break;
            case R.id.nButton:
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
