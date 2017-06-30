package ca.dal.group5.jukefit.API;

import java.io.File;
import java.util.List;

import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Model.Song;

/**
 * Created by lockhart on 2017-06-12.
 */

public interface APISpec {

    void createGroup(RequestHandler<Group> callback);
    void groupInformation(String groupCode, RequestHandler<Group> callback);
    void joinGroup(String groupCode, String name, String deviceID, RequestHandler<Group> callback);
    void updateScore(String groupCode, String deviceID, int newScore, RequestHandler<Member> callback);
    void mp3List(String deviceID, RequestHandler<List<Song>> callback);
    void mp3Upload(String deviceID, File songToUpload, RequestHandler<Song> callback);

}
