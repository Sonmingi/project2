package com.example.user.jsouptest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 2018-04-12.
 */

public class loadBitmap {
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
