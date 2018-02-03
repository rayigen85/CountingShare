package de.raybit.countingshare;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rayig on 02.02.2018.
 */

public class ComUserRegister {

    public Map<String, Object> sendToServer(final String email, final String password){
        Map<String, Object> answer = new HashMap<String, Object>();

        try {
            String textparam = "user_email=" + URLEncoder.encode(email, "UTF-8");
            textparam += "&password=" + URLEncoder.encode(password, "UTF-8");

            URL scripturl = new URL("https", "raybit.de", 443, "/app/register_user.php");
            HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setFixedLengthStreamingMode(textparam.getBytes().length);

            OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
            contentWriter.write(textparam);
            contentWriter.flush();
            contentWriter.close();

            InputStream answerInputStream = connection.getInputStream();
            answer = getTextFromInputStream(answerInputStream);

            answerInputStream.close();
            connection.disconnect();
            return answer;

        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public Map<String, Object> getTextFromInputStream(InputStream is){
        JSONObject result_json;
        Map<String, Object> map = new HashMap<String, Object>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringbuilder = new StringBuilder();

        String aktuelleZeile;
        try {
            while ((aktuelleZeile = reader.readLine()) != null) {
                stringbuilder.append(aktuelleZeile);
                //stringbuilder.append("\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            result_json = new JSONObject(stringbuilder.toString());
            result_json.getBoolean("error");
            result_json.optString("message");


            map = jsonToMap(result_json);

            return map;
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return map;

    }


    public Map<String, Object> jsonToMap(JSONObject json) {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != null) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public Map<String, Object> toMap(JSONObject object){
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        try {
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = object.get(key);
                map.put(key, value);
            }
            return map;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
