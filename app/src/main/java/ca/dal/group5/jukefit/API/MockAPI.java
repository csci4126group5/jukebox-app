package ca.dal.group5.jukefit.API;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Model.Song;

/**
 * Created by lockhart on 2017-06-12.
 */

public class MockAPI implements APISpec {

    public List <String> playerNames;
    public List <String> playerInfo;
    public List<Member> mockMembers(int n) {
        List<Member> members = new ArrayList<Member>();
        Random r = new Random();
        int Low = 0;
        int High = 10000;
        playerInfo = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            int Result = r.nextInt(High-Low) + Low;
            members.add(new Member("Player " + i, "DEVICE_ID_" + i, Result));
            playerInfo.add("Player " + i+"                                       "+Result);
        }
        return members;
    }


    @Override
    public Group createGroup() {
        return new Group("ABCD", mockMembers(0), null, null);
    }

    @Override
    public Group groupInformation(String groupCode) {
        return new Group(groupCode, mockMembers(3), new Song("/DEVICE_ID_1/mp3/SONG_NAME_1", new Date()), new Song("/DEVICE_ID_1/mp3/SONG_NAME_2", new Date()));
    }

    @Override
    public Group joinGroup(String groupCode, String name, String deviceID) {
        List<Member> members = mockMembers(2);
        members.add(new Member(name, deviceID, 0));
        return new Group(groupCode, members, new Song("/DEVICE_ID_1/mp3/SONG_NAME_1", new Date()), new Song("/DEVICE_ID_1/mp3/SONG_NAME_2", new Date()));
    }

    @Override
    public Member updateScore(String groupCode, String deviceID, int newScore) {
        return new Member("NAME_1", deviceID, newScore);
    }

    @Override
    public List<Song> mp3List(String deviceID) {
        List<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < 4; i++) {
            songs.add(new Song("SONG_NAME_" + i, "/" + deviceID + "/mp3/SONG_NAME_" + i));
        }
        return songs;
    }

    @Override
    public Song mp3Upload(String deviceID, File songToUpload) {
        return new Song("SONG_NAME", "/" + deviceID + "/mp3/SONG_NAME");
    }

    @Override
    public File mp3Download(String deviceID, String songName) {
        return null;
    }
}
