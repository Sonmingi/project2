package com.example.user.jsouptest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by user on 2018-04-10.
 */

public class DeleteActivity extends AppCompatActivity{
    TextView nameText,dateText,category;
    ImageView imageView;
    Button delete,used;
    Intent intent;
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(this);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final Calendar calendar = Calendar.getInstance();
        String user = sharedprefereneceUtil.getSharedPreference("userName","");
        if(user.equals("")){
            user = "aabbcc";
        }
        final DatabaseReference getDateRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user").child(user);
        final DatabaseReference getDataRef = getDateRef.child("Data");
        DatabaseReference DeleteRef = getDateRef.child("Delete");
        DatabaseReference UsedRef = getDateRef.child("Used");
        final DatabaseReference mDelete = DeleteRef.push();
        final DatabaseReference mUsed =  UsedRef.push();

        setContentView(R.layout.item_delete);
        nameText = (TextView)findViewById(R.id.nameText);
        dateText = (TextView)findViewById(R.id.dateText);
        imageView = (ImageView)findViewById(R.id.imageView2);
        delete = (Button)findViewById(R.id.deleteButton);
        used = (Button)findViewById(R.id.usedButton);
        category = (TextView)findViewById(R.id.Category02);


        intent = getIntent();

        nameText.setText(intent.getStringExtra("Name"));
        dateText.setText(intent.getStringExtra("Date"));
        imageView.setImageBitmap(loadBitmap(intent.getStringExtra("IMG")));
        category.setText(intent.getStringExtra("Category"));
        Log.d("getKey:",intent.getStringExtra("Key"));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDelete.child("name").setValue(intent.getStringExtra("Name"));
                mDelete.child("Image").setValue(intent.getStringExtra("IMG"));
                mDelete.child("date").setValue(intent.getStringExtra("Date"));
                mDelete.child("Category").setValue(intent.getStringExtra("Category"));
                mDelete.child("DeleteMonth").setValue(calendar.get(Calendar.MONTH)+1);
                getDataRef.child(intent.getStringExtra("Key")).removeValue();
                Toast.makeText(DeleteActivity.this,"삭제했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(DeleteActivity.this,MainActivity.class);
                startActivity(intent1);

            }
        });

        used.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsed.child("name").setValue(intent.getStringExtra("Name"));
                mUsed.child("Image").setValue(intent.getStringExtra("IMG"));
                mUsed.child("date").setValue(intent.getStringExtra("Date"));
                mUsed.child("Category").setValue(intent.getStringExtra("Category"));
                mUsed.child("DeleteMonth").setValue(calendar.get(Calendar.MONTH)+1);
                getDataRef.child(intent.getStringExtra("Key")).removeValue();
                Toast.makeText(DeleteActivity.this,"사용했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(DeleteActivity.this,MainActivity.class);
                startActivity(intent1);
            }
        });
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
}
