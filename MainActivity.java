package com.example.user.jsouptest;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.lang.*;

import static android.support.v4.view.PagerAdapter.POSITION_NONE;
import static com.example.user.jsouptest.PermissionRequester.REQUEST_PERMISSION;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ViewPager vp;
    private FloatingActionButton fab;
    Intent intent;
    static int n = 0;
    String imgURL = "";
    String Name = "";
    String Date = "";
    pagerAdapter vpAdapter = new pagerAdapter(getSupportFragmentManager());
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(this);
    private BackPressCloseHandler backPressCloseHandler;    // 뒤로가기 종료를 하기위한 핸들러 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout mTab = (TabLayout) findViewById(R.id.tab);
        intent = getIntent();


        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();



        //기본 제품
        editor.putString("2", "유제품");
        editor.putString("4", "농산품");
        editor.putString("1", "축산품");
        editor.putString("3", "스낵");
        editor.putString("5", "기타");
        editor.commit();

        Log.d("리시버등록","Main_onCreate()");



        /*** FlotingActionButton ***/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedprefereneceUtil.getSharedPreference("userName","").equals("")){
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    Toast.makeText(MainActivity.this,"아이디를 설정해주세요",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MainActivity.this, BarcodeScanner.class);
                    startActivity(intent);
                }

            }
        });
        /**************************/

        /*** NavigationView ***/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_headerView = navigationView.getHeaderView(0);
        TextView nav_header_text = (TextView)nav_headerView.findViewById(R.id.userName);
        nav_header_text.setText(sharedprefereneceUtil.getSharedPreference("userName","이름을 설정해주세요."));
        ImageView nav_header_img = (ImageView)nav_headerView.findViewById(R.id.imageView);
        TextView nav_header_grade = (TextView)nav_headerView.findViewById(R.id.grade);


        SharedPreferences sharedPreferences = getSharedPreferences("cnt", Context.MODE_PRIVATE);
        int deletecnt = sharedPreferences.getInt("delete", 0);
        int usedcnt = sharedPreferences.getInt("used", 0);
        int div = 0;

        float realused = 0;
        float realdelete= 0;
        float wholecnt = 0;

        if(deletecnt>=usedcnt){
            div = deletecnt-usedcnt;
            wholecnt = div +usedcnt;
            realused = wholecnt-div;//used
            realdelete = div;
        }else{
            div = usedcnt-deletecnt;
            wholecnt = div+deletecnt;
            realdelete = wholecnt-div;//delete
            realused = div;
        }


        Log.i("*****",""+div/wholecnt*100  + "사용 :" + usedcnt + "삭제 :" +deletecnt);

        //사용자 등급에따라서 이모티콘 변화
        if(realused/wholecnt*100 >= 70) {
            nav_header_img.setImageResource(R.drawable.ic_grade1);
            nav_header_grade.setText("우수");
        }
        else if(realused/wholecnt*100 < 70 && realused/wholecnt*100 >= 30) {
            nav_header_img.setImageResource(R.drawable.ic_grade3);
            nav_header_grade.setText("보통");
        }
        else  {
            nav_header_img.setImageResource(R.drawable.ic_grade5);
            nav_header_grade.setText("미흡");
        }
        /**************************/

        /*** ViewPager ***/
        vp = (ViewPager) findViewById(R.id.vp);

        vp.setAdapter(vpAdapter);
        vp.setCurrentItem(0);
        vp.setOffscreenPageLimit(3); //뷰페이저를 미리 로드
        mTab.setupWithViewPager(vp);

        /*******************/
/*
        imgURL = intent.getStringExtra("imgURL");
        Name = intent.getStringExtra("name");
        Date = intent.getStringExtra("Date");
        Intent intent1 = new Intent(MainActivity.this, Frag1.class);
        intent1.putExtra("imgURL", imgURL);
        intent1.putExtra("name", Name);
        intent1.putExtra("Date", Date);
*/

        backPressCloseHandler = new BackPressCloseHandler(this);



    }


    /*** ViewPager 핸들링 ***/
    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {

            switch(position)
            {
                case 0:
                    return new Frag1();
                case 1:
                    return new Frag2();
                case 2:
                    return new Frag3();
                default:
                    return null;
            }
        }
        @Override
        public int getCount() { return 3;}
        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;
        }
        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return "전체";
                case 1:
                    return "임박";
                case 2:
                    return "만료";
                default:
                    return null;
            }
        }

    }

    /******************************************/

//뒤로가기 이벤트
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.days:
                Toast.makeText(this,"유통기한순으로 정렬", Toast.LENGTH_SHORT).show();
                n=0;

                finish();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.category:
                Toast.makeText(this,"카테고리순으로 정렬", Toast.LENGTH_SHORT).show();
                n=1;
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return  true;
            case R.id.edit:
                try {
                    String user = sharedprefereneceUtil.getSharedPreference("userName", null);
                    if(user.equals("")){
                        user = "aabbcc";
                    } else {
                        Intent intent = new Intent(MainActivity.this, CheckBoxDeleteActivity.class);
                        startActivity(intent);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(this, "아이디를 설정해주세요.", Toast.LENGTH_SHORT).show();

                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            Intent intent = new Intent(MainActivity.this, CategorySettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_delete) {
            if(sharedprefereneceUtil.getSharedPreference("userName","").equals("")){
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, DeleteHistoryActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_consumption) {
            Intent intent = new Intent(MainActivity.this, ConsumptionTendencyActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("mailto:kiozxcbnm@gmail.com"); //우리 이메일로 문의사항받기
            String[] ccs = {"secondEmail@gmail.com"}; //참조
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra(Intent.EXTRA_TEXT, "문의하실 내용을 입력하세요.");
            it.putExtra(Intent.EXTRA_CC, ccs);
            startActivity(it);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
@Override
    protected void onResume(){
        super.onResume();
        vpAdapter.notifyDataSetChanged();
}
}
