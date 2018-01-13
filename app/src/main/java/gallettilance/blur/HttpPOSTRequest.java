package gallettilance.blur;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPOSTRequest extends AsyncTask<String, Void, String> {

    public static final String REQUEST_METHOD = "POST";
    public static final int READ_TIMEOUT = 10000;
    public static final int CONNECTION_TIMEOUT = 10000;

    @Override
    protected String doInBackground(String... params){

        String stringUrl = params[0];
        String img = params[1];
        String label = params[2];
        String result = "";

        try {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            connection.connect();


        } catch(Exception e) {
            result = e.toString();
        }

        return result;
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}