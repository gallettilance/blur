package gallettilance.blur;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class HttpPOSTRequest extends AsyncTask<String, Void, String> {

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 100000;
    private static final int CONNECTION_TIMEOUT = 100000;

    @Override
    protected String doInBackground(String... params){

        String stringUrl = params[0];
        String img = params[1];
        String img_label = params[2];
        String img_type = params[3];

        JSONObject myjson = new JSONObject();

        try {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            myjson.put("img", img);
            myjson.put("img_label", img_label);
            myjson.put("img_type", img_type);
            Log.e("MY PARAMETERS", myjson.toString());

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(myjson));
            writer.flush();
            writer.close();
            os.close();

            int responseCode=connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK | responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in=new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            }
            else {
                return "false : "+responseCode;
            }
        }
        catch(Exception e){
            return "Exception: " + e.getMessage();
        }
    }

    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("Result", result);
    }
}