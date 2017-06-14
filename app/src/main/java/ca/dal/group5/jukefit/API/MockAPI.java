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
        deviceUser = new Member("YOU", "CURRENT_DEVICE", 0);
    }

    public List<Member> mockMembers(int n) {
        List<Member> members = new ArrayList<Member>();
        Random r = new Random();
        int Low = 0;
        int High = 10000;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                members.add(deviceUser);
            } else {
                int randomSteps = r.nextInt(High-Low) + Low;
                members.add(new Member("Player " + i, "DEVICE_ID_" + i, randomSteps));
            }
        }
        return members;
    }

    @Override
    public void createGroup(RequestHandler<Group> callback) {
        callback.success(new Group("ABCD", mockMembers(0), null, null));
    }

    @Override
    public void groupInformation(String groupCode, RequestHandler<Group> callback) {
        callback.success(new Group(groupCode, mockMembers(3), new Song("/DEVICE_ID_1/mp3/SONG_NAME_1", new Date()), new Song("/DEVICE_ID_1/mp3/SONG_NAME_2", new Date())));
    }

    @Override
    public void joinGroup(String groupCode, String name, String deviceID, RequestHandler<Group> callback) {
        List<Member> members = mockMembers(2);
        members.add(new Member(name, deviceID, 0));
        callback.success(new Group(groupCode, members, new Song("/DEVICE_ID_1/mp3/SONG_NAME_1", new Date()), new Song("/DEVICE_ID_1/mp3/SONG_NAME_2", new Date())));
    }

    @Override
    public void updateScore(String groupCode, String deviceID, int newScore, RequestHandler<Member> callback) {
        deviceUser.setScore(newScore);
        callback.success(deviceUser);
    }

    @Override
    public void mp3List(String deviceID, RequestHandler<List<Song>> callback) {
        List<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < 4; i++) {
            songs.add(new Song("SONG_NAME_" + i, "/" + deviceID + "/mp3/SONG_NAME_" + i));
        }
        callback.success(songs);
    }

    @Override
    public void mp3Upload(String deviceID, File songToUpload, RequestHandler<Song> callback) {
        callback.success(new Song("SONG_NAME", "/" + deviceID + "/mp3/SONG_NAME"));
    }

    @Override
    public void mp3Download(String deviceID, String songName, RequestHandler<File> callback) {
        callback.success(null);
    }
}
