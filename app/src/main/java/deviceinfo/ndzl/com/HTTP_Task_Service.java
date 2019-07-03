package deviceinfo.ndzl.com;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


class HTTP_Task_Service extends AsyncTask<String, Void, Void> {

    protected Void doInBackground(String... passed) {

        try {
            StringBuilder tokenUri=new StringBuilder(passed[0]);

            String url = passed[0];
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36");
            con.setRequestProperty("Accept-Language", "UTF-8");

            con.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(tokenUri.toString());
            outputStreamWriter.flush();


            int responseCode = con.getResponseCode();
            //System.out.println("Response Code : " + responseCode);

        } catch (IOException e) {

           // e.printStackTrace();
        }



        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

}

