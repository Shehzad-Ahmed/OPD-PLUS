package com.thecreators.android.opdplus;

/**
 * Created by Shehzad Ahmed on 11/23/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Shehzad Ahmed on 11/23/2016.
 */

public class UserLoginTask extends AsyncTask<String, Void, String> {

    private final String mPhoneNumber;
    private final String mPassword;
    AlertDialog mAlertDialog;
    public String result = "Connection Time Out";

    Context mContext;

    UserLoginTask(Context context,String phoneNumber, String password) {
        mPhoneNumber = phoneNumber;
        mPassword = password;
        mContext=context;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO: attempt authentication against a network service.
        //String login_url = "http://www.thecreatorstc.com/OPDPLUS/form/andriod/Andriodlogin.php";
        String login_url = mContext.getString(R.string.URL_Login);
        try {

            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(500000000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String post_data = URLEncoder.encode("mPhoneNumber","UTF-8")+"="+URLEncoder.encode(mPhoneNumber,"UTF-8")+"&"
                    +URLEncoder.encode("mPassword","UTF-8")+"="+URLEncoder.encode(mPassword,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            result="";
            String line = "";
            while((line = bufferedReader.readLine())!=null)
            {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();


            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }






        PatientLoginActivity patientLoginActivity= (PatientLoginActivity)mContext;
        patientLoginActivity.showProgress(false);
        return result;
    }
    @Override
    protected void onPreExecute()
    {
        mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.setTitle("LoginStatus");
    }

    @Override
    protected void onPostExecute(String message) {

        PatientLoginActivity patientLoginActivity= (PatientLoginActivity)mContext;
        patientLoginActivity.showProgress(false);

        mAlertDialog.setMessage(message);
        if(message.equals("unsuccessful"))
        mAlertDialog.show();

        Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();

        if(message.equals("successful"))
        {Intent intent= new Intent(mContext,HomePageActivity.class);
            intent.putExtra("phone number",mPhoneNumber);
        patientLoginActivity.startActivity(intent);}


    }


}


