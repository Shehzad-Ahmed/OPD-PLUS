package com.thecreators.android.opdplus;

/**
 * Created by Shehzad Ahmed on 12/5/2016.
 */

public class Timings {

    private int DAY_KEY;
    private String Day;
    private String startTime;
    private String endTime;

    Timings(int DAY_KEY,String Day,String startTime,String endTime)
    {
        this.DAY_KEY=DAY_KEY;
        this.Day=Day;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDay() {
        return Day;
    }

    public int getDAY_KEY() {
        return DAY_KEY;
    }

    public String getTimingString()
    {
        return "DAY:               " + Day.toUpperCase() + System.lineSeparator() + "START TIME: " + startTime + System.lineSeparator() +"END TIME:     " + endTime;
    }

    public int getDayInInteger()
    {
            if(Day.equals("SUN"))
               return 1;

            if(Day.equals("MON"))
               return 2;

            if(Day.equals("TUE"))
                return 3;

            if (Day.equals("WED"))
                return 4;

            if (Day.equals("THU"))
                return 5;

            if (Day.equals("FRI"))
                return 6;

            if (Day.equals("SAT"))
                return 7;


            return 0;
    }
}
