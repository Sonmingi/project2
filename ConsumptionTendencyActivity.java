package com.example.user.jsouptest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * Created by user on 2018-03-23.
 */

public class ConsumptionTendencyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    PieChart pieChart;
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(ConsumptionTendencyActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumptiontendency);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*** NavigationView ***/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_consumption);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_consumption);
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

        //******************************* pid chart
        pieChart = (PieChart)findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.setTransparentCircleRadius(61f);

        // Text Label Color Set;
        pieChart.setEntryLabelColor(Color.DKGRAY);

       /* String name1 = SharedprefereneceUtil.getData(this, "SharedName");
        String name2 = SharedprefereneceUtil.getData(this, "SharedCount");
        String name3 = SharedprefereneceUtil.getData(this, "SharedDay");
*/


        Log.i("aa",""+deletecnt);
        Log.i("bb",""+usedcnt);

        String name1 = "사용된 제품";
        String name2 = "삭제된 제품";

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        if(name1 != null){
            yValues.add(new PieEntry(realused/wholecnt,name1));
        }

        if(name2 != null){
            yValues.add(new PieEntry(realdelete/wholecnt,name2));
        }

        Description description = new Description();
        description.setText("사용 / 삭제,만료 비율"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        // PieChart Color
        dataSet.setColors(ColorTemplateCustom.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(15f);
        // Label Ratio Color Set
        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            Intent intent = new Intent(this, CategorySettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_delete) {
            if(sharedprefereneceUtil.getSharedPreference("userName","").equals("")){
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, DeleteHistoryActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_consumption) {
            Intent intent = new Intent(this, ConsumptionTendencyActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("mailto:kiozxcbnm@gmail.com"); //우리 이메일로 문의사항받기
            String[] ccs = {"secondEmail@gmail.com"}; //참조
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra(Intent.EXTRA_TEXT, "문의하실 내용을 입력하세요.");
            it.putExtra(Intent.EXTRA_CC, ccs);
            startActivity(it);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_consumption);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}