package com.example.ly_lipengfei.abreak;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView caseTv;
    BreakTime mBreakTimeView;


    SimpleDateFormat simpleDateFormat;

    //SharedPreferences
    SharedPreferences mPref;
    private final String PREF_NAME = "myRecord";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //全屏幕情况下，把状态栏标记为浅色，则字体为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        //获取缓存的今天达标的次数
        simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(System.currentTimeMillis());

        //初始化控件
        caseTv = (TextView) findViewById(R.id.case_content);
        //设置今天时间达标的次数
        caseTv.setText(String.valueOf(mPref.getInt(simpleDateFormat.format(date), 0)));

        mBreakTimeView = (BreakTime) findViewById(R.id.break_time_view);
        mBreakTimeView.setBreakTimeListener(new BreakTime.BreakTimeListener() {
            @Override
            public void breakTime(long time) {
                //监听每一次停止

                if (time > 1800000) {
                    //大于半小时，则成功
                    Date date = new Date(System.currentTimeMillis());
                    int num = mPref.getInt(simpleDateFormat.format(date), 0);

                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putInt(simpleDateFormat.format(date), ++num);
                    editor.apply();

                    caseTv.setText(String.valueOf(num));
                }

            }
        });

    }

}
