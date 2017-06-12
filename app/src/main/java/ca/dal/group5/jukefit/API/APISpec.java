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

    Group createGroup();
    Group groupInformation(String groupCode);
    Group joinGroup(String groupCode, String name, String deviceID);
    Member updateScore(String groupCode, String deviceID, int newScore);
    List<Song> mp3List(String deviceID);
    Song mp3Upload(String deviceID, File songToUpload);
    File mp3Download(String deviceID, String songName);

}
