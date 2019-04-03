package com.example.user.jsouptest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by user on 2018-06-01.
 */

public class StickChart extends AppCompatActivity {

    Toolbar toolbar;

    BarChart chart ;

    ArrayList<BarEntry> BARENTRY ;
    ArrayList<BarEntry> BARENTRY2 ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;


    BarDataSet Bardataset2 ;
    BarData BARDATA ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stick_chart_activity);

        // Toolbar를 생성한다.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //******************************* stick chart
        chart = (BarChart) findViewById(R.id.chart1);

        BARENTRY = new ArrayList<>();
        BARENTRY2 = new ArrayList<>();

        BarEntryLabels = new ArrayList<String>();

        AddValuesToBARENTRY();

        AddValuesToBarEntryLabels();

        // 차트 이름 하나 추가할 때 마다  ArrayList<BarEntry> , BarDataSet 변수 하나 씩 추가해서
        // 이 곳에 추가하기.
        Bardataset = new BarDataSet(BARENTRY, "음식");
        Bardataset2 = new BarDataSet(BARENTRY2, "바보");

        // 생성자에 들어가는 변수는 한계가 없는 듯 함. 최소 5개는 가능.
        BARDATA = new BarData(Bardataset,Bardataset2);

        // 그래프 색상 바꿀 때 설정
        Bardataset.setColors(ColorTemplate.rgb("#000000"));
        Bardataset2.setColors(Color.GREEN);


        chart.setData(BARDATA);

        chart.animateY(3000);

    }

    public void AddValuesToBARENTRY(){

        // 이 곳에서 차트에 대한 데이터 추가
        // (a,b) : a는 상단부에 표기될 숫자. 1부터 오름차순으로 설정 하자.
        //       : b는 그래프의 크기.
        BARENTRY.add(new BarEntry(1, 10));
        BARENTRY.add(new BarEntry(2, 3));
        BARENTRY.add(new BarEntry(3, 9));
        BARENTRY2.add(new BarEntry(4, 2));
        BARENTRY2.add(new BarEntry(5, 0));
        BARENTRY2.add(new BarEntry(6, 8));

    }

    // 사실상 필요 없음. 나중에 삭제하자.
    public void AddValuesToBarEntryLabels(){

        BarEntryLabels.add("January");
        BarEntryLabels.add("February");
        BarEntryLabels.add("March");
        BarEntryLabels.add("April");
        BarEntryLabels.add("May");
        BarEntryLabels.add("June");

    }


    //추가된 소스, ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.gragh, menu);
        return true;
    }
    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.chart1:
                Intent intent = new Intent(getApplicationContext(), ConsumptionTendencyActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "원 차트", Toast.LENGTH_LONG).show();
                return true;
            case R.id.chart2:
                Intent intent2 = new Intent(getApplicationContext(), StickChart.class);
                startActivity(intent2);
                Toast.makeText(getApplicationContext(), "막대 차트", Toast.LENGTH_LONG).show();
                return true;
            default:
                Intent intent3 = new Intent(getApplicationContext(), LineChart.class);
                startActivity(intent3);
                Toast.makeText(getApplicationContext(), "선 차트", Toast.LENGTH_LONG).show();
                return true;
        }
    }
}


