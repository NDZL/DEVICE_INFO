package deviceinfo.ndzl.com;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HTTP_GET extends AsyncTask<String , Void ,String> {
    String server_response;
    URL url;
    boolean bDoLoop = false;


    @Override
    protected String doInBackground(String... strings) {


        HttpURLConnection urlConnection = null;

        try {
            url = new URL(strings[0]);
            bDoLoop = strings[1].equalsIgnoreCase("loop") ? true : false;
            urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(urlConnection.getInputStream());
                //Log.v("CatalogClient", server_response);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.e("Response", "" + server_response);
        if(server_response.contains("speech")){
            String[] whattosay = server_response.split("=");
            if(whattosay.length==2){
                serviceTalk(whattosay[1]);
            }
        }
        if(server_response.contains("assignedchannel")){
            String[] _ac = server_response.split("=");
            String assigned_channel="";
            if(_ac.length==2){
                assigned_channel = _ac[1];
            }
            String _url ="https://clouddumplogger.appspot.com/cmb?wait-on-channel="+assigned_channel;
            new HTTP_GET().execute(_url, "loop");
        }

        if(bDoLoop){
            new HTTP_GET().execute(url.toString(), "loop");
        }

    }

    void serviceTalk(String words){
        /*
        service_is.putExtra("WORDS_TO_SAY", words);
        service_is.putExtra("LANGUAGE", "ITA");

        main_context.startService(service_is);
        */
        SpeakerService.parlaTesto(words);
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}



