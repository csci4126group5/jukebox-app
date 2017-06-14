package ca.dal.group5.jukefit.Model;

import android.support.annotation.NonNull;

/**
 * Created by lockhart on 2017-06-12.
 */

public class Member implements Comparable<Member> {

    String name;
    String deviceID;
    int score;

    public Member(String name, String deviceID, int score) {
        this.name = name;
        this.deviceID = deviceID;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(@NonNull Member o) {
        return o.getScore() - this.getScore();
    }
}
