package com.thecreators.android.opdplus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Shehzad Ahmed on 12/6/2016.
 */

public class BookAppointmentTask extends AsyncTask<URL,Void,String> {
    public static final String LOG_TAG = MakeAppointmentActivity.class.getSimpleName();
    private Context mContext;
    //private final String REQUEST_URL = "http://www.thecreatorstc.com/OPDPLUS/form/andriod/AndroidBookAppointment.php";
    private String REQUEST_URL ;

    String Date_of_Appoin;
    String DAY_KEY;
    String Contact_no;
    String Doc_CNIC;

    BookAppointmentTask(Context context,DoctorTimings doctorTimings, String patientPhoneNumber,int SelectedTimingIndex,String Date_of_Appointment)
    {
        this.mContext=context;
        this.Date_of_Appoin=Date_of_Appointment;
        this.DAY_KEY=Integer.toString(doctorTimings.getTimings().get(SelectedTimingIndex).getDAY_KEY());
        this.Contact_no=patientPhoneNumber;
        this.Doc_CNIC=doctorTimings.getDoctor().getCNIC();
        String s;
        REQUEST_URL = context.getString(R.string.URL_BookAppointment);
    }
    @Override
    protected String doInBackground(URL... urls) {
        // Create URL object
        URL url = createUrl(REQUEST_URL);

        // Perform HTTP request to the URL and receive a JSON response back
        String Result = "";
        try {
            Result = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object


        return Result;
        // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}

    }
    protected void onPostExecute(String result) {
        if ( result== null) {
            return;
        }
        Toast.makeText(mContext,result,Toast.LENGTH_LONG).show();
    }

    /**
     * Returns new URL object from the given string URL.
     */

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private String makeHttpRequest(URL url) throws IOException {
        String result = "Connection Time Out";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(10000000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000000 /* milliseconds */);
//            urlConnection.setDoOutput(true);
  //          urlConnection.setDoInput(true);
            urlConnection.connect();
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String post_data = URLEncoder.encode("Date_of_Appoin","UTF-8")+"="+URLEncoder.encode(Date_of_Appoin,"UTF-8")+
                    "&"+URLEncoder.encode("DAY_KEY","UTF-8")+"="+URLEncoder.encode(DAY_KEY,"UTF-8")+
                    "&"+URLEncoder.encode("Contact_no","UTF-8")+"="+URLEncoder.encode(Contact_no,"UTF-8")+
                    "&"+URLEncoder.encode("Doc_CNIC","UTF-8")+"="+URLEncoder.encode(Doc_CNIC,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            inputStream = urlConnection.getInputStream();
            result="";
            result = readFromStream(inputStream);
        } catch (IOException e) {
            // showProgress(false);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return result;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
