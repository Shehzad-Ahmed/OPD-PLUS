package com.thecreators.android.opdplus;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class TrackingActivity extends AppCompatActivity {
    private Appointment mAppointmentToTrack;
    Context mContext;
    public static final String LOG_TAG = TrackingActivity.class.getSimpleName();
    //private static final String REQUEST_URL ="http://www.thecreatorstc.com/OPDPLUS/form/andriod/AndroidAppointmentTracking.php";
    private String REQUEST_URL ;

    TextView noOfPatientsTextView,avgNoOfPatientsTextView,messageTextView,statusTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        mAppointmentToTrack = (Appointment) getIntent().getExtras().getSerializable("appointment");
        mContext=this;
        REQUEST_URL = getString(R.string.URL_Tracking);
        noOfPatientsTextView= (TextView) findViewById(R.id.tracking_noOfPatients);
        avgNoOfPatientsTextView= (TextView) findViewById(R.id.tracking_avgTime);
        messageTextView= (TextView) findViewById(R.id.tracking_message);
        statusTextView= (TextView) findViewById(R.id.tracking_status);

        TrackingInfoAsyncTask trackingInfoAsyncTask = new TrackingInfoAsyncTask();
        trackingInfoAsyncTask.execute();

    }

    private void UpdateUI(Tracking tracking)
    {
        noOfPatientsTextView.setText("Current No Of Patients: "+tracking.getNoOfPAtients());
        avgNoOfPatientsTextView.setText("Average Time Per Patient: "+tracking.getAvgTimePerPAtient());
        messageTextView.setText("Message From Doctor: "+tracking.getMessage());
        statusTextView.setText("Status: "+tracking.getStatus());
    }

    private class TrackingInfoAsyncTask extends AsyncTask<Void,Void,Tracking> {

        @Override
        protected Tracking doInBackground(Void... voids) {
            // Create URL object
            URL url = createUrl(REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // showProgress(false);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object


            return extractFeatureFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(Tracking tracking)
        {
            if(tracking==null)
            {Toast.makeText(mContext,"Tracking has not started",Toast.LENGTH_LONG).show();
                return;
                }

            UpdateUI(tracking);

        }

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

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String day, starttime, endtime, doctorCnic;
                day = mAppointmentToTrack.getDayOFWeek();
                starttime = mAppointmentToTrack.getStartTime();
                endtime = mAppointmentToTrack.getEndTime();
                doctorCnic = mAppointmentToTrack.getDoctorCNIC();
                String post_data = URLEncoder.encode("day", "UTF-8") + "=" + URLEncoder.encode(day, "UTF-8")
                        + "&" + URLEncoder.encode("starttime", "UTF-8") + "=" + URLEncoder.encode(starttime, "UTF-8")
                        + "&" + URLEncoder.encode("endtime", "UTF-8") + "=" + URLEncoder.encode(endtime, "UTF-8")
                        + "&" + URLEncoder.encode("doctorCnic", "UTF-8") + "=" + URLEncoder.encode(doctorCnic, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                // urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
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
            return jsonResponse;
        }

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

        private Tracking extractFeatureFromJson(String appointmentTrackingJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(appointmentTrackingJSON);
                JSONArray appointmentTrackingArray = baseJsonResponse.getJSONArray("tracking");

                // If there are results in the appointments array
                if (appointmentTrackingArray.length() > 0) {
                    // Extract out the first appointment info

                    JSONObject JSONtrackingObject = appointmentTrackingArray.getJSONObject(0);
                    Tracking tracking = new Tracking();
                    tracking.setNoOfPAtients(JSONtrackingObject.getString("presentNoOfPatient"));
                    tracking.setAvgTimePerPAtient(JSONtrackingObject.getString("AvgTimePerPatient"));
                    tracking.setMessage(JSONtrackingObject.getString("Message"));
                    tracking.setStatus(JSONtrackingObject.getString("Status"));

                    return tracking;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the Appointment JSON results", e);
            }
            return null;
        }
    } //end of AsyncTask
}
