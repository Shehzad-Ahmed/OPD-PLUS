package com.thecreators.android.opdplus;

import java.util.ArrayList;

/**
 * Created by Shehzad Ahmed on 12/5/2016.
 */

public class DoctorTimings {

    private Doctor doctor;
    private ArrayList<Timings> timings;

    DoctorTimings(Doctor doctor)
    {this.doctor=doctor;
        this.timings=new ArrayList<Timings>();
    }
    DoctorTimings(Doctor doctor,ArrayList<Timings> timings)
    {
        this.doctor=doctor;
        this.timings=timings;
    }

    public ArrayList<Timings> getTimings() {
        return timings;
    }

    public void addTimings(Timings timings) {
        this.timings.add(timings);
    }

    public Doctor getDoctor() {
        return doctor;
    }
}
