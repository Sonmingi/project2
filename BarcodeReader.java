
package com.example.user.jsouptest;

        import android.app.DatePickerDialog;
        import android.app.ListActivity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.BitmapRegionDecoder;
        import android.graphics.Matrix;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Handler;
        import android.support.annotation.RequiresApi;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.method.ScrollingMovementMethod;
        import android.util.Log;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ListAdapter;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;


        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import org.jsoup.Connection;
        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;
        import org.jsoup.nodes.Element;
        import org.jsoup.select.Elements;

        import java.io.BufferedInputStream;
        import java.io.IOException;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.net.URLConnection;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Collection;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Map;
        import java.util.Set;
        import java.util.Vector;

public class BarcodeReader extends AppCompatActivity {

    private String htmlPageUrl = "https://www.beepscan.com/barcode/";
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat = "";
    private ImageView imgView;
    private String ImageSource = "";
    private Spinner Category;
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(this);
    Intent intent,intent1;
    String barcode = "";
    String Dataurl = "";




    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        String user = sharedprefereneceUtil.getSharedPreference("userName","");
        if(user.equals("")){
            user = "aabbcc";
        }
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test01-eccc7.firebaseio.com/").child("user").child(user);
        final DatabaseReference mNameRef = mRootRef.child("Data");

        intent = getIntent();
        barcode = intent.getStringExtra("barcode");
        Dataurl = htmlPageUrl + barcode; //url + 바코드

        intent1 = new Intent(BarcodeReader.this, ListActivity.class);
        intent1.putExtra("Dataurl",Dataurl);
        textviewHtmlDocument = (TextView)findViewById(R.id.textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod());
        imgView = (ImageView)findViewById(R.id.imgView);
        final EditText editText = (EditText)findViewById(R.id.SellbyDate);
        final int[] num = {0};
        //spinner
        Category = (Spinner)findViewById(R.id.Cate);
        final List list = new ArrayList();
        //
        final SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        Collection<?> col =  pref.getAll().values();
        Iterator<?> it = col.iterator();

        while(it.hasNext())
        {
            String msg = (String)it.next();
            list.add(msg);
            Ascending ascending = new Ascending();
            Collections.sort(list, ascending); //배열 정렬
            Log.d("Result", msg);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Category.setAdapter(adapter);

        final int[] cNumbers = {sharedprefereneceUtil.getDataInt(this, "cNumber")};
        final Handler handler = new Handler();
        Button button = (Button)findViewById(R.id.button);

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
/*
        for(int i=0; i<sharedprefereneceUtil.getDataInt(getApplicationContext(),"CName");i++){
            if(sharedprefereneceUtil.getDataInt(getApplicationContext(),"count"+i)==0){
                sharedprefereneceUtil.putDataInt(getApplicationContext(),"count"+i,1);
            }
            sharedprefereneceUtil.putSharedPreference(pref.getString(""+i,""),sharedprefereneceUtil.getDataInt(getApplicationContext(),"count"+i));
        }
*/


        init();

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals(null)&&!editText.getText().toString().equals("")) {

                    DatabaseReference mDataRef = mNameRef.push();
                    try {
                        mDataRef.child("name").setValue(removeTag(htmlContentInStringFormat));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mDataRef.child("Image").setValue(ImageSource);
                    mDataRef.child("date").setValue(editText.getText().toString());
                    mDataRef.child("Category").setValue(Category.getSelectedItem().toString());
                    mDataRef.child("d-day").setValue("00");

/*
                      for(int i = 0;i<sharedprefereneceUtil.getDataInt(getApplicationContext(),"CName");i++) {
                          if () {
                              Log.d("mapkey",""+map.get(map.keySet().toArray()[i]));
                              map.put(pref.getString(""+i,""),sharedprefereneceUtil.getDataInt(getApplicationContext(),"count"+i)+1);
                              sharedprefereneceUtil.putDataInt(getApplicationContext(),"count",sharedprefereneceUtil.getDataInt(getApplicationContext(),"count"+i)+1);
                          }
                          mNameRef.child(Category.getSelectedItem().toString()).setValue(map.get(Category.getSelectedItem().toString()));

                      }
*/

                    Intent intent1 = new Intent(BarcodeReader.this, MainActivity.class);
                    try {
                        intent1.putExtra("name", removeTag(htmlContentInStringFormat));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent1.putExtra("imgURL", ImageSource);
                    intent1.putExtra("Date", editText.getText().toString());
                    intent1.putExtra("Category", Category.getSelectedItem().toString());
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent1);
                }else{
                    Toast.makeText(BarcodeReader.this,"유통기한을 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        String imgurl;  // 이미지파일의 주소를 저장
        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        public Bitmap doInBackground(Void... params) {
            try {
                Connection.Response response = Jsoup.connect(Dataurl).execute();
                Document doc = response.parse();
                Elements image = doc.select(".content").select(".card");
                Elements name = doc.select("div.container p");
                for (Element i : image) {
                    imgurl = i.select("img").attr("src");
                    Log.d("JATimg",imgurl);
                    ImageSource += imgurl;
                }
                for (Element t : name){
                    htmlContentInStringFormat += t;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return loadBitmap(imgurl);
        }
        @Override
        public void onPostExecute(Bitmap result) {
            try {
                textviewHtmlDocument.setText(removeTag(htmlContentInStringFormat));
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgView.setImageBitmap(getResizedBitmap(result,1000,1000));

        }
        public Bitmap loadBitmap(String url){
            URL newurl =null;
            Bitmap bitmap = null;
            try{
                newurl = new URL(url);
                bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return bitmap;
        }
    }
    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    void init(){
        //Calendar를 이용하여 년, 월, 일, 시간, 분을 PICKER에 넣어준다.
        final Calendar cal = Calendar.getInstance();
        //DATE PICKER DIALOG
        findViewById(R.id.SellbyDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(BarcodeReader.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        String msg = String.format("%d 년 %d 월 %d 일", year, month + 1, date);
                        Toast.makeText(BarcodeReader.this, msg, Toast.LENGTH_SHORT).show();
                        EditText editText = (EditText)findViewById(R.id.SellbyDate);

                        if((month+1)<10&&date<10) {
                            editText.setText("" + year + "-" + "0" + (month + 1) + "-"+"0"+ date);
                        }else if(date<10){
                            editText.setText("" + year + "-" + (month + 1) + "-" +"0"+ date);
                        }else if((month+1)<10){
                            editText.setText("" + year + "-" +"0" + (month + 1) + "-"+ date);
                        }else {
                            editText.setText("" + year + "-"+ (month + 1) + "-"+ date);
                        }

                        //editText.setText(""+ year + "-"+ (month + 1) + "-"+ date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                //dialog.getDatePicker().setMaxDate(new Date().getTime());    //입력한 날짜 이후로 클릭 안되게 옵션
                dialog.show();
            }
        });
    }
    class Ascending implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
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
}


