package com.thecreators.android.opdplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MakeAppointmentActivity extends AppCompatActivity {

    Context mContext;
    public static final String LOG_TAG = MakeAppointmentActivity.class.getSimpleName();
   // private static final String REQUEST_URL = "http://www.thecreatorstc.com/OPDPLUS/form/andriod/AndroidDoctorsTiming.php";
    private String REQUEST_URL ;

    DoctorTimings doctorTimings;
    String phoneNumber;
    Button selectbutton,cancelButton;
    DatePicker datePicker;

    View make_appointment_Form;
    View mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_appointment);
        make_appointment_Form = findViewById(R.id.make_appointment_form);
        mProgressBar = findViewById(R.id.doctor_timing_fetching_progress);
        REQUEST_URL = getString(R.string.URL_doctorTimings_MakeAppointment);
        mContext=this;
        doctorTimings= new DoctorTimings((Doctor) getIntent().getExtras().getSerializable("doctor"));
        phoneNumber= getIntent().getStringExtra("phone number");
        datePicker = (DatePicker) findViewById(R.id.appointment_DatePicker);
        selectbutton = (Button) findViewById(R.id.appointment_DatePickerSelector);
        cancelButton = (Button) findViewById(R.id.appointment_cancelSelection);

        Calendar calendar = Calendar.getInstance();
        datePicker.setMinDate(calendar.getTimeInMillis());
        datePicker.setMaxDate((long)calendar.getTimeInMillis()+(long)2.592e+9+(long)2.592e+9+(long)2.592e+9);

        DoctorTimingsAsyncTask doctorTimings = new DoctorTimingsAsyncTask();

        doctorTimings.execute();
        showProgress(true);

    }
    private void updateUi(final ArrayList<Timings> timings) {
        showProgress(false);

        String timingStrings[] = new String[timings.size()];
        for(int i=0;i<timingStrings.length;i++)
        {
            timingStrings[i]= timings.get(i).getTimingString();
        }
        ArrayAdapter<String> timingsAdapter = new ArrayAdapter<String>(mContext,R.layout.timing_list,timingStrings);

        final ListView listView = (ListView) findViewById(R.id.list_item_timing);
        Toast.makeText(mContext,"Select your feasible timing ..",Toast.LENGTH_SHORT).show();
        listView.setAdapter(timingsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                listView.setVisibility(View.GONE);
                datePicker.setVisibility(View.VISIBLE);
                selectbutton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Select your feasible Date ..",Toast.LENGTH_SHORT).show();

                selectbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SimpleDateFormat dateformater = new SimpleDateFormat("EEEE");



                        Date date = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()-1);
                        String dayOfWeek = dateformater.format(date);

                        Date date1= new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        SimpleDateFormat df = new SimpleDateFormat("dd-MM");
                        String selectedDate= df.format(date1);



                        String first3CharactersOfDayOfWeek = dayOfWeek.substring(0,2).toUpperCase();
                        if(first3CharactersOfDayOfWeek.equals(timings.get(i).getDay().substring(0,2).toUpperCase()))
                        {

                                    selectedDate+= "-"+Integer.toString(datePicker.getYear());
                            listView.setVisibility(View.VISIBLE);
                            datePicker.setVisibility(View.GONE);
                            selectbutton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            Toast.makeText(mContext,"Wait ......",Toast.LENGTH_SHORT).show();
                            BookAppointmentTask bookAppointmentTask = new BookAppointmentTask(mContext,doctorTimings,phoneNumber,
                                    i,selectedDate);
                            bookAppointmentTask.execute();

                        }
                        else
                        {

                            Toast.makeText(mContext,"Please choose your respective Day of Week i.e "+timings.get(i).getDay().toUpperCase(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listView.setVisibility(View.VISIBLE);
                        datePicker.setVisibility(View.GONE);
                        selectbutton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                        Toast.makeText(mContext,"Select your feasible Date ..",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }





    public class DoctorTimingsAsyncTask extends AsyncTask<URL, Void, ArrayList<Timings>> {

        @Override
        protected ArrayList<Timings> doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object



            return extractFeatureFromJson(jsonResponse);
            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}

        }



        @Override
        protected void onPostExecute(ArrayList<Timings> timings) {
            if ( timings== null) {
                return;
            }
            for(Timings t : timings)
            {doctorTimings.addTimings(t);}
            updateUi(timings);
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
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String doctor_cnic=doctorTimings.getDoctor().getCNIC();
                String post_data = URLEncoder.encode("doctor_cnic","UTF-8")+"="+URLEncoder.encode(doctor_cnic,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
              //  urlConnection.connect();
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

        /**
         * Return an {@link Doctor} object by parsing out information
         * about the first earthquake from the input earthquakeJSON string.
         */
        private ArrayList<Timings> extractFeatureFromJson(String timingJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(timingJSON);
                JSONArray timingsArray = baseJsonResponse.getJSONArray("timing");

                // If there are results in the doctors array
                if (timingsArray.length() > 0) {
                    // Extract out the first doctor info

                    JSONObject timingObject;

                    ArrayList<Timings> timings = new ArrayList<Timings>();
                    String temp[] = new String[4];
                    for(int i=0;i<timingsArray.length();i++)
                    {
                        timingObject= timingsArray.getJSONObject(i);

                        // Extract Doctor timings values
                        temp[0] = timingObject.getString("DAY_KEY");
                        temp[1] = timingObject.getString("Day").toUpperCase();
                        temp[2] = timingObject.getString("Start_Time");
                        temp[3] = timingObject.getString("End_Time");


                        timings.add(new Timings(Integer.parseInt(temp[0]),temp[1],temp[2],temp[3]));

                    }

                    return timings;
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

            make_appointment_Form.setVisibility(show ? View.GONE : View.VISIBLE);
            make_appointment_Form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    make_appointment_Form.setVisibility(show ? View.GONE : View.VISIBLE);
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
            make_appointment_Form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
