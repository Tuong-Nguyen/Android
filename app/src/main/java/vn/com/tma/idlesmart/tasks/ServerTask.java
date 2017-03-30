package vn.com.tma.idlesmart.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.com.tma.idlesmart.BuildConfig;
import vn.com.tma.idlesmart.httpClient;
import vn.com.tma.idlesmart.params.UpdateTaskConfig;

/**
 * Run a task to send a request to server
 * First parameter: url to be created connection object
 * Second parameter: json will be writing to HttpURLConnection
 */
public class ServerTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "IdleSmart.ServerTask";
    String code;
    private Context context;

    public ServerTask() {
        this.code = BuildConfig.FLAVOR;
    }

    public void setContext(Context contextf) {
        this.context = contextf;
    }

    protected String doInBackground(String... arg0) {
        StringBuilder result = new StringBuilder();
        Log.i(TAG, "  ===> ServerTask::doInBackground..");
        try {
            URL url = new URL(arg0[0]);
            Log.i(TAG, "  dib: url=" + url);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/json");
            c.setDoOutput(true);
            c.setDoInput(true);
            c.connect();
            OutputStream os = c.getOutputStream();
            os.write(arg0[1].getBytes());
            os.flush();
            os.close();
            int httpResponseCode = c.getResponseCode();
            if (httpResponseCode == 200 || httpResponseCode == 201) {
                InputStream is = new BufferedInputStream(c.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    result.append(line);
                }
                Log.i(TAG, "      ServerTask result:" + result.toString());
                is.close();
            } else {
                Log.e(TAG, "      ServerTask Error:http response code:" + Integer.toString(httpResponseCode));
            }
            c.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "  ServerTask::IOException");
            e.printStackTrace();
        }
        Log.i(TAG, "  ===> ServerTask::done");
        return result.toString();
    }
}
