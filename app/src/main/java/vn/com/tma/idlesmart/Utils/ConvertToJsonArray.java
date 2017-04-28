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
     * @param datumStr
     * @param datumMaxLine
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONArray convertDatumToJsonArray(String datumStr, int datumMaxLine) throws IOException, JSONException {

        InputStream inputStream = new ByteArrayInputStream(datumStr.getBytes());
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
                    datum_name = line.substring(indexOfWhiteSpace, indexOfSlash);
                    int lastIndexOfSlash = line.lastIndexOf("\\");
                    if (lastIndexOfSlash <= 0){
                        return  jsonArray;
                    }else{
                        datum_value = line.substring(indexOfSlash, lastIndexOfSlash);
                        jsonObject = getDatumJsonObject(timestamp, datum_name, datum_value);
                        jsonArray.put(jsonObject);
                        i++;
                        if (i > datumMaxLine) {
                            break;
                        }
                        line = datumIn.readLine();
                    }
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
}
