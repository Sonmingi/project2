package com.example.user.jsouptest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 2018-03-13.
 */

public class Frag3 extends Fragment {
    protected int expirItemCount=0;
    private RecyclerView mRecyclerView;
    private Frag3.ListAdapter mListadapter;
    Bundle bundle;
    Intent intent;
    String name;
    String date;
    String img;

    private int tYear;
    private int tMonth;
    private int tDay;

    private int dYear =1;
    private int dMonth=1;
    private int dDay = 1;

    private long dd;
    private long tt;
    private long rr;

    private  int resultNumber = 0;


    ArrayList data = new ArrayList<DataNote>();

    //DatabaseReference getDateRef = FirebaseDatabase.getInstance().getReference("Data");
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        intent = getActivity().getIntent();
        bundle = getArguments();
        View view = inflater.inflate(R.layout.frag3, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView3);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //현재 날짜
        final Calendar calendar = Calendar.getInstance();
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH) + 1;
        tDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH,1);
        //Log.d("오늘 날짜4 ",""+tYear+tMonth+tDay);
        final SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(getActivity());
        String user = sharedprefereneceUtil.getSharedPreference("userName","");
        if(user.equals("")){
            user = "aabbcc";
        }
        final DatabaseReference DateRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/user").child(user);
        final DatabaseReference getDateRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/user").child(user).child("Data");
        final DatabaseReference mdeleteRef = DateRef.child("Delete").push();
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                data.clear();
                new Thread(){
                    @Override
                    public  void run(){

                        getDateRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Log.d("Frag3",""+dataSnapshot.getKey());
                                Log.d("Frag3",""+dataSnapshot.child("name").getValue());

                                try{
                                    String Key = dataSnapshot.getKey().toString(); // 파이어베이스상에서 키값 저장
                                    String Category = dataSnapshot.child("Category").getValue().toString();
                                    name = dataSnapshot.child("name").getValue().toString();
                                    date = dataSnapshot.child("date").getValue().toString();
                                    img = dataSnapshot.child("Image").getValue().toString();
                                    Calendar calendar1 = Calendar.getInstance();
                                    intent.getStringExtra("year");
                                    String[] text = date.split("-");
                                    String year = text[0];
                                    String month = text[1];
                                    String day = text[2];

                                    int y = Integer.parseInt(year);
                                    int m = Integer.parseInt(month);
                                    int d = Integer.parseInt(day);
                                    calendar1.set(Calendar.YEAR,y); //d-day 날짜를 입력
                                    calendar1.set(Calendar.MONTH,m);
                                    calendar1.set(Calendar.DAY_OF_MONTH,d);
                                    if(d==31){
                                        calendar1.add(Calendar.DATE,-1);
                                    }
                                    //Log.d(" 월 ",""+calendar1.get(Calendar.DAY_OF_MONTH));
                                    //Log.d("dday날짜날짜",""+calendar1.get(Calendar.YEAR)+(calendar1.get(Calendar.DAY_OF_MONTH)+1)+calendar1.get(Calendar.DAY_OF_MONTH));
                                    tt=calendar.getTimeInMillis()/86400000;
                                    dd=calendar1.getTimeInMillis()/86400000;
                                    rr=(dd-tt);
                                    resultNumber = (int)rr;


                                    getDateRef.child(dataSnapshot.getKey()).child("d-day").setValue(resultNumber);
                                    //dataNote.setDday(null);
                                    if(resultNumber<0) {
                                        DataNote dataNote = new DataNote();
                                        dataNote.setKey(Key);
                                        dataNote.setComment(name);
                                        dataNote.setText("");
                                        dataNote.setDate(date);
                                        dataNote.setImgurl(img);
                                        dataNote.setCategory(Category);
                                        dataNote.setDday(d_day(resultNumber));
                                        if(-sharedprefereneceUtil.getSharedPreference("Number_Expire",7)>=resultNumber){ //만료 기간에따라 삭제
                                            mdeleteRef.child("Category").setValue(Category);
                                            mdeleteRef.child("name").setValue(name);
                                            mdeleteRef.child("image").setValue(img);
                                            mdeleteRef.child("date").setValue(date);
                                            mdeleteRef.child("DeleteMonth").setValue(calendar.get(Calendar.MONTH));
                                            getDateRef.child(Key).removeValue();
                                            mListadapter.notifyDataSetChanged();
                                        }
                                        Log.d("데이",""+calendar.get(Calendar.DAY_OF_MONTH));
                                        if (img.equals("")) {
                                            dataNote.setImg(null);
                                        } else {
                                            dataNote.setImg(getResizedBitmap(loadBitmap(img), 200,200));

                                        }
                                        data.add(dataNote);
                                        Frag3.CompareDateAsc compareDateAsc = new Frag3.CompareDateAsc();
                                        Collections.sort(data, compareDateAsc);
                                        mListadapter = new Frag3.ListAdapter(data);
                                        mRecyclerView.setAdapter(mListadapter);


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mListadapter.notifyDataSetChanged();
                                                /// 어댑터 업데이트

                                            }
                                        });
                                    }else{
                                        DataNote dataNote = new DataNote();
                                        dataNote.setKey(Key);
                                        dataNote.setComment(null);
                                        dataNote.setText(null);
                                        dataNote.setDate(null);
                                        dataNote.setImgurl(null);
                                        dataNote.setCategory(null);
                                        dataNote.setDday(null);
                                        if (img.equals("")) {
                                            dataNote.setImg(null);
                                        } else {
                                            dataNote.setImg(getResizedBitmap(loadBitmap(img), 200,200));
                                        }
                                        //data.add(dataNote);

                                        //mListadapter = new ListAdapter(data);
                                        //mRecyclerView.setAdapter(mListadapter);


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mListadapter.notifyDataSetChanged();
                                                /// 어댑터 업데이트

                                            }
                                        });
                                    }
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                try {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cnt", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    Log.i("datasize1", "" + data.size());
                                    if (data.size() == 0) {
                                        editor.putInt("expi", 0);
                                        editor.commit();
                                    } else {
                                        expirItemCount = data.size();
                                        editor.putInt("expi", expirItemCount);
                                        editor.commit();

                                    }
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

                    private void runOnUiThread(Runnable runnable) {
                    }


                }.start();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new Thread(){
            @Override
            public  void run(){

                getDateRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("Frag3",""+dataSnapshot.getKey());
                        Log.d("Frag3",""+dataSnapshot.child("name").getValue());

                        try{
                            String Key = dataSnapshot.getKey().toString(); // 파이어베이스상에서 키값 저장
                            String Category = dataSnapshot.child("Category").getValue().toString();
                            name = dataSnapshot.child("name").getValue().toString();
                            date = dataSnapshot.child("date").getValue().toString();
                            img = dataSnapshot.child("Image").getValue().toString();
                            Calendar calendar1 = Calendar.getInstance();
                            intent.getStringExtra("year");
                            String[] text = date.split("-");
                            String year = text[0];
                            String month = text[1];
                            String day = text[2];

                            int y = Integer.parseInt(year);
                            int m = Integer.parseInt(month);
                            int d = Integer.parseInt(day);
                            calendar1.set(Calendar.YEAR,y); //d-day 날짜를 입력
                            calendar1.set(Calendar.MONTH,m);
                            calendar1.set(Calendar.DAY_OF_MONTH,d);
                            if(d==31){
                                calendar1.add(Calendar.DATE,-1);
                            }
                            //Log.d(" 월 ",""+calendar1.get(Calendar.DAY_OF_MONTH));
                            //Log.d("dday날짜날짜",""+calendar1.get(Calendar.YEAR)+(calendar1.get(Calendar.DAY_OF_MONTH)+1)+calendar1.get(Calendar.DAY_OF_MONTH));
                            tt=calendar.getTimeInMillis()/86400000;
                            dd=calendar1.getTimeInMillis()/86400000;
                            rr=(dd-tt);
                            resultNumber = (int)rr;


                            getDateRef.child(dataSnapshot.getKey()).child("d-day").setValue(resultNumber);
                            //dataNote.setDday(null);
                            if(resultNumber<0) {
                                DataNote dataNote = new DataNote();
                                dataNote.setKey(Key);
                                dataNote.setComment(name);
                                dataNote.setText("");
                                dataNote.setDate(date);
                                dataNote.setImgurl(img);
                                dataNote.setCategory(Category);
                                dataNote.setDday(d_day(resultNumber));


                                if (img.equals("")) {
                                    dataNote.setImg(null);
                                } else {
                                    dataNote.setImg(getResizedBitmap(loadBitmap(img), 200,200));

                                }
                                data.add(dataNote);
                                Frag3.CompareDateAsc compareDateAsc = new Frag3.CompareDateAsc();
                                Collections.sort(data, compareDateAsc);
                                mListadapter = new Frag3.ListAdapter(data);
                                mRecyclerView.setAdapter(mListadapter);


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mListadapter.notifyDataSetChanged();
                                        /// 어댑터 업데이트

                                    }
                                });
                            }else{
                                DataNote dataNote = new DataNote();
                                dataNote.setKey(Key);
                                dataNote.setComment(null);
                                dataNote.setText(null);
                                dataNote.setDate(null);
                                dataNote.setImgurl(null);
                                dataNote.setCategory(null);
                                dataNote.setDday(null);
                                if (img.equals("")) {
                                    dataNote.setImg(null);
                                } else {
                                    dataNote.setImg(getResizedBitmap(loadBitmap(img), 200,200));
                                }
                                //data.add(dataNote);

                                //mListadapter = new ListAdapter(data);
                                //mRecyclerView.setAdapter(mListadapter);


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mListadapter.notifyDataSetChanged();
                                        /// 어댑터 업데이트

                                    }
                                });
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        try{
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("cnt", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                        Log.i("datasize1", ""+ data.size());
                        if(data.size() == 0) {
                            editor.putInt("expi", 0);
                            editor.commit();
                        }else {
                            expirItemCount = data.size();
                            editor.putInt("expi", expirItemCount);
                            editor.commit();

                        }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

            private void runOnUiThread(Runnable runnable) {
            }


        }.start();


        return view;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }
    class CompareDateAsc implements Comparator<DataNote> { //유통기한 오름차순
        @Override
        public int compare(DataNote o1, DataNote o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
    // 이름 오름차순
    class CompareNameAsc implements Comparator<DataNote> {

        @Override
        public int compare(DataNote o1, DataNote o2) {
            return o1.getComment().compareTo(o2.getComment());
        }
    }

    public class ListAdapter extends RecyclerView.Adapter<Frag3.ListAdapter.ViewHolder>
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
            TextView textViewdday;
            ImageView imageView;
            public ViewHolder(View itemView)
            {
                super(itemView);
                this.textViewText = (TextView) itemView.findViewById(R.id.text);
                this.textViewComment = (TextView) itemView.findViewById(R.id.comment);
                this.textViewDate = (TextView) itemView.findViewById(R.id.date);
                this.imageView = (ImageView) itemView.findViewById(R.id.ListImage);
                this.textCategory = (TextView) itemView.findViewById(R.id.Category01);
                this.textViewdday = (TextView) itemView.findViewById(R.id.dday);
            }
        }

        @Override
        public Frag3.ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);

            Frag3.ListAdapter.ViewHolder viewHolder = new Frag3.ListAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(Frag3.ListAdapter.ViewHolder holder, final int position)
        {
            SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(getActivity());
            if(dataList.get(position).getImg()==null){
                holder.imageView.setImageResource(R.drawable.ic_none_image);
            }else {
                holder.imageView.setImageBitmap(dataList.get(position).getImg());
            }
            holder.textViewText.setText(dataList.get(position).getText());
            holder.textViewComment.setText(dataList.get(position).getComment());
            holder.textViewDate.setText(dataList.get(position).getDate());
            holder.textCategory.setText(dataList.get(position).getCategory());
            if(Integer.parseInt(dataList.get(position).getDday())<=0&&Integer.parseInt(dataList.get(position).getDday())>=-sharedprefereneceUtil.getSharedPreference("Number",3)) {
                holder.textViewdday.setText("D"+dataList.get(position).getDday());
                holder.textViewdday.setTextColor(Color.RED);
            }else if(Integer.parseInt(dataList.get(position).getDday())>0)
            {
                holder.textViewdday.setText("D"+dataList.get(position).getDday());
                holder.textViewdday.setTextColor(Color.BLUE);
            }else
            {
                holder.textViewdday.setText("D"+dataList.get(position).getDday());
                holder.textViewdday.setTextColor(Color.BLACK);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Toast.makeText(getActivity(), "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),DeleteActivity.class);
                    intent.putExtra("Name",dataList.get(position).getComment());
                    intent.putExtra("Date",dataList.get(position).getDate());
                    intent.putExtra("IMG",dataList.get(position).getImgurl());
                    intent.putExtra("Key",dataList.get(position).getKey());
                    intent.putExtra("Category",dataList.get(position).getCategory());
                    startActivity(intent);


                }
            });
        }

        @Override
        public int getItemCount()
        {
            return dataList.size();
        }
    }
    //비트맵 리사이즈
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

    // 비트매핑
    public Bitmap loadBitmap(String url){
        URL newurl =null;
        Bitmap bitmap = null;
        HttpURLConnection connection =null;
        try{
            newurl = new URL(url);
            connection = (HttpURLConnection)newurl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }
    //d-day 변환
    public String d_day(int resultNumber){
        if(resultNumber>0){
            return String.format("-%d",resultNumber);
        }else if(resultNumber<0){
            int absR=Math.abs(resultNumber);
            return String.format("+%d",absR);
        }else{
            return String.format("-%d",resultNumber);
        }
    }
}
