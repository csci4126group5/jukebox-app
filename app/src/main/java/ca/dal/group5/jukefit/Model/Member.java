package ca.dal.group5.jukefit.Model;

/**
 * Created by lockhart on 2017-06-12.
 */

public class Member {

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

    public int getScore(String name) {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
