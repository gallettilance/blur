package gallettilance.blur;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import 	java.nio.charset.StandardCharsets;

public class HttpPOSTRequest extends AsyncTask<String, Void, Void> {

    public static final String REQUEST_METHOD = "POST";
    public static final int READ_TIMEOUT = 10000;
    public static final int CONNECTION_TIMEOUT = 10000;

    @Override
    protected Void doInBackground(String... params){

        String stringUrl = params[0];
        String img = params[1];
        String img_label = params[2];
        String img_type = params[3];

        JSONObject myjson = new JSONObject();

        //OutputStream out;

        try {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoOutput(true);

            myjson.put("img", img);
            myjson.put("img_label", img_label);
            myjson.put("img_type", img_type);

            //connection.addRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.addRequestProperty("Content-Type", "application/json");

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(myjson.toString());
            wr.flush();
            wr.close();

            //OutputStream os = connection.getOutputStream();
            //os.write(myjson.toString().getBytes("UTF-8"));
            //os.close();


            //connection.setRequestProperty("img", img);
            //connection.setRequestProperty("label", label);

            //out = new BufferedOutputStream(connection.getOutputStream());

            //BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));


            //out.flush();
            //out.close();


            //connection.addRequestProperty("img", img);
            //connection.addRequestProperty("label", label);
            //connection.connect();

        } catch(Exception e) {
            System.out.print(e);
        }
        return null;
    }

    protected void onPostExecute(Void result){
        super.onPostExecute(result);
    }
}