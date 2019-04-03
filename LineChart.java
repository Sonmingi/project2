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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by user on 2018-06-01.
 */

public class LineChart extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_chart_activity);

        // Toolbar를 생성한다.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //*******************************line chart
        BarChart barChart = (BarChart) findViewById(R.id.chart1);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
//        barChart.setDescription("");
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(0.90f);
        xl.setCenterAxisLabels(true);
        xl.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }

        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }

        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        barChart.getAxisRight().setEnabled(false);



        //데이터 폭. 이곳을 수정하면 더 많은 데이터 삽입 가능.
        float groupSpace = 0.03f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.27f; // x2 dataset
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"
        // xl.setGranularity(1f); 에서 1f에 맞춘 값.

        // 상단부에 나오는 숫자
        int startYear = 1980;
        int endYear = 1985;


        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<BarEntry> yVals2 = new ArrayList<BarEntry>();
        List<BarEntry> yVals3 = new ArrayList<BarEntry>();

        int indexNumber1 = 1;
        int indexNumber2 = 3;
        int indexNumber3 = 4;

        // 항목 추가. 이곳에 들어가는 0.4/ 0.7 값이 실 데이터
        for (int i = startYear; i < endYear; i++) {
            yVals1.add(new BarEntry(i, indexNumber1++));
        }

        for (int i = startYear; i < endYear; i++) {
            yVals2.add(new BarEntry(i, indexNumber2++));
        }

        for (int i = startYear; i < endYear; i++) {
            yVals3.add(new BarEntry(i, indexNumber3));
            indexNumber3 += 2;
        }


        BarDataSet set1, set2, set3;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet)barChart.getData().getDataSetByIndex(0);
            set3 = (BarDataSet)barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            set3.setValues(yVals3);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create 2 datasets with different types
            set1 = new BarDataSet(yVals1, "버려짐");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(yVals2, "사용됨");
            set2.setColor(Color.rgb(164, 228, 251));
            set3 = new BarDataSet(yVals3, "전체");
            set3.setColor(Color.rgb(126, 120, 110));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);
            dataSets.add(set3);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(startYear);
        barChart.groupBars(startYear, groupSpace, barSpace);
        barChart.invalidate();
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

