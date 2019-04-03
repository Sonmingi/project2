package com.example.user.jsouptest;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by user on 2018-03-23.
 */

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int REQUEST_INVITE;
    private DatabaseReference databaseReference;
    String number;
    int hours, mins;
    final SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(this);
    final String[] code = new String[1];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        TextView setting_name = (TextView) findViewById(R.id.setting_name);
        TextView setting_share = (TextView)findViewById(R.id.setting_share);
        final TextView setting_push_time = (TextView)findViewById(R.id.setting_push_time);
        TextView setting_push_on_off = (TextView)findViewById(R.id.setting_push_on_off);
        TextView participate = (TextView)findViewById(R.id.participate_);
        TextView leave = (TextView)findViewById(R.id.leave);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tv = (TextView)findViewById(R.id.settingtime);
        TextView expire = (TextView)findViewById(R.id.expire_day);
        Boolean btn = sharedprefereneceUtil.getDataBoolean(this, "SaveToggle");
        String hour = sharedprefereneceUtil.getData(this, "SharedHour");
        String min = sharedprefereneceUtil.getData(this, "SharedMin");

        String saveHour = sharedprefereneceUtil.getData(this, "SaveHour");
        String saveMin = sharedprefereneceUtil.getData(this, "SaveMin");
        setSupportActionBar(toolbar);

        /*** NavigationView ***/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_setting);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_setting);
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


        /**동적링크생성****/
        final String decodeStr;
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(sharedprefereneceUtil.getSharedPreference("userName",""))) //정보를 담은 링크
                .setDynamicLinkDomain("q7rpj.app.goo.gl") //동적링크 도메인
                //.setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder().setSource(sharedprefereneceUtil.getSharedPreference("userName","")).build())
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.example.user.jsouptest").build()) //패키지명
                // Open links with com.example.ios on iOS
                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        final Uri dynamicLinkUri = dynamicLink.getUri();
        Log.d("infomationlink : ",dynamicLinkUri.toString());
        Log.d("infomationString",""+dynamicLinkUri.toString().length());
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

        setting_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage("귀하를 \""+sharedprefereneceUtil.getSharedPreference("userName","")+"\" 에 초대합니다.\n")
                        .setDeepLink(dynamicLinkUri)
                        .setCallToActionText(getString(R.string.text))
                        .build();
                startActivityForResult(intent,REQUEST_INVITE);
            }
        });
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        Uri deepLink = null;

                        if (data != null) {
                            Log.d("INVITE_GET", "getInvitation: data!");
                            // Get the deep link
                            deepLink = data.getLink();

                            Toast.makeText(SettingActivity.this, "참가를 눌러주세요" + deepLink.toString(), Toast.LENGTH_SHORT).show();

                            code[0] = data.getLink().toString().substring(61,data.getLink().toString().length());

                            return;
                        }
                        // Handle the deep link

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("INVITE_GET", "getDynamicLink:onFailure", e);
                        Toast.makeText(SettingActivity.this, "링크를 받지 못하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });


        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    android.support.v7.app.AlertDialog.Builder ad = new android.support.v7.app.AlertDialog.Builder(SettingActivity.this);
                    final EditText editText = new EditText(SettingActivity.this);
                    editText.setFocusable(false);
                    editText.setClickable(false);
                    //디코딩

                    try {
                        String decodeStr = URLDecoder.decode(code[0],"UTF-8");
                        editText.setText(decodeStr);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    ad.setView(editText);
                    ad.setTitle("참가");
                    ad.setMessage("참가하시겠습니까? \n 그룹에 참여시 기존에 보유한 데이터는 전부 삭제됩니다.");

                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String decodeStr = URLDecoder.decode(code[0],"UTF-8");

                                if(sharedprefereneceUtil.getSharedPreference("userName","").equals(decodeStr)) {
                                    Toast.makeText(SettingActivity.this,"이미 가입된 그룹입니다.",Toast.LENGTH_SHORT).show();
                                }else{
                                    databaseReference.child(sharedprefereneceUtil.getSharedPreference("userName", "")).removeValue();
                                    databaseReference.addListenerForSingleValueEvent(checkUserName);
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();     //닫기
                            // Event
                        }
                    });
                    ad.show();
                }catch (NullPointerException e){
                    Toast.makeText(SettingActivity.this, "초대를 받지 않았습니다.", Toast.LENGTH_SHORT).show();

                }

            }

        });

        setting_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        final TextView settingday =(TextView)findViewById(R.id.settingday);
        setting_push_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] value = new int[1];
                AlertDialog.Builder d = new AlertDialog.Builder(SettingActivity.this);
                final NumberPicker numberPicker = new NumberPicker(SettingActivity.this);
                d.setTitle("기간 설정");
                d.setView(numberPicker);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(30);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        value[0] = newVal;
                    }
                });
                d.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedprefereneceUtil.putSharedPreference("Number",value[0]);
                        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                d.show();
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, RegisterActivity.class);
                Toast.makeText(SettingActivity.this,"탈퇴를 하시려면 계정을 생성해주세요.",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });



        final ToggleButton alarmToggle = (ToggleButton)findViewById(R.id.setting_push_on_off);
        alarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarmToggle.isChecked()){
                    String hour = sharedprefereneceUtil.getData(getApplicationContext(), "SaveHour");
                    String min = sharedprefereneceUtil.getData(getApplicationContext(), "SaveMin");

                    sharedprefereneceUtil.putData(getApplicationContext(), "SharedHour", hour);
                    sharedprefereneceUtil.putData(getApplicationContext(), "SharedMin", min);

                    if(hour == null){
                        Toast.makeText(getApplicationContext(),"알람 시간을 설정해주세요.",Toast.LENGTH_SHORT).show();
                        sharedprefereneceUtil.putDataBoolean(getApplicationContext(), "SaveToggle", false);
                        alarmToggle.setChecked(false);
                    }else{

                        // 100으로 설정 함으로써 알람을 발송가능한 상태로 만듦 // BroadcastActivity 참고.
                        sharedprefereneceUtil.putDataInt(getApplicationContext(), "SaveNoti", 100);

                        hours = Integer.parseInt(hour);;
                        mins = Integer.parseInt(min);

                        sharedprefereneceUtil.putDataBoolean(getApplicationContext(), "SaveToggle", true);

                        Toast.makeText(getApplicationContext(),"Alarm On : " + hours + " 시 " + mins + " 분 ",Toast.LENGTH_SHORT).show();

                        new AlarmHATT(getApplicationContext()).Alarm();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Alarm Off",Toast.LENGTH_SHORT).show();

                    sharedprefereneceUtil.putDataBoolean(getApplicationContext(), "SaveToggle", false);

                    sharedprefereneceUtil.putData(getApplicationContext(), "SharedHour", null);
                    sharedprefereneceUtil.putData(getApplicationContext(), "SharedMin", null);


                    // 1으로 설정 함으로써 알람을 발송하지 못함. // BroadcastActivity 참고.
                    sharedprefereneceUtil.putDataInt(getApplicationContext(), "SaveNoti", 1);
                }

            }
        });

        if(hour == null){
            if(saveHour == null){
                tv.setText("시간이 설정되지 않았습니다.");
            }else {
                tv.setText("설정된 시간 : ");
                tv.setText(tv.getText() + saveHour + "시" + saveMin + "분\n");


                SharedprefereneceUtil.putData(getApplicationContext(), "SharedHour", saveHour);
                SharedprefereneceUtil.putData(getApplicationContext(), "SharedMin", saveMin);
            }
        }else{
            tv.setText("설정된 시간 : ");
            tv.setText(tv.getText() + hour + "시" + min + "분\n");
        }

        TableRow tableRow = (TableRow)findViewById(R.id.tablerow);
        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] value = new int[1];
                AlertDialog.Builder d = new AlertDialog.Builder(SettingActivity.this);
                final NumberPicker numberPicker = new NumberPicker(SettingActivity.this);
                d.setTitle("기간 설정");
                d.setView(numberPicker);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(30);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        value[0] = newVal;
                    }
                });
                d.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedprefereneceUtil.putSharedPreference("Number",value[0]);
                        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                d.show();

            }
        });
        TableRow tableRow2 = (TableRow)findViewById(R.id.tablerow2);
        tableRow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeSetting newFragment = new TimeSetting();

                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        TextView settingtime = (TextView)findViewById(R.id.setting_push);
        settingtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeSetting newFragment = new TimeSetting();

                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        expire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] value = new int[1];
                AlertDialog.Builder d = new AlertDialog.Builder(SettingActivity.this);
                final NumberPicker numberPicker = new NumberPicker(SettingActivity.this);
                d.setTitle("기간 설정");
                d.setView(numberPicker);
                numberPicker.setMinValue(7);
                numberPicker.setMaxValue(30);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        value[0] = newVal;
                    }
                });
                d.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedprefereneceUtil.putSharedPreference("Number_Expire",value[0]);
                        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                d.show();
            }
        });
        TableRow tableRow3 = (TableRow)findViewById(R.id.tablerow3);
        tableRow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] value = new int[1];
                AlertDialog.Builder d = new AlertDialog.Builder(SettingActivity.this);
                final NumberPicker numberPicker = new NumberPicker(SettingActivity.this);
                d.setTitle("기간 설정");
                d.setView(numberPicker);
                numberPicker.setMinValue(7);
                numberPicker.setMaxValue(30);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        value[0] = newVal;
                    }
                });
                d.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedprefereneceUtil.putSharedPreference("Number_Expire",value[0]);
                        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                d.show();
            }
        });

        if(btn){
            alarmToggle.setChecked(true);
        }else{
            alarmToggle.setChecked(false);
        }

        if(hour == null){
            hours = 99;;
            mins = 99;
        }else{
            hours = Integer.parseInt(hour);;
            mins = Integer.parseInt(min);
        }
        if (sharedprefereneceUtil.getSharedPreference("Number",0)==0){
            settingday.setText("기간이 설정되지 않았습니다.\n (기본3일)");
        }else{
            settingday.setText(sharedprefereneceUtil.getSharedPreference("Number", 3) + "일");
        }

        if(sharedprefereneceUtil.getSharedPreference("Number_Expire",0)==0) {
            expire.setText("기간 설정되지 않았습니다.\n (기본7일)");
        }else{
            expire.setText(sharedprefereneceUtil.getSharedPreference("Number_Expire", 7) + "일");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("INVITE_INVITE", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("INVITE_INVITE", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }

    }
    private ValueEventListener checkUserName = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
/*
            while (child.hasNext()) {
                if (child.next().getKey().equals(userName.getText().toString())) {
                    //Toast.makeText(getApplicationContext(), "존재하는 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                    //databaseReference.removeEventListener(this);
                    return;
                }
            }*/
            createUser();
        }
        public void createUser(){
            try {
                String decodeStr = URLDecoder.decode(code[0],"UTF-8");
                databaseReference.child(decodeStr).push().child("number").setValue(number);
                Toast.makeText(getApplicationContext(),"초대에 참가함.",Toast.LENGTH_SHORT).show();
                sharedprefereneceUtil.putSharedPreference("userName",decodeStr);
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //////////////////////////////////
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            Intent intent = new Intent(SettingActivity.this, CategorySettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_delete) {
            Intent intent = new Intent(this, DeleteHistoryActivity.class);
            startActivity(intent);

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_setting);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        public void Alarm() {

            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(SettingActivity.this, BroadcastActivity.class);

            PendingIntent sender = PendingIntent.getBroadcast(SettingActivity.this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기

            // 년,월,일,시,분,초
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hours, mins, 0);

            // 3,1일 남은 제품 Push는 매일 자정에 푸쉬.
            // 유통기한이 당일 인 제품은 마감시간 1~2시간 전에 푸쉬.
            // 유통기한이 지난 제품은 자정에 푸쉬할 때 같이 한번 더 푸쉬할 수 있도록.
            // 사용했는지 여부를 물어보고 그것에 따라 버림 표시 할 수 있게.

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(SettingActivity.this, "알람 시간이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        SharedprefereneceUtil.putDataInt(getApplicationContext(), "SaveNoti", 100);
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

}