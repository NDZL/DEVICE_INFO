package deviceinfo.ndzl.com;

import android.app.IntentService;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

import static android.content.ContentValues.TAG;


public class SpeakerService extends IntentService implements TextToSpeech.OnInitListener {

    TextToSpeech tts;
    String WORDS_TO_SAY="HELLO WORLD";
    String LANGUAGE="ITA";

    public static final Integer lock_TTS=0;

    public void initTTSandSpeak(String words) {
        Log.i(TAG, "initTTS-1 " + words);

        tts = new TextToSpeech(getApplicationContext(), SpeakerService.this);

        Log.i(TAG, "initTTS-FINE " + words);
    }


    private void parlaTesto(final String testo) {
        Log.i(TAG, "speaking " + testo);
        tts.speak(testo, TextToSpeech.QUEUE_FLUSH, null, "" + this.hashCode());
    }


    @Override
    public void onInit(int status) {
        Log.i(TAG, "ONINIT-1");


        if(LANGUAGE.equals("ITA"))
            tts.setLanguage(Locale.ITALY);
        else
            tts.setLanguage(Locale.US);


        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
            }

            @Override
            public void onError(String utteranceId) {
            }
        });

        parlaTesto(WORDS_TO_SAY);

        Log.i(TAG, "ONINIT-2");
    }

    public SpeakerService(String name) {
        super(name);
    }

    public SpeakerService() {
        super("");
    }



    @Override
    protected void onHandleIntent(Intent workIntent) {
        WORDS_TO_SAY = workIntent.getStringExtra("WORDS_TO_SAY");
        LANGUAGE = workIntent.getStringExtra("LANGUAGE");

        Thread _t = new Thread(){
            @Override
            public void run() {
                super.run();
                initTTSandSpeak(WORDS_TO_SAY);
            }
        };
        _t.start();

        }


    }

