package com.example.ly_lipengfei.abreak;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView mCaseTv;
    private TextView mTimeTv;
    private BreakTime mBreakTimeView;

    private SimpleDateFormat mSimpleDateFormat;

    //SharedPreferences
    private SharedPreferences mPref;
    private final String PREF_NAME = "myRecord";

    private static final String STR_TIME = "TIME";
    private static final String STR_CASE = "CASE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //全屏幕情况下，把状态栏标记为浅色，则字体为深色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        //获取缓存的今天达标的次数
        mSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(System.currentTimeMillis());

        //初始化控件
        mCaseTv = (TextView) findViewById(R.id.case_content);
        mTimeTv = (TextView) findViewById(R.id.time_content);

        //设置今天时间达标的次数
        mCaseTv.setText(String.valueOf(mPref.getInt(mSimpleDateFormat.format(date) + STR_CASE, 0)));
        mTimeTv.setText(String.format("%.1f", mPref.getFloat(mSimpleDateFormat.format(date) + STR_TIME, 0)));

        mBreakTimeView = (BreakTime) findViewById(R.id.break_time_view);
        mBreakTimeView.setBreakTimeListener(new BreakTime.BreakTimeListener() {
            @Override
            public void breakTime(long time) {
                //监听每一次停止，大于半小时，则算完成一个番茄时段
                if (time > 1800000) {
                    //获取存储的次数和时间
                    Date date = new Date(System.currentTimeMillis());
                    int oldCaseNum = mPref.getInt(mSimpleDateFormat.format(date) + STR_CASE, 0);
                    float oldTimeHour = mPref.getFloat(mSimpleDateFormat.format(date) + STR_TIME, 0);

                    //计算更新后的次数和时间
                    final int newCaseNum = ++oldCaseNum;
                    final float newTimeHour = oldTimeHour + (float) time / 3600000;

                    //保存更新后的次数和时间
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putInt(mSimpleDateFormat.format(date) + STR_CASE, newCaseNum);
                    editor.putFloat(mSimpleDateFormat.format(date) + STR_TIME, newTimeHour);
                    editor.apply();

                    mCaseTv.post(new Runnable() {
                        @Override
                        public void run() {
                            //在主线程设置更新的结果
                            mCaseTv.setText(String.valueOf(newCaseNum));
                            mTimeTv.setText(String.format("%.1f", newTimeHour));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBreakTimeView.reset();
    }
}

