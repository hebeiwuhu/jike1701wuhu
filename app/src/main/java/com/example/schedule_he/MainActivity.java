package com.example.schedule_he;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toolbar;

import com.example.schedule_he.ui.Side_Menu;
import com.example.schedule_he.ui.bianqian.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration;
        appBarConfiguration= new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                    .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //new 一个Side_Menu对象，调�dsdsd�其构造方法，获取保存的夜间模式状�?
        Side_Menu side_menu = new Side_Menu(this);

        //我的代码分割�?/////////////////////////////////////////
        if (getSupportActionBar() != null){//去除默认的ActionBar
            getSupportActionBar().hide();
        }

//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);


        int [][] states=new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };
        int[] colors=new int[]{
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.colorAccent)
        };
        ColorStateList csl = new ColorStateList(states,colors);

        //setStatusBarColor(MainActivity.this,6);
        if(Side_Menu.night_mode){
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.night_bottom_nav_menu);
            navView.setBackgroundColor(Color.BLACK);
            navView.setItemTextColor(csl);
            navView.setItemIconTintList(csl);
        }
        else{
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.bottom_nav_menu);
        }

        //状态栏颜色
        if(!Side_Menu.night_mode){
            setStatusBarColor(MainActivity.this,Color.parseColor("#A09AB4"));
        }
        else{
            setStatusBarColor(MainActivity.this,Color.parseColor("#3b3b3b"));
        }

        //用来常驻后台
        //Intent forgroundService = new Intent(this,BackGroundService.class);
        //startService(forgroundService);
    }

    //设置状态栏颜色
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模�?
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状�?
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

}
