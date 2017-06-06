package com.thecreators.android.opdplus;

/**
 * Created by Shehzad Ahmed on 12/28/2016.
 */

public class Tracking {
    private String noOfPAtients;
    private String avgTimePerPAtient;
    private String message;
    private String status;

    public String getNoOfPAtients() {
        return noOfPAtients;
    }

    public void setNoOfPAtients(String noOfPAtients) {
        this.noOfPAtients = noOfPAtients;
    }

    public String getAvgTimePerPAtient() {
        return avgTimePerPAtient;
    }

    public void setAvgTimePerPAtient(String avgTimePerPAtient) {
        this.avgTimePerPAtient = avgTimePerPAtient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
