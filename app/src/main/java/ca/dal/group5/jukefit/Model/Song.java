package ca.dal.group5.jukefit.Model;

import java.util.Date;

/**
 * Created by lockhart on 2017-06-12.
 */

public class Song {

    String name;
    String url;
    Date endTime;

    public Song(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Song(String url, Date endTime) {
        this.url = url;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
