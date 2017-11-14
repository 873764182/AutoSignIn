package com.pixel.asi;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView mGoToTime;
    private TextView mAfterTime;
    private EditText mWifiEdit;
    private Switch mNetSwitch;
    private Switch mServiceSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoToTime = (TextView) findViewById(R.id.goToTime);
        mAfterTime = (TextView) findViewById(R.id.afterTime);
        mWifiEdit = (EditText) findViewById(R.id.wifiEdit);
        mNetSwitch = (Switch) findViewById(R.id.netSwitch);
        mServiceSwitch = (Switch) findViewById(R.id.serviceSwitch);

        mGoToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance();
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mGoToTime.setText(hourOfDay + ":" + minute);
                    }
                }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true).show();
            }
        });
        mAfterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time = Calendar.getInstance();
                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mAfterTime.setText(hourOfDay + ":" + minute);
                    }
                }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true).show();
            }
        });

        mNetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 保存上下班时间与WIFI名称
                    ConfigUtil.saveString(MainActivity.this, SignInUtil.GOTO_TIME, mGoToTime.getText().toString().trim());
                    ConfigUtil.saveString(MainActivity.this, SignInUtil.AFTER_TIME, mAfterTime.getText().toString().trim());
                    ConfigUtil.saveString(MainActivity.this, SignInUtil.WIFI_SSID, mWifiEdit.getText().toString().trim());
                    // 启动服务
                    startService(new Intent(MainActivity.this, TimingService.class));
                } else {
                    // 停止服务
                    stopService(new Intent(MainActivity.this, TimingService.class));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String GOTO_TIME = ConfigUtil.getString(this, SignInUtil.GOTO_TIME);
        if (GOTO_TIME != null && GOTO_TIME.length() > 0) {
            mGoToTime.setText(GOTO_TIME);
        }
        String AFTER_TIME = ConfigUtil.getString(this, SignInUtil.AFTER_TIME);
        if (AFTER_TIME != null && AFTER_TIME.length() > 0) {
            mAfterTime.setText(AFTER_TIME);
        }

        try {
            AppUtil.execShellCmd("input text  'Get ROOT permission to succeed'");
            Toast.makeText(this, "程序必须拥有ROOT权限", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDingDing(View view) {
        AppUtil.doStartApplicationWithPackageName(this, SignInUtil.DD_PN);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
