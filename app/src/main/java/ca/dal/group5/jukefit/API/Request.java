package ca.dal.group5.jukefit.API;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Request extends AsyncTask<Void, Void, String> {

    private RequestType method;
    private URL url;
    private JSONObject body = null;
    private HashMap<String, String> headers = null;
    private RequestHandler<String> handler;
    private Boolean isFile = false;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    File SongFile;

    private ProgressDialog pDialog = null;

    Request(RequestType method, URL url, JSONObject body, HashMap<String, String> headers, RequestHandler<String> handler) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.headers = headers;
        this.handler = handler;
    }

    Request(RequestType method, URL url, JSONObject body, RequestHandler<String> handler) {
        this.method = method;
        this.url = url;
        this.body = body;
        this.handler = handler;
    }

    Request(RequestType method, URL url, RequestHandler<String> handler) {
        this.method = method;
        this.url = url;
        this.handler = handler;
    }

    Request(RequestType method, URL url, RequestHandler<String> handler, File Song) {
        this.method = method;
        this.url = url;
        this.handler = handler;
        isFile = true;
        SongFile = Song;
    }

    public void setProgressDialog(Activity activity, String dialogText) {
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(dialogText);
        pDialog.setCancelable(false);
    }

    private InputStream getRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.addRequestProperty(key, headers.get(key));
            }
        }

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();
        return connection.getInputStream();
    }

    private InputStream postRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        if (!isFile) {
            connection.setRequestProperty("Content-Type", "application/json");
        } else if (isFile) {
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(SongFile);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"" + lineEnd);
            dos.writeBytes(lineEnd);
            int bytesRead, bytesAvailable, bufferSize;
            bytesAvailable = fileInputStream.available();
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // close streams
            Log.d("Debug", "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        }

        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.addRequestProperty(key, headers.get(key));
            }
        }

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();

        if (body != null) {
            DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
            printout.writeBytes(body.toString());
            printout.flush();
            printout.close();
        }

        return connection.getInputStream();
    }

    private InputStream putRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");

        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.addRequestProperty(key, headers.get(key));
            }
        }

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();

        DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
        printout.writeBytes(body.toString());
        printout.flush();
        printout.close();

        return connection.getInputStream();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (pDialog != null) {
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        InputStream is;
        try {
            switch (this.method) {
                case GET:
                    is = getRequest();
                    break;
                case POST:
                    is = postRequest();
                    break;
                case PUT:
                    is = putRequest();
                    break;
                default:
                    return null;
            }
            return inputStreamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pDialog != null) {
            pDialog.dismiss();
        }
        handler.callback(result);
    }

    private static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }
}
