package vn.com.tma.idlesmart.Utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.com.tma.idlesmart.MainActivity;


public class SendToGateway {
//    public byte[] send(JSONObject jsonObject);

    public int sendCscHeaderToGateway(JSONObject jsonObject,  byte[] parentArray) throws JSONException {

        if (MainActivity.DebugLog){
            Log.i("IdleSmart.UpdateGateway", "<sendCSCHeaderToGateway>");
        }
        List<String> feature;

        feature = new ArrayList<String>();
        feature.add("start");
        feature.add("size");
        feature.add("tra");
        feature.add("signature");
        feature.add("crc_1");
        feature.add("crc_2");
        feature.add("crc_3");
        feature.add("block_count");
        parentArray[0] = (byte) (jsonObject.getInt("format") & 255);
        int i = 1;
        if (i < parentArray.length){
            for (int j = 0; j < feature.size(); j++){
                if (feature.get(j) == "block_count"){
                    if (hexStringToByteArray(jsonObject.getString("block_count")).length == 1){
                        parentArray[i] = 0;
                        i++;
                        byte[] childArray = hexStringToByteArray(jsonObject.getString(feature.get(j)));
                        int l = 0;
                        while (childArray.length > l){
                            parentArray[i] = childArray[l];
                            l++;
                            i++;
                        }

                        int h = 0;
                        while (jsonObject.getString("version").getBytes().length < 10){
                            parentArray[i] = jsonObject.getString("version").getBytes()[h];
                            h++;
                            i++;
                        }
                    }
                }
                byte[] childArray = hexStringToByteArray(jsonObject.getString(feature.get(j)));
                int k = 0;
                while (childArray.length > k){
                    parentArray[i] = childArray[k];
                    k++;
                    i++;
                }
            }
        }
        return i;

    }
    public byte[] hexStringToByteArray(String str) {
        String TAG = "IdleSmart.UpdateGateway";
        try {
            String s;
            if (str.charAt(0) == '0' && str.charAt(1) == 'x') {
                s = str.substring(2);
            } else {
                s = str;
            }
            int len = s.length();
            byte[] bArr = new byte[(len / 2)];
            for (int i = 0; i < len; i += 2) {
                bArr[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            }
            return bArr;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

}
