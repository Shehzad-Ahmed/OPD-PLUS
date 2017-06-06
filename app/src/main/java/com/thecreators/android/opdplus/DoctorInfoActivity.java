package com.thecreators.android.opdplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class DoctorInfoActivity extends AppCompatActivity {

    Context mContext;
    public static final String LOG_TAG = DoctorInfoActivity.class.getSimpleName();
    //private static final String REQUEST_URL ="http://www.thecreatorstc.com/OPDPLUS/form/andriod/AndroidDoctorsInfo.php";
   private String REQUEST_URL;


    View doctor_Info_Form;
    View mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);
        mContext=this;
        doctor_Info_Form = findViewById(R.id.list);
         mProgressBar = findViewById(R.id.doctor_info_fetching_progress);

        REQUEST_URL = getString(R.string.URL_DoctorsInfo);

        DoctorInfoAsyncTask task = new DoctorInfoAsyncTask();
       showProgress(true);
        task.execute();
    }

    private class DoctorClickListener implements ListView.OnItemClickListener{
        ArrayList<Doctor> mDoctorArrayList;
        DoctorClickListener(ArrayList<Doctor> mDoc)
        {
            mDoctorArrayList=mDoc;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent intent = new Intent(mContext,MakeAppointmentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("doctor", mDoctorArrayList.get(i));
            intent.putExtras(bundle);
            intent.putExtra("phone number",getIntent().getStringExtra("phone number"));
            mContext.startActivity(intent);

        }
    }

    /**
     * Update the screen to display information from the given {@link Doctor}.
     */
    private void updateUi(final ArrayList<Doctor> doctor) {

        doctorAdapter doctorsList = new doctorAdapter(mContext,doctor);

        ListView listview = (ListView) findViewById(R.id.list);


        showProgress(false);
        Toast.makeText(mContext,"Select Doctor ..",Toast.LENGTH_SHORT).show();
        listview.setAdapter(doctorsList);
        listview.setOnItemClickListener(new DoctorClickListener(doctor));


    }

    private class doctorAdapter extends ArrayAdapter<Doctor>
    {

        public doctorAdapter(Context context,ArrayList<Doctor> doctors) {
            super(context, 0, doctors);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;

            if(listItemView==null)
            {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.doctors_list,parent,false);
            }

            Doctor doctor = getItem(position);

            TextView textViewName = (TextView) listItemView.findViewById(R.id.doctor_name);
            textViewName.setText("Doctor Name: "+doctor.getName());

            TextView textViewSpecializton = (TextView) listItemView.findViewById(R.id.doctor_specialization);
            textViewSpecializton.setText("Specialization: "+doctor.getSpecialization());

            TextView textViewEmailAddress = (TextView) listItemView.findViewById(R.id.doctor_emailAddress);
            textViewEmailAddress.setText("Email Address: "+doctor.getEmail_Address());

            TextView textViewFeesPerAppoin = (TextView) listItemView.findViewById(R.id.doctor_feesPerAppoin);
            textViewFeesPerAppoin.setText("Fees: "+doctor.getFees_Per_Appoin());

            return listItemView;
        }
    }





    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first doctor in the response.
     */

    public class DoctorInfoAsyncTask extends AsyncTask<URL, Void, ArrayList<Doctor>> {

        @Override
        protected ArrayList<Doctor> doInBackground(URL... urls) {
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
         * Update the screen with the given earthquake (which was the result of the
         * {@link DoctorInfoAsyncTask}).
         */

        @Override
        protected void onPostExecute(ArrayList<Doctor> doctor) {
            if ( doctor== null) {
                return;
            }

            updateUi(doctor);
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
                urlConnection.connect();
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


        private ArrayList<Doctor> extractFeatureFromJson(String doctorJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(doctorJSON);
                JSONArray doctorArray = baseJsonResponse.getJSONArray("doctors");

                // If there are results in the doctors array
                if (doctorArray.length() > 0) {
                    // Extract out the first doctor info

                    JSONObject firstdoc;

                    ArrayList<Doctor> doctor = new ArrayList<Doctor>();
                    String docinfo[] = new String[5];
                     for(int i=0;i<doctorArray.length();i++)
                    {
                        firstdoc= doctorArray.getJSONObject(i);

                        // Extract Doctor info values
                        docinfo[0] = firstdoc.getString("Doc_CNIC");
                        docinfo[1] = firstdoc.getString("Name");
                        docinfo[2] = firstdoc.getString("Specialization");
                        docinfo[3] = firstdoc.getString("Email_Adress");
                        docinfo[4] = firstdoc.getString("Fees_Per_Appoin");

                        doctor.add(new Doctor(docinfo));

                    }
                    return doctor;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the doctor JSON results", e);
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

            doctor_Info_Form.setVisibility(show ? View.GONE : View.VISIBLE);
            doctor_Info_Form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    doctor_Info_Form.setVisibility(show ? View.GONE : View.VISIBLE);
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
            doctor_Info_Form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

