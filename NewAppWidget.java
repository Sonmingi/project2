package com.example.user.jsouptest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {


/*

    // 커스텀 액션
    public static String PENDING_ACTION = "com.example.testsetonclickpendingintent.Pending_Action";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // RemoteViews 인스턴트 생성
        RemoteViews rv = new RemoteViews(context.getPackageName(),  R.layout.new_app_widget);

        // 수신한 인텐트로부터 액션값을 읽음
        String action = intent.getAction();

        // AppWidget의 기본 Action 들
        if (action.equals(PENDING_ACTION)) {
            rv.setTextViewText(R.id.approching, action);
            rv.setTextViewText(R.id.approchingCount,
                    String.valueOf(intent.getIntExtra("viewId", 0)));
        }

        // rv.setOnClickPendingIntent(R.id.refbtn, getPendingIntent(context, R.id.refbtn));


        // 위젯 화면 갱신
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName cpName = new ComponentName(context, NewAppWidget.class);
        appWidgetManager.updateAppWidget(cpName, rv);
    }

    // 호출한 객체에 PendingIntent를 부여
    private PendingIntent getPendingIntent(Context context, int id) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(PENDING_ACTION);
        intent.putExtra("viewId", id);

        // 중요!!! getBroadcast를 이용할 때 동일한 Action명을 이용할 경우 서로 다른 request ID를 이용해야함
        // 아래와 같이 동일한 request ID를 주면 서로 다른 값을 putExtra()하더라도 제일 처음 값만 반환됨
        // return PendingIntent.getBroadcast(context, 0, intent, 0);
        return PendingIntent.getBroadcast(context, id, intent, 0);
    }
*/


    /////////////////////////////////////////////////////////////////////
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, "확인하러가기");

        SharedPreferences sharedPreferences = context.getSharedPreferences("cnt", Context.MODE_PRIVATE);
        String a = Integer.toString(sharedPreferences.getInt("appr", 0));
        views.setTextViewText(R.id.approchingCount, a);
        String b = Integer.toString(sharedPreferences.getInt("expi",0));
        views.setTextViewText(R.id.expirationCount, b);

        // 버튼 클릭시 MainActivity 로 이동
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of themsuper.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}
