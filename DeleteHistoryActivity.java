package com.example.user.jsouptest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 2018-03-30.
 */

public class DeleteHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView mRecyclerView;
    private ListAdapter mListadapter;
    private Adapter adapter;
    ArrayList data = new ArrayList<DataNote>();
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(this);
    private String deleteItemCount;
    private String usedItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletehistory);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String user = sharedprefereneceUtil.getSharedPreference("userName",null);
            if(user.equals("")){
            user = "aabbcc";
        }
        /*** NavigationView ***/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_delete);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_delete);
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

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user").child(user);
        final DatabaseReference deleteRef = mRef.child("Delete");
        final DatabaseReference UsedRef = mRef.child("Used");
        final Calendar calendar = Calendar.getInstance();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView4);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        Log.d("달달달",""+calendar.get(Calendar.MONTH));
        final loadBitmap loadBitmap = new loadBitmap();

        new Thread() {
            @Override
            public void run() {
                deleteRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            String getkey = dataSnapshot.getRef().getParent().toString();
                            Log.d("========",getkey);
                            String Key = dataSnapshot.getKey().toString(); // 파이어베이스상에서 키값 저장
                            Log.d("history", "" + dataSnapshot.getKey());
                            Log.d("history", "" + dataSnapshot.child("name").getValue());
                            String name = dataSnapshot.child("name").getValue().toString();
                            String date = dataSnapshot.child("date").getValue().toString();
                            //String img = dataSnapshot.child("Image").getValue().toString();
                            String Category = dataSnapshot.child("Category").getValue().toString();

                            if (Integer.parseInt(dataSnapshot.child("DeleteMonth").getValue().toString())!=(calendar.get(Calendar.MONTH)+1)){ //한달이지나면 데이터 삭제
                                deleteRef.child(Key).removeValue();
                                mListadapter.notifyDataSetChanged();
                            }
                            DataNote dataNote = new DataNote();
                            dataNote.setKey(Key);
                            dataNote.setComment(name);
                            dataNote.setText("");
                            dataNote.setDate(date);
                            //dataNote.setImgurl(img);
                            dataNote.setCategory(Category);
                            dataNote.setState("삭제된 물품");
/*
                            if (img.equals("")) {
                                dataNote.setImg(null);
                            } else {
                                dataNote.setImg(getResizedBitmap(loadBitmap.loadBitmap(img), 200,200));
                            }*/
                            data.add(dataNote);
                            CompareDateAsc compareDateAsc = new CompareDateAsc();
                            Collections.sort(data, compareDateAsc);
                            mListadapter = new ListAdapter(data);
                            mRecyclerView.setAdapter(mListadapter);
                            runOnUiThread ( new Runnable ()
                            {
                                @Override
                                public void run ()
                                {

                                    mListadapter.notifyDataSetChanged();
                                    /// 어댑터 업데이트

                                }
                            });

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        //Log.d("삭제갯수",""+data.size());
                        deleteItemCount = Integer.toString(data.size());
                        SharedPreferences sharedPreferences = getSharedPreferences("cnt", Context.MODE_PRIVATE);
                        if(data.size() == 0) {

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putInt("delete", 0);
                            editor.commit();
                        } else {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("delete", Integer.parseInt(deleteItemCount));
                            editor.commit();

                           Log.i("bbbbbbbbbb삭제", deleteItemCount);
                           // Log.i("ccc삭제", sharedPreferences.getString("delete", "ccc"));
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }.start();

       usedLoad();

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
            }
            else {
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_delete);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
    {

        private ArrayList<DataNote> dataList;
        public ListAdapter(ArrayList<DataNote> data)
        {
            this.dataList = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView textViewText;
            TextView textViewComment;
            TextView textViewDate;
            TextView textCategory;
            //ImageView imageView;
            TextView state;
            CheckBox checkBox;
            public ViewHolder(View itemView)
            {
                super(itemView);
                this.textViewText = (TextView) itemView.findViewById(R.id.text);
                this.textViewComment = (TextView) itemView.findViewById(R.id.comment);
                this.textViewDate = (TextView) itemView.findViewById(R.id.date);
                //this.imageView = (ImageView) itemView.findViewById(R.id.ListImage);
                this.textCategory = (TextView) itemView.findViewById(R.id.Category01);
                this.state = (TextView)itemView.findViewById(R.id.state);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);

            ListAdapter.ViewHolder viewHolder = new ListAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position)
        {
            holder.textViewText.setText(dataList.get(position).getText());
            holder.textViewComment.setText(dataList.get(position).getComment());
            holder.textViewDate.setText(dataList.get(position).getDate());
            holder.textCategory.setText(dataList.get(position).getCategory());
            holder.state.setText(dataList.get(position).getState());
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   // Toast.makeText(DeleteHistoryActivity.this, "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount()
        {
            return dataList.size();
        }
    }
    class CompareDateAsc implements Comparator<DataNote> { //유통기한 오름차순
        @Override
        public int compare(DataNote o1, DataNote o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


    public void usedLoad() {
        String user = sharedprefereneceUtil.getSharedPreference("userName",null);
        if(user.equals("")){
            user = "aabbcc";
        }
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user").child(user);
        final DatabaseReference deleteRef = mRef.child("Delete");
        final DatabaseReference UsedRef = mRef.child("Used");
        final Calendar calendar = Calendar.getInstance();
        new Thread() {
            @Override
            public void run() {
                UsedRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            String getkey = dataSnapshot.getRef().getParent().toString();
                            Log.d("========",getkey);
                            String Key = dataSnapshot.getKey().toString(); // 파이어베이스상에서 키값 저장
                            Log.d("history", "" + dataSnapshot.getKey());
                            Log.d("history", "" + dataSnapshot.child("name").getValue());
                            String name = dataSnapshot.child("name").getValue().toString();
                            String date = dataSnapshot.child("date").getValue().toString();
                            //String img = dataSnapshot.child("Image").getValue().toString();
                            String Category = dataSnapshot.child("Category").getValue().toString();
                            if (Integer.parseInt(dataSnapshot.child("DeleteMonth").getValue().toString())!=(calendar.get(Calendar.MONTH)+1)){ //한달이지나면 데이터 삭제
                                UsedRef.child(Key).removeValue();
                                mListadapter.notifyDataSetChanged();
                            }
                            DataNote dataNote = new DataNote();
                            dataNote.setKey(Key);
                            dataNote.setComment(name);
                            dataNote.setText("");
                            dataNote.setDate(date);
                            //dataNote.setImgurl(img);
                            dataNote.setCategory(Category);
                            dataNote.setState("사용된 물품");

                            /*
                            if (img.equals("")) {
                                dataNote.setImg(null);
                            } else {
                                dataNote.setImg(getResizedBitmap(loadBitmap.loadBitmap(img), 200,200));
                            }*/
                            data.add(dataNote);
                            CompareDateAsc compareDateAsc = new CompareDateAsc();
                            Collections.sort(data, compareDateAsc);
                            mListadapter = new ListAdapter(data);
                            mRecyclerView.setAdapter(mListadapter);

                            runOnUiThread ( new Runnable ()
                            {
                                @Override
                                public void run ()
                                {

                                    mListadapter.notifyDataSetChanged();
                                    /// 어댑터 업데이트

                                }
                            });

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        //Log.d("사용갯수",""+data.size());
                        usedItemCount = Integer.toString(data.size());

                        SharedPreferences sharedPreferences = getSharedPreferences("cnt", Context.MODE_PRIVATE);
                        if(data.size() == 0) {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("used", 0);
                            editor.commit();
                        } else {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("used", Integer.parseInt(usedItemCount));
                            editor.commit();

                            Log.i("bbbbbbbbbb사용", usedItemCount);
                            //Log.i("ccc사용", sharedPreferences.getString("used", "ccc"));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        }.start();

    }
}
