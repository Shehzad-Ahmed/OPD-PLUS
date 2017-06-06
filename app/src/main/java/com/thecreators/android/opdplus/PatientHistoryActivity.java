package com.thecreators.android.opdplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.R.attr.handle;

public class PatientHistoryActivity extends FragmentActivity {


    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    Context mContext;
    public static final String LOG_TAG = MakeAppointmentActivity.class.getSimpleName();
    //private static final String REQUEST_URL = "http://www.thecreatorstc.com/OPDPLUS/form/andriod/AndroidHistory.php";
    private String REQUEST_URL ;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);
        REQUEST_URL = getString(R.string.URL_History);
        mPhoneNumber=getIntent().getExtras().getString("phone number");
        mContext=this;
        HistoryAsyncTask historyAsyncTask = new HistoryAsyncTask();
        historyAsyncTask.execute();



    }

    private void UpdateUI(ArrayList<History> histories)
    {
            HistoryAdapter historyAdapter = new HistoryAdapter(mContext,histories);

        ListView listView = (ListView) findViewById(R.id.history_List);
        listView.setAdapter(historyAdapter);

    }

    private class HistoryAdapter extends ArrayAdapter<History>
    {

        public HistoryAdapter(Context context,ArrayList<History> histories) {
            super(context, 0, histories);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;

            if(listItemView==null)
            {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.history_listview,parent,false);
            }

            History history = getItem(position);

            final ImageView imageView = (ImageView) listItemView.findViewById(R.id.history_PriscriptionImageView);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            if (history.getPriscriptionImage()==null) {
                try {
                    history.setPriscriptionImage(getImage(history.getAppointmentID()));
                    imageView.setImageBitmap(history.getPriscriptionImage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                imageView.setImageBitmap(history.getPriscriptionImage());
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(imageView,((BitmapDrawable)imageView.getDrawable()).getBitmap());
                }
            });

            // Retrieve and cache the system's default "short" animation time.
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);



            TextView textViewDateOfAppoin = (TextView) listItemView.findViewById(R.id.history_DateOfAppoin);
            textViewDateOfAppoin.setText("Date Of Appointment: "+history.getDateOfAppoin());

            TextView textViewDoctorName = (TextView) listItemView.findViewById(R.id.history_DoctorName);
            textViewDoctorName.setText("Doctor Name: "+history.getDoctorName());



            return listItemView;
        }
    }

    private class HistoryAsyncTask extends AsyncTask<Void,Void,ArrayList<History>>
    {

        @Override
        protected ArrayList<History> doInBackground(Void... voids) {
            URL url = createUrl(REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                //   showProgress(false);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object

            return extractFeatureFromJson(jsonResponse);

        }

        @Override
        protected void onPostExecute(ArrayList<History> histories)
        {
            if(histories==null)
            {
                return;
            }
            UpdateUI(histories);
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
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data = URLEncoder.encode("mPhoneNumber","UTF-8")+"="+URLEncoder.encode(mPhoneNumber,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                 urlConnection.connect();
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

        private ArrayList<History> extractFeatureFromJson(String historyJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(historyJSON);
                JSONArray historyArray = baseJsonResponse.getJSONArray("history");

                // If there are results in the appointments array
                if (historyArray.length() > 0) {
                    // Extract out the first appointment info

                    JSONObject historyObject;

                    ArrayList<History> histories = new ArrayList<History>();

                    for(int i=0;i<historyArray.length();i++)
                    {
                        History history = new History();
                        historyObject= historyArray.getJSONObject(i);

                        // Extract history values
                        history.setDateOfAppoin(historyObject.getString("DateOfAppoin"));
                        history.setDoctorName(historyObject.getString("DoctorName"));
                        history.setAppointmentID(historyObject.getString("appointmentID"));
                        history.setPriscriptionImage(null);
                      //  InputStream stream = new ByteArrayInputStream(imageArray.getString(0).getBytes(StandardCharsets.UTF_8));
                       // history.setPriscriptionImage(BitmapFactory.decodeStream(stream));
                        histories.add(history);
                    }
                    return histories;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the Appointment JSON results", e);
            }
            return null;
        }
    }


    private Bitmap getImage(String AppointmentID) throws IOException
    {

        PrescriptionImageTask prescriptionImageTask = new PrescriptionImageTask(AppointmentID,mContext);
        Bitmap bitmap=null;
        try {
             bitmap = prescriptionImageTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //zooming of Image

    private void zoomImageFromThumb(final View thumbView, Bitmap bitmap) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageBitmap(bitmap);

        ListView listView = (ListView) findViewById(R.id.history_List);
        listView.setVisibility(View.INVISIBLE);
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();

                }


                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;

                ListView listView = (ListView) findViewById(R.id.history_List);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }




}
