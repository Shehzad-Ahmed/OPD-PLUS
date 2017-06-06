package com.thecreators.android.opdplus;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

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
public class UserRegistrationTask extends AsyncTask<Patient,Void,String> {

    Context mContext;

    String result="Connection Time Out";
    AlertDialog mAlertDialog;
    public String CNIC;
    public String Name;
    public String FatherName;
    public String PhoneNumber;
    public String EmailAddress;
    public String Allergies;
    public String BloodGroup;
    public String Address;
    public String Password;

    UserRegistrationTask(Context context,Patient patient)
    {
        mContext=context;

        CNIC=patient.CNIC;
        Name=patient.Name;
        FatherName=patient.FatherName;
        PhoneNumber=patient.PhoneNumber;
        EmailAddress=patient.EmailAddress;
        Allergies=patient.Allergies;
        BloodGroup=patient.BloodGroup;
        Address=patient.Address;
        Password=patient.Password;
    }

    @Override
    protected String doInBackground(Patient... voids) {
//        String register_url = "http://www.thecreatorstc.com/OPDPLUS/form/andriod/AndroidRegister.php";
        String register_url = mContext.getString(R.string.URL_Registration);
        try {

            URL url = new URL(register_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(500000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String post_data = URLEncoder.encode("CNIC","UTF-8")+"="+URLEncoder.encode(CNIC,"UTF-8")+"&"
                    +URLEncoder.encode("Name","UTF-8")+"="+URLEncoder.encode(Name,"UTF-8")+"&"
                    +URLEncoder.encode("FatherName","UTF-8")+"="+URLEncoder.encode(FatherName,"UTF-8")+"&"
                    +URLEncoder.encode("PhoneNumber","UTF-8")+"="+URLEncoder.encode(PhoneNumber,"UTF-8")+"&"
                    +URLEncoder.encode("EmailAddress","UTF-8")+"="+URLEncoder.encode(EmailAddress,"UTF-8")+"&"
                    +URLEncoder.encode("Allergies","UTF-8")+"="+URLEncoder.encode(Allergies,"UTF-8")+"&"
                    +URLEncoder.encode("BloodGroup","UTF-8")+"="+URLEncoder.encode(BloodGroup,"UTF-8")+"&"
                    +URLEncoder.encode("Address","UTF-8")+"="+URLEncoder.encode(Address,"UTF-8")+"&"
                    +URLEncoder.encode("Password","UTF-8")+"="+URLEncoder.encode(Password,"UTF-8");
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



        return result;


    }
    protected void onPreExecute()
    {

        mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.setTitle("RegisterStatus");
    }

    @Override
    protected void onPostExecute(String message) {


        PatientRegisterActivity patientRegisterActivity= (PatientRegisterActivity) mContext;
        patientRegisterActivity.showProgress(false);

        mAlertDialog.setMessage(message);
        mAlertDialog.show();
    }
}
