package com.thecreators.android.opdplus;

import java.io.Serializable;

/**
 * Created by Shehzad Ahmed on 11/30/2016.
 */
public class Doctor implements Serializable{


    private String Name;
    private String Specialization;
    private String Email_Address;
    private String Fees_Per_Appoin;
    private String CNIC;

    Doctor(String N[])
    {

         CNIC=N[0];
         Name=N[1];
         Specialization=N[2];
         Email_Address=N[3];
         Fees_Per_Appoin=N[4];
    }


    public String getCNIC() {
        return CNIC;
    }

    public String getName() {
        return Name;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public String getEmail_Address() {
        return Email_Address;
    }

    public String getFees_Per_Appoin() {
        return Fees_Per_Appoin;
    }


}
