package com.thecreators.android.opdplus;

import java.io.Serializable;

/**
 * Created by Shehzad Ahmed on 12/8/2016.
 */

public class Appointment implements Serializable {
    private String mDoctorName;

    public String getDoctorCNIC() {
        return mDoctorCNIC;
    }

    public void setDoctorCNIC(String doctorCNIC) {
        mDoctorCNIC = doctorCNIC;
    }

    private String mDoctorCNIC;
    private String mAppointmentNo;

    public String getDoctorName() {
        return mDoctorName;
    }

    public void setDoctorName(String doctorName) {
        mDoctorName = doctorName;
    }

    public String getAppointmentNo() {
        return mAppointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        mAppointmentNo = appointmentNo;
    }

    public String getDateOfAppointment() {
        return mDateOfAppointment;
    }

    public void setDateOfAppointment(String dateOfAppointment) {
        mDateOfAppointment = dateOfAppointment;
    }

    public String getDayOFWeek() {
        return mDayOFWeek;
    }

    public void setDayOFWeek(String dayOFWeek) {
        mDayOFWeek = dayOFWeek;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }

    private String mDateOfAppointment;
    private String mDayOFWeek;
    private String mStartTime;
    private String mEndTime;
}
