package ca.dal.group5.jukefit.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Model.Song;

/**
 * Created by lockhart on 2017-06-20.
 */

public class JSONParser {

    static Group parseGroup(JSONObject group) throws JSONException {
        return new Group(
                group.getString("code"),
                parseMemberList(group.getJSONArray("members")),
                group.isNull("currentSong") ? null : parseSong(group.getJSONObject("currentSong")),
                group.isNull("nextSong") ? null : parseSong(group.getJSONObject("nextSong"))
        );
    }

    static List<Member> parseMemberList(JSONArray members) throws JSONException {
        ArrayList<Member> results = new ArrayList<Member>();
        for (int i = 0; i < members.length(); i++) {
            JSONObject curr = members.getJSONObject(i);
            results.add(parseMember(curr));
        }
        return results;
    }

    static Member parseMember(JSONObject member) throws JSONException {
        return new Member(
                member.getString("name"),
                member.getString("device_id"),
                member.getInt("score")
        );
    }

    static List<Song> parseSongList(JSONArray songs) throws JSONException {
        ArrayList<Song> results = new ArrayList<Song>();
        for (int i = 0; i < songs.length(); i++) {
            JSONObject curr = songs.getJSONObject(i);
            results.add(parseSong(curr));
        }
        return results;
    }

    static Song parseSong(JSONObject song) throws JSONException {
        if (song.has("name")) {
            return new Song(
                    song.getString("name"),
                    song.getString("url")
            );
        } else {
            return new Song(
                    song.getString("url"),
                    new Date(song.getString("end_time"))
            );
        }
    }
}
