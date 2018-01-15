package gallettilance.blur;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
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
    private static final int READ_TIMEOUT = 1000000;
    private static final int CONNECTION_TIMEOUT = 1000000;
    private final String USER_AGENT = "Chrome/63.0.3239.132";

    @Override
    protected String doInBackground(String... params){

        String stringUrl = params[0];
        String img = params[1];
        String img_label = params[2];
        String img_type = params[3];

        JSONObject myjson = new JSONObject();

        try {
            URL myUrl = new URL(stringUrl);
            HttpsURLConnection connection =(HttpsURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            myjson.put("img_label", img_label);
            myjson.put("img_type", img_type);
            myjson.put("img", img);
            Log.e("MY PARAMETERS", myjson.toString());

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(getPostDataString(myjson));
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.d("ResponseCode", Integer.toString(connection.getResponseCode()));
            Log.d("ReponseMessage", connection.getResponseMessage());
            Log.d("Response", response.toString());

            connection.disconnect();
            return response.toString();
            /*
            int responseCode=connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK | responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                if ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                connection.disconnect();
                return sb.toString();
            } else {
                connection.disconnect();
                return "false : "+responseCode;
            }
            */

        }

        catch(Exception e) {
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

            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        Log.d("result.toString()", result.toString());
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("Result", result);
    }
}