package com.shivam.pt.barcodescanner.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Shivam on 7/27/2018.
 */

public class Internetservice extends Service {
    Timer t = new Timer();
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    TextToSpeech tts;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    private void speak(String text){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Say one to scan new product. Place   your    rear   camera   near   the   barcode of the   product ");
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                      if (!isInternetPresent)
                                      {
                                                  speak("Internet Connection is not present. Please connect to internet to continue.!");
                                                 /* finish();
                                                  moveTaskToBack(true);*/
                                                  return;
                                      }
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                0,
                //Set the amount of time between each execution (in milliseconds)
                5000);

        if (!isInternetPresent)
        {
            // Log.e("innnnnnnn", "innnnnn");
            // Internet Connection is Present
            // make HTTP requests
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speak("Internet Connection is not present. Please connect to internet to continue. Exitting the application!");
                    /*finish();
                    moveTaskToBack(true);*/
                    return;
                }
            }, 4000);

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
