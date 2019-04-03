package com.example.user.jsouptest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Date;

/**
 * Created by user on 2018-06-01.
 */

public class BroadcastActivity extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(context);
        //알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오기
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_LOW;

        // Notification Channel Create.
        // 채널 ID
        String id = "notification_channel";
        // 채널 이름
        CharSequence channelName = "notiChannel";

        if (Build.VERSION.SDK_INT >= 26) {
            // Sdk 26버전부터 실행할 코드
            // Sdk 26버전 = Oreo. Oreo에 Notification 관련 issue. Notification을 발생할 때, Channel을 만들지 않으면
            // 해당 Notification이 발생하지 않음.
            // Channel을 만드는 Method는 Sdk version 26 이상만 사용이 가능함.
            // minSdk가 22이기 떄문에, sdk26 이상의 OS를 가진 기기만 해당 코드를 수행하도록 함수 구현.

            NotificationChannel notiChannel = new NotificationChannel(id, channelName, importance);

            //채널 설정
            notificationmanager.createNotificationChannel(notiChannel);
        }


        // pendingIntnet2 : 삭제하러가기를PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ListTest.class), PendingIntent.FLAG_UPDATE_CURRENT); 눌렀을 때 해당 리스트가 나온 액티비티로 이동하기 위하여 새로 설정.
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        // 푸쉬 발생 시 헤드업 알림을 띄워 줌.
        builder.setPriority(Notification.PRIORITY_MAX);

        String sendText = "유통기한이"+ sharedprefereneceUtil.getSharedPreference("Number",3) +" 일 이내로 남은 물품이 "+sharedprefereneceUtil.getSharedPreference("Expired",0)+" 개 입니다.";
        int noti = SharedprefereneceUtil.getDataInt(context, "SaveNoti");




        /////////////////////////////////////////////
        // 5.23 추가분. 이 부분은 지워도 문제 없음.
        // 알람 시간과 현재 시간을 비교하여 다음날일 경우 noti의 값을 바꾼다.
        long now = System.currentTimeMillis();
        // 푸쉬 시간
        Date date1 = new Date(now);

        // 현재 시간
        Date date2 = new Date(now);
        String hour = SharedprefereneceUtil.getData(context, "SharedHour");
        String min = SharedprefereneceUtil.getData(context, "SharedMin");


        int hours;
        int mins;
        if(hour == null){
            // hour가 null일 경우, 시간 설정이 안되어있다는 것. 저장된 값이 있어도 shared가 null이면
            // 알람이 off라는 것.
            SharedprefereneceUtil.putDataInt(context, "SaveNoti", 1);
            Log.d("broadLog", "checked Notification value : " + noti);

        }else{
            hours = Integer.parseInt(hour);;
            mins = Integer.parseInt(min);

            date1.setHours(hours);
            date1.setMinutes(mins);

            // broadcast는 딱 시간이 되면 그때 발생.
            // 한번 발생하고, 다음날에는 초기화가 되어야 함.

            if(date1.equals(date2)){
                SharedprefereneceUtil.putDataInt(context, "SaveNoti", 100);
                Log.d("broadLog", "checked Notification value : if " + noti);
            }else{
                SharedprefereneceUtil.putDataInt(context, "SaveNoti", 1);
                Log.d("broadLog", "checked Notification value : else " + noti);
            }
        }
        /////////////////////////////////////////////



        // default value 을 100으로 설정해둠. 100일 경우는 Noti가 한번도 발생하지 않은 경우이므로 이 때 notification 발생
        if(noti == 100){
            if (Build.VERSION.SDK_INT >= 26) {
                // Sdk 26버전부터 실행할 코드
                // setChannelId 이 sdk version 26 이상만 사용가능한 코드. 따라서, 26이상의 경우만 수행하도록 따로 구현
                builder.setSmallIcon(R.drawable.ic_done_black_24dp).setTicker("expiration").setWhen(System.currentTimeMillis())
                        .setNumber(5).setContentTitle("유통기한 알리미").setContentText(sendText).setChannelId(id)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent2).setAutoCancel(true);
                SharedprefereneceUtil.putDataInt(context, "SaveNoti", 1);
                Log.d("broadLog", "Check sendText : " + sendText);
            }
            else{
                //  SDK version이 25 이하의 경우 해당 코드 수행

                builder.setSmallIcon(R.drawable.ic_done_black_24dp).setTicker("expiration").setWhen(System.currentTimeMillis())
                        .setNumber(5).setContentTitle("유통기한 알리미").setContentText(sendText)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent2).setAutoCancel(true);
                SharedprefereneceUtil.putDataInt(context, "SaveNoti", 1);
                Log.d("broadLog", "Check sendText : " + sendText);
            }

            // PendingIntent2를 사용하여 삭제하기를 할 수 있는 액티비티로 바로 이동.
            builder.addAction(android.R.drawable.star_off, "항목 확인하러 가기", pendingIntent2);
            Log.d("broadLog", "check endLog ");
            // builder.addAction(android.R.drawable.star_on, "항목 확인하기", pendingIntent);
            // 하단부에 뜨는 리스트는 1개가 아니라 여러 개를 추가할 수 있다.
            // 항목 삭제하러 가기


            notificationmanager.notify(1, builder.build());
        }else{
            Log.d("broadLog", "No send Notification : " + noti);
        }
    }
}