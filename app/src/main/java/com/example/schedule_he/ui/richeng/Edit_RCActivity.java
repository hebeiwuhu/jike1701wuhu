package com.example.schedule_he.ui.richeng;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.schedule_he.BarColor;
import com.example.schedule_he.R;
import com.example.schedule_he.ui.Side_Menu;
import com.example.schedule_he.ui.bianqian.EditActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Edit_RCActivity extends AppCompatActivity {//就是带有标题栏的Activity.

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    EditText et_rc_title;
    EditText et_rc_content;
    //private String content;
    //private String time;
    private String old_title;
    private String old_content;
    private String old_time="";
    private String timeshow;
    private String day;
    private String old_day;
    private long id=0;
    private int openMode = 0;
    public Intent intent = new Intent();
    public boolean tagChange = false;
    Toolbar toolbar;
    private Button set_date;
    private Button set_time;
    private TextView date;
    private TextView time;
    private int[] dateArray = new int[3];
    private int[] timeArray = new int[2];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {//初始化
        super.onCreate(savedInstanceState);

        /**根据夜间和非夜间选择图标*/
        if(!Side_Menu.night_mode){
            setContentView(R.layout.edit_rc_layout);
        }
        else{
            setContentView(R.layout.night_edit_rc_layout);
        }
        if(!Side_Menu.night_mode){
            BarColor.setStatusBarColor(Edit_RCActivity.this, Color.parseColor("#A09AB4"));
        }
        else{
            BarColor.setStatusBarColor(Edit_RCActivity.this,Color.parseColor("#3b3b3b"));
        }

        if (getSupportActionBar() != null){//去除默认的ActionBar
            getSupportActionBar().hide();
        }
        toolbar = (Toolbar) findViewById(R.id.edit_rc_Toolbar);

        //加载菜单
        if(!Side_Menu.night_mode){
            toolbar.inflateMenu(R.menu.rc_edit_menu);
            toolbar.setNavigationIcon(R.drawable.ic_back_edit_24dp);
        }
        else{
            toolbar.inflateMenu(R.menu.night_rc_edit_menu);
            toolbar.setNavigationIcon(R.drawable.ic_back_white_24dp);
        }
        toolbar.setTitle("日程编辑");


      /**左上角返回*/
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {//设置其点击事件
            @Override
            public void onClick(View v) {
                autoSetMessage();//跳转到日程编辑函数
                setResult(RESULT_OK,intent);
                finish();//结束该活动，回到之前的Activity
            }
        });


        /**右上角删除*/
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_rc_delete:
                        onEditRCDeleteClic();//跳转到删除函数
                        break;
                }
                return true;
            }
        });
        init();
    }



    /**删除特定日程的函数*/
    public void onEditRCDeleteClic() {
        new AlertDialog.Builder(Edit_RCActivity.this)
                .setMessage("删除吗？")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    //分两种情况：1：新编辑的还未保存 2：已经保存了的
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(openMode == 4) {//新
                            intent.putExtra("mode",-1);
                            intent.putExtra("old_day", old_day);
                            setResult(RESULT_OK,intent);
                        }
                        else{//已经保存的
                            intent.putExtra("mode",2);//需要删除当前
                            intent.putExtra("id",id);
                            intent.putExtra("old_day", old_day);
                            setResult(RESULT_OK,intent);
                        }
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {//点击取消
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();//创建并显示
    }

/**点击屏幕下方的返回键，自动保存编辑的日程*/
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_HOME){
            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_BACK){//点击返回键
            autoSetMessage();
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }



/**自动设置日程函数，点击返回键即视为编辑完成并保存结果*/
    public void autoSetMessage(){//日程编辑 分两种情况：1：新建笔记 2：打开已有笔记
        if(openMode == 4){//新建笔记  两种情况：1：没有输入标题。2：输入标题

            if(et_rc_title.getText().toString().length() == 0){//标题为空
                intent.putExtra("mode", -1); //-1代表什么也没发生
                Toast.makeText(Edit_RCActivity.this, "事件标题为空，未添加", Toast.LENGTH_LONG).show();
            }
            else{
                intent.putExtra("mode", 0); // 0代表创建了个新的笔记
                intent.putExtra("title", et_rc_title.getText().toString());
                intent.putExtra("content", et_rc_content.getText().toString());
                intent.putExtra("time", timeshow);
                intent.putExtra("day", day);//此处不应该为本日日期，而是应该是日历选中的时间
            }
        }
        else {//打开已有笔记  分三种情况：1：没做任何改变 2：删除了标题，直接删除日程 3：更改内容
            if (et_rc_title.getText().toString().equals(old_title)
                    && et_rc_content.getText().toString().equals(old_content)
                    && timeshow.equals(old_time))//啥也没改 标签也没变
                intent.putExtra("mode", -1); // edit nothing
            else if(et_rc_title.getText().toString().length() == 0){
                intent.putExtra("mode",2);//需要删除当前笔记
                intent.putExtra("id",id);
                Toast.makeText(Edit_RCActivity.this, "事件标题为空，已删除", Toast.LENGTH_LONG).show();
            }
            else {
                intent.putExtra("mode", 1); //mond 变为1  修改内容
                intent.putExtra("title", et_rc_title.getText().toString());
                intent.putExtra("content", et_rc_content.getText().toString());
                intent.putExtra("time", timeshow);
                intent.putExtra("id", id);
                intent.putExtra("day",day);
            }
        }
        intent.putExtra("old_day", old_day);
    }



    /**格式化输出*/
    public String dateToStr(){//获取时间并规定格式
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = simpleDateFormat.format(date);
        return s;
    }
    public String dateToStr_For_Tag(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = simpleDateFormat.format(date);
        String s2 = s.substring(0,4)+s.substring(5,7)+s.substring(8,10);//拼接为当前日期
        return s2;
    }




/**初始化编辑日程页面*/
    private void init(){
        et_rc_title=findViewById(R.id.et_title);
        et_rc_content=findViewById(R.id.et_rc_content);
        Intent getIntent = getIntent();
        openMode = getIntent.getIntExtra("mode",0);
        if (openMode == 3) {//打开已存在的note
            id = getIntent.getLongExtra("id", 0);
            old_title = getIntent.getStringExtra("title");
            old_content = getIntent.getStringExtra("content");
            old_time = getIntent.getStringExtra("time");
            old_day=getIntent.getStringExtra("day");
            et_rc_title.setText(old_title);
            et_rc_content.setText(old_content);//打开文本
            et_rc_content.setSelection(old_content.length());//光标定位到末尾
        }
        if(openMode == 4){//新建
            old_day=getIntent.getStringExtra("day");
        }
        timeshow=old_time;
        day=old_day;
        ///////////////////////////////////////
        Note_RC note_rc = new Note_RC();
        dateArray[0] = Integer.valueOf(day.substring(0,4));            //年
        dateArray[1] = Integer.valueOf(day.substring(4,6));      //月
        dateArray[2] = Integer.valueOf(day.substring(6,8));          //日
        timeArray[0] = note_rc.getHour();            //时
        timeArray[1] = note_rc.getMinute();          //分
        set_date = findViewById(R.id.set_date);
        set_time = findViewById(R.id.set_time);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        //initialize two textviews
        time.setText(old_time);
        setDateTV(dateArray[0], dateArray[1], dateArray[2]);//调用函数设置日程编辑初始化日期值
        if(old_time.equals("")){//设置日程编辑的初始化时间为当前时间+5分钟
            setTimeTV((timeArray[1]>54? timeArray[0]+1 : timeArray[0]), (timeArray[1]+5)%60);
        }
        //首先调用Calendar类获取年月日，然后将获取到的年月日放进new出来的DatePickerDialog中，这样就可以默认选中当前日期
        dateSetListener = new DatePickerDialog.OnDateSetListener() {//日期组件
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setDateTV(year, month+1, dayOfMonth);
            }
        };
        /**param
         * context：当前上下文
         * listener：时间改变监听器
         * hourOfDay：初始化的小时
         * minute：初始化的分钟
         * is24HourView：是否以24小时显示时间
         *
         */
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {//dialog弹出一个钟表选择时间，钟表仅在Android5.0以上有
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTimeTV(hourOfDay, minute);
            }
        };
        set_date.setOnClickListener(new View.OnClickListener() {//设置监听 设置日期
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(Edit_RCActivity.this,
                        R.style.DayDialogTheme, dateSetListener,
                        dateArray[0], dateArray[1] - 1, dateArray[2]);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable((isNightMode()?Color.BLACK : Color.WHITE)));

                dialog.show();
            }
        });
        set_time.setOnClickListener(new View.OnClickListener() {//设置监听 设置时间
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog1 = new TimePickerDialog(Edit_RCActivity.this,
                        R.style.DayDialogTheme, timeSetListener,
                        timeArray[0], timeArray[1], true);
                //dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog1.show();
            }
        });
    }




    /**格式化日期，时间*/
    private void setDateTV(int y, int m, int d){
        //update tv and dateArray
        String temp = y + "-";
        String temp_day = y + "";
        if(m<10){
            temp += "0";
            temp_day += "0";
        }
        temp += (m + "-");
        temp_day += m;
        if(d<10) {
            temp +="0";
            temp_day +="0";
        }
        temp += d;
        temp_day+=d;
        date.setText(temp);
        day=temp_day;
        dateArray[0] = y;
        dateArray[1] = m;
        dateArray[2] = d;
    }

    private void setTimeTV(int h, int m){
        //update tv and timeArra
        String temp = "";
        if(h<10) temp += "0";
        temp += (h + ":");
        if(m<10) temp += "0";
        temp += m;
        time.setText(temp);
        timeshow=temp;
        timeArray[0] = h;
        timeArray[1] = m;
    }

}
