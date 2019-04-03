package com.example.user.jsouptest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

/**
 * Created by user on 2018-05-14.
 */

public class RegisterActivity extends RuntimePermission {
    private SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(this);
    private DatabaseReference databaseReference;
    private EditText userName;
    String number;
    private static final int REQUEST_PERMISSION = 10;   // 런타임 퍼미션
    private ValueEventListener checkUserName = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

            while (child.hasNext()) {
                if (child.next().getKey().equals(userName.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "존재하는 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                    databaseReference.removeEventListener(this);
                    return;
                }
            }
            createUser();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestAppPermissions(new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.CALL_PHONE,
                        android.Manifest.permission.READ_SMS
                },
                R.string.message, REQUEST_PERMISSION);
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        number = telephonyManager.getLine1Number();
        databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user");
        userName = (EditText)findViewById(R.id.email);
        Intent intent = new Intent(RegisterActivity.this,SettingActivity.class);
        intent.putExtra("number",number);

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
                ad.setTitle("경고");
                ad.setMessage("기존에 보유한(그룹포함) 데이터는 완전히 삭제됩니다.");
                ad.setPositiveButton("계속", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                // Event
                    }
                });
                // 창 띄우기
                ad.show();
            }
        });

        final Button check = (Button)findViewById(R.id.submit);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PhoneNumber",number);
                if(databaseReference.child((sharedprefereneceUtil.getSharedPreference("userName",""))).child("number").equals(number)){
                    databaseReference.child(sharedprefereneceUtil.getSharedPreference("userName","")).removeValue();
                    if(userName.getText().toString().length() > 1){
                        databaseReference.addListenerForSingleValueEvent(checkUserName);
                    }else{
                        Toast.makeText(getApplicationContext(), "2글자 이상 입력해주세요.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(userName.getText().toString().length() > 1){
                        databaseReference.addListenerForSingleValueEvent(checkUserName);
                    }else{
                        Toast.makeText(getApplicationContext(), "2글자 이상 입력해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requsetCode) {
        //
    }

    public void createUser(){
        databaseReference.child((userName.getText().toString())).child("number").setValue(number);
        Toast.makeText(getApplicationContext(),"닉네임이 설정되었습니다.",Toast.LENGTH_SHORT).show();
        sharedprefereneceUtil.putSharedPreference("userName",userName.getText().toString());
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);

    }

}
