package com.thecreators.android.opdplus;

import android.graphics.Bitmap;

/**
 * Created by Shehzad Ahmed on 12/28/2016.
 */

public class History {
    private String DoctorName;
    private String DateOfAppoin;

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    private String appointmentID;

    public Bitmap getPriscriptionImage() {
        return priscriptionImage;
    }

    public void setPriscriptionImage(Bitmap priscriptionImage) {
        this.priscriptionImage = priscriptionImage;
    }

    public String getDateOfAppoin() {
        return DateOfAppoin;
    }

    public void setDateOfAppoin(String dateOfAppoin) {
        DateOfAppoin = dateOfAppoin;
    }

    public String getDoctorName() {
        return DoctorName;
    }

    public void setDoctorName(String doctorName) {
        DoctorName = doctorName;
    }

    private Bitmap priscriptionImage;
}
