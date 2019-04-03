package com.example.user.jsouptest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 2018-05-31.
 */

public class CheckBoxDeleteActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private ListAdapter mListadapter;
    private Adapter adapter;
    ArrayList data = new ArrayList<DataNote>();
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(this);

    private int tYear;
    private int tMonth;
    private int tDay;

    private int dYear = 1;
    private int dMonth = 1;
    private int dDay = 1;

    private long dd;
    private long tt;
    private long rr;

    private int resultNumber = 0;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkboxdelete);
        String user = sharedprefereneceUtil.getSharedPreference("userName",null);
        if(user.equals("")){
            user = "aabbcc";
        }
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user").child(user);

        final DatabaseReference dataRef = mRef.child("Data");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView5);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        final loadBitmap loadBitmap = new loadBitmap();
        final Calendar calendar = Calendar.getInstance();
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH) + 1;
        tDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH,1);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);


        new Thread() {
            @Override
            public void run() {
                dataRef.addChildEventListener(new ChildEventListener() {
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
                            String img = dataSnapshot.child("Image").getValue().toString();
                            String Category = dataSnapshot.child("Category").getValue().toString();
                            Calendar calendar1 = Calendar.getInstance();
                            Log.d("저장된 날짜",date);
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
                            tt=(calendar.getTimeInMillis())/86400000;
                            dd=calendar1.getTimeInMillis()/86400000;
                            rr=(dd-tt);
                            resultNumber = (int)rr;


                            DataNote dataNote = new DataNote();
                            dataNote.setKey(Key);
                            dataNote.setComment(name);
                            dataNote.setText("");
                            dataNote.setDate(date);
                            dataNote.setImgurl(img);
                            dataNote.setCategory(Category);
                            dataNote.setSelected(false);

                            dataNote.setDday(d_day(resultNumber));
                            if (img.equals("")) {
                                dataNote.setImg(null);
                            } else {
                                dataNote.setImg(getResizedBitmap(loadBitmap.loadBitmap(img), 200,200));
                            }
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

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
    {
        boolean[] isCheckedConfrim;
        private ArrayList<DataNote> dataList;
        final CheckBox checkAll = (CheckBox)findViewById(R.id.checkall);

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
            ImageView imageView;
            TextView state;
            TextView textViewdday;
            CheckBox checkBox;

            public ViewHolder(View itemView)
            {
                super(itemView);
                this.textViewText = (TextView) itemView.findViewById(R.id.text_edit);
                this.textViewComment = (TextView) itemView.findViewById(R.id.comment_edit);
                this.textViewDate = (TextView) itemView.findViewById(R.id.date_edit);
                this.imageView = (ImageView) itemView.findViewById(R.id.ListImage_edit);
                this.textCategory = (TextView) itemView.findViewById(R.id.Category01_edit);
                this.state = (TextView)itemView.findViewById(R.id.state_edit);
                this.checkBox = (CheckBox)itemView.findViewById(R.id.Check_edit);
                this.textViewdday = (TextView)itemView.findViewById(R.id.dday_edit);
            }
        }


        @Override
        public CheckBoxDeleteActivity.ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_edit, parent, false);

            ListAdapter.ViewHolder viewHolder = new CheckBoxDeleteActivity.ListAdapter.ViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position)
        {


            final SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(CheckBoxDeleteActivity.this);
            FloatingActionButton delButton = (FloatingActionButton) findViewById(R.id.listDel);
            FloatingActionButton useButton = (FloatingActionButton)findViewById(R.id.listUse);
            if(dataList.get(position).getImg()==null){
                holder.imageView.setImageResource(R.drawable.ic_none_image);
            }else {
                holder.imageView.setImageBitmap(dataList.get(position).getImg());
            }

            holder.textViewText.setText(dataList.get(position).getText());
            holder.textViewComment.setText(dataList.get(position).getComment());
            if(Integer.parseInt(dataList.get(position).getDday())<=0&&Integer.parseInt(dataList.get(position).getDday())>-sharedprefereneceUtil.getSharedPreference("Number",3)) {
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
            holder.textViewDate.setText(dataList.get(position).getDate());
            holder.textCategory.setText(dataList.get(position).getCategory());
            holder.state.setText(dataList.get(position).getState());
            if (checkAll.isChecked()) {
                holder.checkBox.setSelected(dataList.get(position).isSelected());
            }else{
                holder.checkBox.setSelected(!dataList.get(position).isSelected());
            }
            holder.checkBox.setChecked(dataList.get(position).isSelected());
            checkAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkAll.isChecked()){
                        for(int i = 0 ; i < dataList.size();i++) {
                            dataList.get(i).setSelected(true);
                            //holder.checkBox.setSelected(true);
                        }
                    }else{
                        for(int i = 0; i < dataList.size(); i++){
                            dataList.get(i).setSelected(false);
                            //holder.checkBox.setSelected(false);
                        }
                    }
                  mListadapter.notifyDataSetChanged();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(holder.checkBox.isChecked()){
                        holder.checkBox.setSelected(false);
                        //holder.checkBox.setChecked(false);
                        mListadapter.notifyDataSetChanged();
                        //Toast.makeText(CheckBoxDeleteActivity.this, "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        holder.checkBox.setSelected(true);
                        //holder.checkBox.setChecked(true);
                        mListadapter.notifyDataSetChanged();
                        //Toast.makeText(CheckBoxDeleteActivity.this, "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.checkBox.isSelected()){
                        holder.checkBox.setSelected(false);
                        holder.checkBox.setChecked(false);
                        mListadapter.notifyDataSetChanged();
                        //Toast.makeText(CheckBoxDeleteActivity.this, "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        holder.checkBox.setSelected(true);
                        holder.checkBox.setChecked(true);
                        mListadapter.notifyDataSetChanged();
                        // Toast.makeText(CheckBoxDeleteActivity.this, "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            final Calendar calendar = Calendar.getInstance();

            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {AlertDialog.Builder ad = new AlertDialog.Builder(CheckBoxDeleteActivity.this);
                    ad.setTitle("삭제");
                    ad.setMessage("삭제하시겠습니까?");
                    final AlertDialog.Builder builder = ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < dataList.size(); i++) {
                                if (dataList.get(i).isSelected()) {
                                    String user = sharedprefereneceUtil.getSharedPreference("userName", null);
                                    if (user.equals("")) {
                                        user = "aabbcc";
                                    }
                                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user").child(user);
                                    final DatabaseReference deleteRef = mRef.child("Delete");
                                    final DatabaseReference UsedRef = mRef.child("Used");
                                    final DatabaseReference dataRef = mRef.child("Data");
                                    final DatabaseReference mdelete = deleteRef.push();
                                    final DatabaseReference mUsed = UsedRef.push();
                                    final int finalI = i;
                                    dataRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            mdelete.child("name").setValue(dataList.get(finalI).getComment());
                                            mdelete.child("image").setValue(dataList.get(finalI).getImg());
                                            mdelete.child("Category").setValue(dataList.get(finalI).getCategory());
                                            mdelete.child("date").setValue(dataList.get(finalI).getDate());
                                            mdelete.child("d-day").setValue(dataList.get(finalI).getDday());
                                            mdelete.child("DeleteMonth").setValue(calendar.get(Calendar.MONTH)+1);
                                            dataRef.child(dataList.get(finalI).getKey()).removeValue();
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

                                    Toast.makeText(CheckBoxDeleteActivity.this, "삭제합니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CheckBoxDeleteActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(CheckBoxDeleteActivity.this, "전체삭제 체크를 해주세요", Toast.LENGTH_SHORT).show();
                                }
                            }
                            dialog.dismiss();     //닫기
                            // Event
                        }
                    });
                    ad.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); //닫기
                        }
                    });
                    // 창 띄우기
                    ad.show();

                }
            });

            useButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {AlertDialog.Builder ad = new AlertDialog.Builder(CheckBoxDeleteActivity.this);
                    ad.setTitle("사용");
                    ad.setMessage("사용하셨습니까?");
                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int i = 0; i<dataList.size();i++) {
                                if (dataList.get(i).isSelected()) {
                                    String user = sharedprefereneceUtil.getSharedPreference("userName", null);
                                    if (user.equals("")) {
                                        user = "aabbcc";
                                    }
                                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user").child(user);
                                    final DatabaseReference deleteRef = mRef.child("Delete");
                                    final DatabaseReference UsedRef = mRef.child("Used");
                                    final DatabaseReference dataRef = mRef.child("Data");
                                    final DatabaseReference mdelete = deleteRef.push();
                                    final DatabaseReference mUsed = UsedRef.push();
                                    final int finalI = i;
                                    dataRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            mUsed.child("name").setValue(dataList.get(finalI).getComment());
                                            mUsed.child("image").setValue(dataList.get(finalI).getImg());
                                            mUsed.child("Category").setValue(dataList.get(finalI).getCategory());
                                            mUsed.child("date").setValue(dataList.get(finalI).getDate());
                                            mUsed.child("d-day").setValue(dataList.get(finalI).getDday());
                                            mUsed.child("DeleteMonth").setValue(calendar.get(Calendar.MONTH)+1); // 한달초기화
                                            dataRef.child(dataList.get(finalI).getKey()).removeValue();
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

                                    Toast.makeText(CheckBoxDeleteActivity.this, "목록에서 삭제합니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CheckBoxDeleteActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(CheckBoxDeleteActivity.this, "사용한 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            dialog.dismiss();     //닫기
                            // Event
                        }
                    });
                    ad.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); //닫기
                        }
                    });
                    // 창 띄우기
                    ad.show();

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
    //d-day
    public String d_day(int resultNumber){
        if(resultNumber>=0){
            return String.format("-%d",resultNumber);
        }else{
            int absR=Math.abs(resultNumber);
            return String.format("+%d",absR);
        }
    }

}
