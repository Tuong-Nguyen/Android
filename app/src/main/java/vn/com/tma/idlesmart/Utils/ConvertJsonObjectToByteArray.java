package vn.com.tma.idlesmart.Utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import vn.com.tma.idlesmart.MainActivity;


public class ConvertJsonObjectToByteArray {
    /**
     * Convert CscHeader Object to Byte Array
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public List<Byte> convertCscHeaderObjectToByteArray(JSONObject jsonObject) throws JSONException {
        List<Byte> parentArray = new ArrayList<>();
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
        if (jsonObject.has("format")){
            parentArray.add((byte) (jsonObject.getInt("format") & 255));
            for (int j = 0; j < feature.size(); j++){
                if (feature.get(j) == "block_count"){
                    if (hexStringToByteArray(jsonObject.getString("block_count")).length == 1){

                        BigInteger bigInt = BigInteger.valueOf(0);
                        parentArray.add(bigInt.byteValue());

                        byte[] childArray = hexStringToByteArray(jsonObject.getString(feature.get(j)));
                        int l = 0;
                        while (childArray.length > l){
                            parentArray.add(childArray[l]);
                            l++;
                        }

                        int h = 0;
                        while (jsonObject.getString("version").getBytes().length < 10){
                            parentArray.add(jsonObject.getString("version").getBytes()[h]);
                            h++;
                        }
                    }

                    return parentArray;
                }
                byte[] childArray = hexStringToByteArray(jsonObject.getString(feature.get(j)));
                int k = 0;
                while (childArray.length > k){
                    parentArray.add(childArray[k]);
                    k++;

                }
            }

        }

        return parentArray;
    }

    /**
     * Convert String hex to byte Array
     * @param str
     * @return
     */
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
