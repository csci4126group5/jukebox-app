package ca.dal.group5.jukefit.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lockhart on 2017-06-12.
 */

public class Group {

    String code;
    List<Member> members;
    Song currentSong;

    public Group(String code, List<Member> members, Song currentSong) {
        this.code = code;
        this.members = members;
        this.currentSong = currentSong;
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

    public List<Member> getSortedMembers() {
        List<Member> sortedMembers = new ArrayList<Member>();
        for (Member member : members) {
            sortedMembers.add(member);
        }
        Collections.sort(sortedMembers);
        return sortedMembers;
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
}
