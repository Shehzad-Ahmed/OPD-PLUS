package com.thecreators.android.opdplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Shehzad Ahmed on 12/28/2016.
 */

public class PrescriptionImageTask extends AsyncTask<String,Void,Bitmap> {

    private String AppointmentID;
    private Context mContext;

    PrescriptionImageTask(String appointmentID,Context context)
    {
        AppointmentID=appointmentID;
        mContext=context;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        URL url = createUrl(mContext.getString(R.string.URL_PriscriptionImage));
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        Bitmap myBitmap=null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String post_data = URLEncoder.encode("AppointmentID","UTF-8")+"="+URLEncoder.encode(AppointmentID,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            // urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return myBitmap;
    }
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {

            return null;
        }
        return url;
    }
}
