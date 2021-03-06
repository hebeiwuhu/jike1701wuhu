package com.example.schedule_he.ui.richeng;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.schedule_he.R;

import com.example.schedule_he.ui.Side_Menu;
import com.example.schedule_he.ui.bianqian.HomeFragment;
import com.example.schedule_he.ui.bianqian.NoteDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment implements AdapterView.OnItemClickListener {//当被点击时调用方法。

    //private DashboardViewModel dashboardViewModel;

    private NoteBD_RC dbHelper = new NoteBD_RC(getContext());//sqlite数据库操作对象
    //private Context context = this;
    FloatingActionButton btn;//定义悬浮按钮
    private Note_RC_Adapter adapter;//数据适配器，标准化数据格式
    private List<Note_RC> noteList = new ArrayList<Note_RC>();


    private Toolbar myToolbar;//标准工具栏
    private ListView lv_rc;//以列表的形式展示具体内容，并且能够根据数据的长度自适应显示
    CalendarView myCalendarView;//日历组件
    TextView nong_Li;//文本框
    FloatingActionButton btn2;
    View root;//视图组件 矩形区域
    private String _day="";
    private String select_day="";

    private AlarmManager alarmManager;
/**可以通过它开发手机闹钟类的APP， 而在文档中的解释是：在特定的时刻为我们广播一个指定的Intent，简单说就是我们自己定一个时间，
 * 然后当到时间时，AlarmManager会为我们广播一个我们设定好的Intent，比如时间到了，可以指向某个 Activity或者Service
 * 简单说，就是在某个特定时刻运行你的代码，即使你的app没有运行。
  */
    /**oncreateview
     *
     * @param inflater 用来寻找xml布局下的具体的控件
     * @param container 表示容器
     * @param savedInstanceState 保存当前的状态，在活动的生命周期中，只要离开了可见阶段，活动很可能就会被进程终止，这种机制能保存当时的状态
     * @return 视图
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {//创建视图层次结构。



        /**夜间非夜间*/
        if(!Side_Menu.night_mode){//非夜间模式
            root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        }
        else{//夜间模式
            root = inflater.inflate(R.layout.night_layout_dashboard, container, false);
        }//inflater.inflate将xml转换成一个View对象，用于动态的创建布局
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);//获得闹钟实例对象
        myToolbar=root.findViewById(R.id.myToolbar2);
        setHasOptionsMenu(true);
        //加载菜单
        if(!Side_Menu.night_mode){
            myToolbar.inflateMenu(R.menu.rc_menu);
        }
        else{
            myToolbar.inflateMenu(R.menu.night_rc_menu);
        }
        myToolbar.setTitle("日程");//设置标题



        /**侧栏菜单*/
        if(!Side_Menu.night_mode){//侧栏菜单的设置
            myToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        }
        else{
            myToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        }
        final Side_Menu side_menu = new Side_Menu(root,this,R.id.dashboard_layout,2);
        side_menu.initPopupView();//弹出框
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击时弹出侧栏菜单
                side_menu.showPopUpView();
            }
        });


/**添加日程*/
        btn2=(FloatingActionButton) root.findViewById(R.id.add_richeng);//添加日程
        btn2.setOnClickListener(new View.OnClickListener() {//点击添加    设置监听
            @Override
            public void onClick(View v) {//点击时触发
                Intent intent = new Intent(getContext(), Edit_RCActivity.class);//意图
                intent.putExtra("mode",4);//模式为4 代表新建笔记
                intent.putExtra("day",select_day);
                startActivityForResult(intent,0);//启功EditActivity,并获取结果
            }
        });


/**日历组件*/
        myCalendarView = (CalendarView) root.findViewById(R.id.cal_View);//日历组件
        Date date = new Date();
        date.setTime(myCalendarView.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式
        _day=sdf.format(date);
        select_day=_day;//设置选中值初始为当前值
        //设置日历点击监听
        myCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {//  日期转换的监听
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month=month+1;
                String day_format="";
                if(month<=9){
                    if(dayOfMonth<=9){
                        day_format=year+"0"+month+"0"+dayOfMonth;
                    }else{
                        day_format=year+"0"+month+dayOfMonth;
                    }
                }
                else{
                    if(dayOfMonth<=9){
                        day_format=year+""+month+"0"+dayOfMonth;
                    }else{
                        day_format=year+""+month+dayOfMonth;
                    }
                }
                select_day=day_format;
                refreshListViwe(day_format);
                //农历
                try {
                    nong_Li.setText(CalendarUtil.solarToLunar(select_day));//调用阳历转阴历转换方法
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



/**菜单元素监听*/
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {//设置点击菜单的监听
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rc_delete_all:
                        onDeleteAllClic();//点击全部删除，跳转到对应函数
                        break;

                        /** protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                         super.onActivityResult(requestCode, resultCode, data);
                         if (requestCode == 3 && resultCode == RESULT_OK) {
                         // SearchAddressInfo info = (SearchAddressInfo) data.getParcelableExtra("position");
                         String position = data.getStringExtra("position");
                         mTvClockInAddress.setText(position);
                         }
                         }
                        break;*/
                }
                return true;
            }
        });
        nong_Li=root.findViewById(R.id.non_Li);
        try {
            nong_Li.setText(CalendarUtil.solarToLunar(select_day));//调用阳历转阴历函数
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**给文本设置两个监听器：点击和长按*/
        lv_rc = root.findViewById(R.id.lv_Cale);
        adapter = new Note_RC_Adapter(root.getContext(), noteList);
        refreshListViwe(_day);
        lv_rc.setAdapter(adapter);//给ListView设置适配器显示内容
        lv_rc.setOnItemClickListener(this);//给文本设置点击事件监听器
        lv_rc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//长按监听器
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(root.getContext());
                builder.setMessage("确定删除?");
                builder.setTitle("提示");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {//如果点击确定
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note_RC note=(Note_RC)adapter.getItem(position);
                        if(adapter.get_RC_NoteList().remove(position)!=null){
                            Note_RC curNote = new Note_RC();
                            curNote.setId(note.getId());
                            DBop_rc op = new DBop_rc(getContext());//操纵数据库
                            op.open();
                            cancelAlarm(curNote);
                            op.removeNote(curNote);//删除该日程
                            op.close();
                            //System.out.println("success");
                        }else {
                            //System.out.println("failed");
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {//如果点击取消
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
        return root;
    }



/**点击全部删除所触发的函数*/
    private void onDeleteAllClic(){//点击全部删除所触发的函数
        new AlertDialog.Builder(root.getContext())
                .setMessage("删除全部吗？")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//点击确定删除
                        DBop_rc op = new DBop_rc(getContext());//操纵数据库
                        op.open();
                        cancelAlarms(op.getAllNotes());
                        op.close();

                        dbHelper = new NoteBD_RC(getContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("notes_rc", null, null);//删除数据库中数据
                        db.execSQL("update sqlite_sequence set seq=0 where name='notes_rc'");
                        refreshListViwe(_day);//刷新
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//点击取消
                dialog.dismiss();
            }
        }).create().show();
    }


    //接收startActivityForResult的结果
    /**新的Activity 关闭后会向前面的Activity传回数据，为了得到传回的数据，必须在前面的Activity中重写onActivityResult*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//用来接收传回来的
        String old_day=select_day;
        int returnMode;
        long note_id;
        returnMode=data.getExtras().getInt("mode",-1);//获取模式 默认-1 啥也没改
        note_id = data.getExtras().getLong("id",0);

        if (returnMode == 1) {  //更新当前笔记
            String title = data.getExtras().getString("title");
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            String day = data.getExtras().getString("day");
            old_day=day;//用于刷新列表的day
            Note_RC newNote = new Note_RC(title,content, time,day);
            newNote.setId(note_id);
            DBop_rc op = new DBop_rc(getContext());
            op.open();
            op.updateNote(newNote);
            //更新闹钟
            cancelAlarm(newNote);//删除闹钟
            //startAlarm(newNote);//添加新闹钟
            op.close();
        } else if (returnMode == 0) {  // 新建了一个
            String title = data.getExtras().getString("title");
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            String day = data.getExtras().getString("day");
            old_day=day;
            Note_RC newNote = new Note_RC(title,content,time,day);
            DBop_rc op = new DBop_rc(getContext());
            op.open();
            op.addNote(newNote);
            //添加闹钟
            //startAlarm(newNote);//添加新闹钟
            op.close();
        } else if (returnMode == 2) { //删除
            Note_RC curNote = new Note_RC();
            curNote.setId(note_id);
            DBop_rc op = new DBop_rc(getContext());
            op.open();
            op.removeNote(curNote);
            //删除闹钟
            cancelAlarm(curNote);//删除闹钟
            op.close();
        }
        else{//什么也不做

        }
        String showday = data.getExtras().getString("old_day");
        refreshListViwe(showday);//刷新
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void refreshListViwe(String day){//更新内容 使用DBOP_rc类
        try {
            nong_Li.setText(CalendarUtil.solarToLunar(day));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBop_rc op = new DBop_rc(getContext());
        op.open();
        // set adapter
        if (noteList.size() > 0){
            noteList.clear();
        }
        noteList.addAll(op.getAllDayNotes(day));
        startAlarms(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }




    /**你点击的到底是哪个控件，存储下此控件信息，然后可以操纵那个控件*/
    @Override
 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_Cale://存入各种数据
                Note_RC curNote = (Note_RC) parent.getItemAtPosition(position);
                Intent intent = new Intent(root.getContext(), Edit_RCActivity.class);
                intent.putExtra("title", curNote.getTitle());
                intent.putExtra("content", curNote.getContent());
                intent.putExtra("id", curNote.getId());
                intent.putExtra("time", curNote.getTime());
                intent.putExtra("mode", 3);     //打开模式   此处为3 即代表打开的是已有文件
                intent.putExtra("day",curNote.getDay());
                startActivityForResult(intent, 1);//启动intent对象
                break;
        }
    }







    /////////设置提醒 失败

    //设置提醒
    private void startAlarm(Note_RC p) {
        Calendar c = p.getPlanTime();
        if(!c.before(Calendar.getInstance())) {
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra("title", p.getTitle());
            intent.putExtra("content", p.getContent());
            intent.putExtra("id", (int)p.getId());

            //是intent的封装，PendingIntent将某个动作的触发时机交给其他应用；让那个应用代表自己去执行那个动作（权限都给他）
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) p.getId(), intent, 0);
            //单次提醒
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }

    //设置很多提醒
    private void startAlarms(List<Note_RC> plans){
        for(Note_RC note_rc : plans){
            startAlarm(note_rc);
        }
    }

    //取消提醒
    private void cancelAlarm(Note_RC p) {
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int)p.getId(), intent, 0);
        alarmManager.cancel(pendingIntent);

    }

    //取消很多提醒
    private void cancelAlarms(List<Note_RC> plans){
        for(Note_RC note_rc : plans){
            cancelAlarm(note_rc);
        }
    }

}
