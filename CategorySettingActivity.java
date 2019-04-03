package com.example.user.jsouptest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user on 2018-03-30.
 */

public class CategorySettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    final List<String> list = new ArrayList<>();
    ListViewAdapter adapter;
    ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();
    Intent intent;
    //private FloatingActionButton cateAdd;
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(CategorySettingActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorysetting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*** NavigationView ***/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_category);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_category);
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

        final Category category = new Category();
        final SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        //기본 제품
        editor.putString("1", "축산품");
        editor.putString("3", "스낵");
        editor.putString("2", "유제품");
        editor.putString("4", "농산품");
        editor.putString("5", "기타");
        editor.commit();


        final int[] CName = {sharedprefereneceUtil.getDataInt(this, "CName")};

        if(sharedprefereneceUtil.getDataInt(this, "CName") == 100 || sharedprefereneceUtil.getDataInt(this, "CName") == 6){
            CName[0] = 6;
            sharedprefereneceUtil.putDataInt(this,"CName",CName[0]);
        }


        // Adapter 생성
        adapter = new ListViewAdapter(itemList);
        // 리스트뷰 참조 및 Adapter달기
        final ListView listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);
        final Collection<?> col = pref.getAll().values();
        final Iterator<?> it = col.iterator();

        while (it.hasNext()) {
            String msg = (String) it.next();
            list.add(msg);
            //Ascending ascending = new Ascending();
            //Collections.sort(list, ascending); //배열 정렬
            adapter.addItem(msg);
            //Log.d("Result", msg+i+"/"+sharedprefereneceUtil.getDataInt(this,"CName"));

        }


        final Intent intent = new Intent(CategorySettingActivity.this, CategorySettingActivity.class);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(CategorySettingActivity.this);

                ad.setTitle("카테고리 제거");       // 제목 설정
                ad.setMessage(adapter.getItemList().get(listview.getCheckedItemPosition()).getCategory() + " (를)을 삭제하시겠습니까?");
                final String getName = adapter.getItemList().get(listview.getCheckedItemPosition()).getCategory();
// 확인 버튼 설정
                ad.setPositiveButton("제거", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count, checked;
                        checked = listview.getCheckedItemPosition();
                        count = sharedprefereneceUtil.getDataInt(getApplicationContext(),"CName");

                        if (count > 0) {

                            if (checked > -1 && checked < count) {
                                // 아이템 삭제
                                adapter.remove(checked);
                                for(int i = 1; i<count; i++){
                                    if(getName.equals(pref.getString(""+i,""))){
                                        //Log.d("Result commit",pref.getString(""+i,"")+" shared.num "+i);
                                        editor.remove(""+i).commit();
                                    }else{
                                        //Log.d("Result error",i +" "+ pref.getString(""+i,"")+" count: "+count);
                                    }
                                }

                                Toast.makeText(CategorySettingActivity.this, "삭제했습니다.", Toast.LENGTH_SHORT).show();
                                // listview 갱신.
                                adapter.notifyDataSetChanged();
                                //Intent intent = new Intent(CategorySettingActivity.this, CategorySettingActivity.class);
                                //startActivity(intent);
                                // listview 선택 초기화.
                                listview.clearChoices();
                                dialog.dismiss();     //닫기
                                // Event
                            }
                        }
                    }
                });

// 취소 버튼 설정
                ad.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.v(TAG,"No Btn Click");
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

// 창 띄우기
                ad.show();
            }

        });
// 카테고리 추가 버튼
        FloatingActionButton cateAdd = (FloatingActionButton) findViewById(R.id.cateAdd);
        cateAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder ad = new AlertDialog.Builder(CategorySettingActivity.this);
                ad.setTitle("카테고리추가");       // 제목 설정
                ad.setMessage("추가할 항목을 입력해 주세요");   // 내용 설정
                // EditText 삽입하기
                final EditText et = new EditText(CategorySettingActivity.this);
                ad.setView(et);

// 확인 버튼 설정
                ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = et.getText().toString();
                        editor.putString(""+CName[0], value);
                        editor.commit();
                        sharedprefereneceUtil.putDataInt(getApplicationContext(),"CName",sharedprefereneceUtil.getDataInt(getApplicationContext(),"CName")+1);
                        Log.d("Result",""+sharedprefereneceUtil.getDataInt(getApplicationContext(),"CName")+"/"+pref.getString(""+CName[0],""));
                        adapter.addItem(value);
                        adapter.notifyDataSetChanged();
                        startActivity(intent);
                        dialog.dismiss();     //닫기
                    }
                });

// 취소 버튼 설정
                ad.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
// 창 띄우기
                ad.show();
            }
        });


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            Intent intent = new Intent(CategorySettingActivity.this, CategorySettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(CategorySettingActivity.this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_delete) {
            if(sharedprefereneceUtil.getSharedPreference("userName","").equals("")){
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(CategorySettingActivity.this, DeleteHistoryActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_consumption) {
            Intent intent = new Intent(CategorySettingActivity.this, ConsumptionTendencyActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {
            Uri uri = Uri.parse("mailto:kiozxcbnm@gmail.com"); //우리 이메일로 문의사항받기
            String[] ccs = {"secondEmail@gmail.com"}; //참조
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra(Intent.EXTRA_TEXT, "문의하실 내용을 입력하세요.");
            it.putExtra(Intent.EXTRA_CC, ccs);
            startActivity(it);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_category);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class Ascending implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }

    }
}