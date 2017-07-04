package ca.dal.group5.jukefit.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.List;

import ca.dal.group5.jukefit.Model.Group;
import ca.dal.group5.jukefit.Model.Member;
import ca.dal.group5.jukefit.Model.Song;

/**
 * Created by lockhart on 2017-06-20.
 */

public class APIService implements APISpec {

    public static final String BASE_URL = "https://fitness-jukebox.herokuapp.com";
    private static final String BASE_GROUP = BASE_URL + "/group";
    private static final String SELECT_GROUP = BASE_GROUP + "/%s";
    private static final String SELECT_MEMBER = SELECT_GROUP + "/member/%s";
    private static final String BASE_MP3 = BASE_URL + "/%s/mp3";

    @Override
    public void createGroup(final RequestHandler<Group> handler) {
        try {
            URL url = new URL(BASE_GROUP);
            Request request = new Request(RequestType.POST, url, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        handler.callback(JSONParser.parseGroup(new JSONObject(result)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.callback(null);
                    }
                }
            });
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void groupInformation(String groupCode, final RequestHandler<Group> handler) {
        try {
            URL url = new URL(String.format(SELECT_GROUP, groupCode));
            Request request = new Request(RequestType.GET, url, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        handler.callback(JSONParser.parseGroup(new JSONObject(result)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.callback(null);
                    }
                }
            });
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinGroup(String groupCode, String name, String deviceID, final RequestHandler<Group> handler) {
        try {
            URL url = new URL(String.format(SELECT_GROUP, groupCode));
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("device_id", deviceID);
            Request request = new Request(RequestType.POST, url, body, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        handler.callback(JSONParser.parseGroup(new JSONObject(result)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.callback(null);
                    }
                }
            });
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateScore(String groupCode, String deviceID, int newScore, final RequestHandler<Member> handler) {
        try {
            URL url = new URL(String.format(SELECT_MEMBER, groupCode, deviceID));
            JSONObject body = new JSONObject();
            body.put("score", newScore);
            Request request = new Request(RequestType.PUT, url, body, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        handler.callback(JSONParser.parseMember(new JSONObject(result)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.callback(null);
                    }
                }
            });
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mp3List(String deviceID, final RequestHandler<List<Song>> handler) {
        try {
            URL url = new URL(BASE_MP3);
            Request request = new Request(RequestType.GET, url, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        JSONArray JSONObjList = new JSONArray();
                        JSONObjList.put(new JSONObject(result));
                        handler.callback(JSONParser.parseSongList(JSONObjList));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.callback(null);
                    }
                }
            });
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mp3Upload(String deviceID, File songToUpload, final RequestHandler<Song> handler) {
        try {
            URL url = new URL(String.format(BASE_MP3, deviceID));
            Request request = new Request(RequestType.POST, url, new RequestHandler<String>() {
                @Override
                public void callback(String result) {
                    if (result == null) {
                        handler.callback(null);
                        return;
                    }
                    try {
                        handler.callback(JSONParser.parseSong(new JSONObject(result)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.callback(null);
                    }
                }
            }, songToUpload);
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
