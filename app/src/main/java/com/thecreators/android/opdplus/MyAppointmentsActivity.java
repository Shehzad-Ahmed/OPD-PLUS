package com.thecreators.android.opdplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

//Reminder .... Make sure to change Date Format in PHP file because saeed has changed it.

public class MyAppointmentsActivity extends AppCompatActivity {


    Context mContext;
    public static final String LOG_TAG = MakeAppointmentActivity.class.getSimpleName();
    //private static final String REQUEST_URL = "http://www.thecreatorstc.com/OPDPLUS/form/andriod/AndroidMyAppointment.php";
    private  String REQUEST_URL ;
    private String mPhoneNumber;
    View appointment_listView;
    View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);
        mProgressBar=findViewById(R.id.appointment_info_fetching_progress);
        appointment_listView=findViewById(R.id.appointment_listview);
        REQUEST_URL = getString(R.string.URL_MyAppointments);
        mPhoneNumber=getIntent().getStringExtra("phone number");
        mContext=this;
        AppointmentAsyncTask appointmentAsyncTask = new AppointmentAsyncTask();
        appointmentAsyncTask.execute();

        showProgress(true);

    }

    private class AppointmentClickListener implements ListView.OnItemClickListener{
        ArrayList<Appointment> mAppointmentArrayList;
        AppointmentClickListener(ArrayList<Appointment> appointments)
        {
            mAppointmentArrayList=appointments;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(c.getTime());

            if(formattedDate.equals(mAppointmentArrayList.get(i).getDateOfAppointment())) {
                Intent intent = new Intent(mContext, TrackingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("appointment", mAppointmentArrayList.get(i));
                intent.putExtras(bundle);
                //intent.putExtra("phone number",getIntent().getStringExtra("phone number"));
                mContext.startActivity(intent);
            }else
            {
                Toast.makeText(mContext,"No Tracking",Toast.LENGTH_SHORT).show();
            }

            //Toast.makeText(mContext,mAppointmentArrayList.get(i).getDoctorCNIC(),Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Update the screen to display information from the given {@link Appointment}.
     */
    private void updateUi(final ArrayList<Appointment> appointments) {

        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(mContext,appointments);

        ListView listview = (ListView) findViewById(R.id.appointment_listview);


        showProgress(false);
        listview.setAdapter(appointmentAdapter);
        listview.setOnItemClickListener(new MyAppointmentsActivity.AppointmentClickListener(appointments));


    }

    private class AppointmentAdapter extends ArrayAdapter<Appointment>
    {

        public AppointmentAdapter(Context context, ArrayList<Appointment> appointments) {
            super(context, 0, appointments);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;

            if(listItemView==null)
            {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.appointment_list,parent,false);
            }

            Appointment appointment = getItem(position);

            TextView textViewDoctorName = (TextView) listItemView.findViewById(R.id.appointment_DoctorName);
            textViewDoctorName.setText("Doctor Name: "+appointment.getDoctorName());

            TextView textViewDateOfAppoin = (TextView) listItemView.findViewById(R.id.appointment_DateOfAppointment);
            textViewDateOfAppoin.setText("Date of Appointment: "+appointment.getDateOfAppointment());

            TextView textViewAppoinNo = (TextView) listItemView.findViewById(R.id.appointment_AppointmentNo);
            textViewAppoinNo.setText("Appointment Number: "+appointment.getAppointmentNo());

            TextView textViewDayOfWeek = (TextView) listItemView.findViewById(R.id.appointment_DayOfWeek);
            textViewDayOfWeek.setText("Day of Week: "+appointment.getDayOFWeek().toUpperCase());

            TextView textViewStartTime = (TextView) listItemView.findViewById(R.id.appointment_StartTime);
            textViewStartTime.setText("Start Time: "+appointment.getStartTime());

            TextView textViewEndTime = (TextView) listItemView.findViewById(R.id.appointment_EndTime);
            textViewEndTime.setText("End Time: "+appointment.getEndTime());

            return listItemView;
        }
    }





    public class AppointmentAsyncTask extends AsyncTask<URL, Void, ArrayList<Appointment>> {

        @Override
        protected ArrayList<Appointment> doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                showProgress(false);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object



            return extractFeatureFromJson(jsonResponse);
        }



    /**
     * Update the screen with the given appointment (which was the result of the
     */

    @Override
    protected void onPostExecute(ArrayList<Appointment> appointments) {
        showProgress(false);
        if ( appointments== null) {
            Toast.makeText(mContext,"Nothing To Show",Toast.LENGTH_LONG).show();
            return;
        }

        updateUi(appointments);
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
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String post_data = URLEncoder.encode("mPhoneNumber","UTF-8")+"="+URLEncoder.encode(mPhoneNumber,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            // urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            showProgress(false);
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


    private ArrayList<Appointment> extractFeatureFromJson(String appointmentsJSON) {
        try {
            JSONObject baseJsonResponse = new JSONObject(appointmentsJSON);
            JSONArray appointmentsArray = baseJsonResponse.getJSONArray("appointments");

            // If there are results in the appointments array
            if (appointmentsArray.length() > 0) {
                // Extract out the first appointment info

                JSONObject firstapp;

                ArrayList<Appointment> appointments = new ArrayList<Appointment>();

                for(int i=0;i<appointmentsArray.length();i++)
                {
                    Appointment appointment = new Appointment();
                    firstapp= appointmentsArray.getJSONObject(i);

                    // Extract Appointment info values
                    appointment.setDoctorCNIC(firstapp.getString("DoctorCNIC"));
                    appointment.setDoctorName(firstapp.getString("DoctorName"));
                    appointment.setAppointmentNo(firstapp.getString("AppointmentNo"));
                    appointment.setDateOfAppointment(firstapp.getString("DateOfAppointment"));
                    appointment.setDayOFWeek(firstapp.getString("DayOfWeek"));
                    appointment.setStartTime(firstapp.getString("StartTime"));
                    appointment.setEndTime(firstapp.getString("EndTime"));

                    appointments.add(appointment);

                }
                return appointments;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Appointment JSON results", e);
        }
        return null;
    }
}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            appointment_listView.setVisibility(show ? View.GONE : View.VISIBLE);
            appointment_listView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    appointment_listView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            appointment_listView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}


