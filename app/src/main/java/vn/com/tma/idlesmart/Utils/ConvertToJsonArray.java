package vn.com.tma.idlesmart.Utils;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ntmhanh on 4/28/2017.
 */

public class ConvertToJsonArray {
    /**
     * Convert Datum To JsonArray
     * @param datum
     * @param datumLimitLine
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONArray convertDatumToJsonArray(String datum, int datumLimitLine) throws IOException, JSONException {

        InputStream inputStream = new ByteArrayInputStream(datum.getBytes());
        BufferedReader datumIn = new BufferedReader(new InputStreamReader(inputStream));

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        int i = 0;
        String line = datumIn.readLine();
        while (line != null){
            String timestamp;
            String datum_name;
            String datum_value;

            int indexOfWhiteSpace = line.indexOf(" ");
            if (indexOfWhiteSpace <= 0){
                return jsonArray;
            }else{
                timestamp = line.substring(0, indexOfWhiteSpace);
                int indexOfSlash = line.indexOf("\\");
                if (indexOfSlash <= 0){
                    return jsonArray;
                }else{
                    indexOfWhiteSpace++;
                    datum_name = line.substring(indexOfWhiteSpace, indexOfSlash);
                    indexOfSlash++;
                    datum_value = line.substring(indexOfSlash);
                    jsonObject = getDatumJsonObject(timestamp, datum_name, datum_value);
                    jsonArray.put(jsonObject);
                    i++;
                    if (i > datumLimitLine) {
                        break;
                    }
                    line = datumIn.readLine();
                }
            }
        }

        return jsonArray;
    }
    @NonNull
    private JSONObject getDatumJsonObject(String timestamp, String datum_name, String datum_value) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time_stamp", timestamp);
        jsonObject.put("datum_name", datum_name);
        jsonObject.put("datum_value", datum_value);
        return jsonObject;
    }

    /**
     * Convert Log To JsonArray
     * @param log
     * @param logLimitLine
     * @return
     * @throws IOException
     * @throws JSONException
     */

    public JSONArray convertLogToJsonArray(String log, int logLimitLine) throws IOException, JSONException {
        InputStream inputStream = new ByteArrayInputStream(log.getBytes());
        BufferedReader logIn = new BufferedReader(new InputStreamReader(inputStream));

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        int i = 0;
        String line = logIn.readLine();
        while (line != null){
            String timestamp;
            String event;
            String event_trigger;
            String event_comment;

            int indexOfWhiteSpace = line.indexOf(" ");
            if (indexOfWhiteSpace <= 0){
                return jsonArray;
            }else{
                timestamp = line.substring(0, indexOfWhiteSpace);
                int indexOfSlash = line.indexOf("\\");
                if (indexOfSlash <= 0){
                    return jsonArray;
                }else{
                    indexOfWhiteSpace++;
                    event = line.substring(indexOfWhiteSpace, indexOfSlash);
                    int lastIndexOfSlash = line.lastIndexOf("\\");
                    if (lastIndexOfSlash <= 0){
                        return jsonArray;
                    }else{
                        indexOfSlash++;
                        event_trigger =  line.substring(indexOfSlash, lastIndexOfSlash);
                        if (lastIndexOfSlash <= 0){
                            return jsonArray;

                        }else{
                            lastIndexOfSlash++;
                            event_comment = line.substring(lastIndexOfSlash);
                            jsonObject = getLogJSONObject(timestamp, event, event_trigger, event_comment);
                            jsonArray.put(jsonObject);
                            i++;
                            if (logLimitLine < i){
                                break;
                            }
                            line = logIn.readLine();
                        }
                    }

                }
            }
        }
        return jsonArray;
    }

    /**
     * Put Data into Log JsonObject
     * @param timestamp
     * @param event
     * @param event_trigger
     * @param event_comment
     * @return
     * @throws JSONException
     */
    @NonNull
    public JSONObject getLogJSONObject( String timestamp, String event, String event_trigger, String event_comment) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp",timestamp);
        jsonObject.put("event",event);
        jsonObject.put("event_trigger",event_trigger);
        jsonObject.put("event_comment",event_comment);
        return  jsonObject;
    }
}
