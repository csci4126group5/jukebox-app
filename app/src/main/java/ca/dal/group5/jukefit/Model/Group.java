package ca.dal.group5.jukefit.Model;

import java.util.List;

/**
 * Created by lockhart on 2017-06-12.
 */

public class Group {

    String code;
    List<Member> members;
    Song currentSong;
    Song nextSong;

    public Group(String code, List<Member> members, Song currentSong, Song nextSong) {
        this.code = code;
        this.members = members;
        this.currentSong = currentSong;
        this.nextSong = nextSong;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public Song getNextSong() {
        return nextSong;
    }

    public void setNextSong(Song nextSong) {
        this.nextSong = nextSong;
    }
}
