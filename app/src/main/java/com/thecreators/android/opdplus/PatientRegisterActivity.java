package com.thecreators.android.opdplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PatientRegisterActivity extends AppCompatActivity {
    UserRegistrationTask userRegistrationTask;
    // UI references.
    private EditText mCNICview;
    private EditText mNameView;
    private EditText mFatherNameView;
    private EditText mPhoneNumberView;
    private EditText mEmailAddressView;
    private EditText mAllergiesView;
    private EditText mBloodGroupView;
    private EditText mAddressView;
    private EditText mPasswordView;
    private Button mRegisterView;
    private Patient mPatient;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);


        mRegisterView = (Button) findViewById(R.id.register_button);
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    attemptRegister();
            }
        });

        }

        public void attemptRegister()
        {
            mPatient = new Patient();
            boolean cancel = false;
            View focusView = null;

            mCNICview = (EditText) findViewById(R.id.cnic_text);
            mPatient.CNIC = mCNICview.getText().toString();

            mNameView = (EditText) findViewById(R.id.name_text);
            mPatient.Name = mNameView.getText().toString();

            mFatherNameView = (EditText) findViewById(R.id.father_name_text);
            mPatient.FatherName=mFatherNameView.getText().toString();

            mPhoneNumberView = (EditText) findViewById(R.id.phone_number_text);
            mPatient.PhoneNumber = mPhoneNumberView.getText().toString();

            mEmailAddressView = (EditText) findViewById(R.id.email_address_text);
            mPatient.EmailAddress = mEmailAddressView.getText().toString();

            mAllergiesView = (EditText) findViewById(R.id.allergies_text);
            mPatient.Allergies = mAllergiesView.getText().toString();

            mBloodGroupView = (EditText) findViewById(R.id.blood_group_text);
            mPatient.BloodGroup = mBloodGroupView.getText().toString();

            mAddressView= (EditText) findViewById(R.id.address_text);
            mPatient.Address = mAddressView.getText().toString();

            mPasswordView = (EditText) findViewById(R.id.password_text);
            mPatient.Password = mPasswordView.getText().toString();



            if(mPatient.FatherName.isEmpty())
            {
                focusView=mFatherNameView;
                mFatherNameView.setError("Please Provide Your Father's Name");
                cancel=true;
            }

            if(mPatient.Name.isEmpty())
            {
                focusView=mNameView;
                mNameView.setError("Please Provide Your Name");
                cancel=true;
            }

            if(mPatient.Address.isEmpty())
            {
                focusView=mAddressView;
                mAddressView.setError("Please Provide Your Address");
                cancel=true;
            }

            if(mPatient.Allergies.isEmpty())
            {
                mPatient.Allergies="No Allergies";
            }

            if(mPatient.BloodGroup.isEmpty())
            {
                focusView=mBloodGroupView;
                mBloodGroupView.setError("Please Provide Blood Group");
                cancel=true;
            }



            if(mPatient.Password.isEmpty())
            {
                focusView=mPasswordView;
                mPasswordView.setError("Please Provide Your Password");
                cancel=true;
            }

            if(mPatient.PhoneNumber.isEmpty())
            {
                focusView=mPhoneNumberView;
                mPhoneNumberView.setError("Please Provide Your Phone Number");
                cancel=true;
            }
            else
            {
                if(mPatient.PhoneNumber.length()!=11)
                {
                    focusView=mPhoneNumberView;
                    mPhoneNumberView.setError("Please Provide a Valid Phone Number");
                    cancel=true;
                }
            }

            if(mPatient.CNIC.isEmpty())
            {
                focusView=mCNICview;
                mCNICview.setError("Please Provide Your CNIC");
                cancel=true;
            }

            else
            {
                if(mPatient.CNIC.length()!=13)
                {
                    focusView=mCNICview;
                    mCNICview.setError("Please Provide a valid CNIC");
                    cancel=true;
                }
            }

            if(cancel)
            {
                focusView.requestFocus();
            }
            else{
                showProgress(true);
                userRegistrationTask = new UserRegistrationTask(this, mPatient);
                userRegistrationTask.execute();
            }
        }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
