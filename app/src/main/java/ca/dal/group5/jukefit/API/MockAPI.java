package ca.dal.group5.jukefit.API;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Model.Song;
import ca.dal.group5.jukefit.PlaylistAndWorkoutActivity;

/**
 * Created by lockhart on 2017-06-12.
 */

public class MockAPI implements APISpec {

    private Member deviceUser;

    public MockAPI() {
        deviceUser = new Member("YOU          ", "CURRENT_DEVICE", 0);
    }

    public List<Member> mockMembers(int n) {
        List<Member> members = new ArrayList<Member>();
        Random r = new Random();
        int Low = 0;
        int High = 10000;
        for (int i = 1; i <= 9; i++) {
            if (i == n) {
                members.add(deviceUser);
            } else {
                int randomSteps = r.nextInt(High-Low) + Low;
                members.add(new Member("Player " + i, "DEVICE_ID_" + i, randomSteps));
            }
        }
        return members;
    }

    @Override
    public void createGroup(RequestHandler<Group> handler) {
        handler.callback(new Group("ABCD", mockMembers(0), null));
    }

    @Override
    public void groupInformation(String groupCode, RequestHandler<Group> handler) {
        handler.callback(new Group(groupCode, mockMembers(3), new Song("/DEVICE_ID_1/mp3/SONG_NAME_1", new Date())));
    }

    @Override
    public void joinGroup(String groupCode, String name, String deviceID, RequestHandler<Group> handler) {
        List<Member> members = mockMembers(2);
        members.add(new Member(name, deviceID, 0));
        handler.callback(new Group(groupCode, members, new Song("/DEVICE_ID_1/mp3/SONG_NAME_1", new Date())));
    }

    @Override
    public void updateScore(String groupCode, String deviceID, int newScore, RequestHandler<Member> handler) {
        deviceUser.setScore(newScore);
        handler.callback(deviceUser);
    }

    @Override
    public void mp3List(String deviceID, RequestHandler<List<Song>> handler) {
        List<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < 4; i++) {
            songs.add(new Song("SONG_NAME_" + i, "/" + deviceID + "/mp3/SONG_NAME_" + i));
        }
        handler.callback(songs);
    }

    @Override
    public void mp3Upload(String deviceID, File songToUpload, RequestHandler<Song> handler) {
        handler.callback(new Song("SONG_NAME", "/" + deviceID + "/mp3/SONG_NAME"));
    }
}
