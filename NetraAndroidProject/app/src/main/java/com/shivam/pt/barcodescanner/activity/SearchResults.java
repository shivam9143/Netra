package com.shivam.pt.barcodescanner.activity;

/**
 * Created by Shivam on 7/28/2018.
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.shivam.pt.barcodescanner.R;
import com.shivam.pt.barcodescanner.adapter.StoreBarDetails;
import com.shivam.pt.barcodescanner.utils.Internetservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SearchResults extends AppCompatActivity {

    StoreBarDetails SD1;
    TextView pn,pt,price,bn,md,ed,sn,an,pid,des;
    JSONObject job2;
    private TextToSpeech tts;
    String s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    //speak("Welcome to South Supermarket . Say 1 to scan 2 to search ");

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
        pn = (TextView)findViewById(R.id.pname1);
        pt=(TextView)findViewById(R.id.ptype1);
        sn=(TextView)findViewById(R.id.shelfno1);
        an=(TextView)findViewById(R.id.aisle1);
        String value="";
        Bundle b=getIntent().getExtras();
        if(b!=null)
        {
            value=b.getString("Details");
        }
        Log.e("In new Activity",value);
        try {

            JSONArray jarray = null;
            try {
                Log.e("Inside try in productD","drfe");
                jarray = new JSONArray(value);
                job2 = jarray.getJSONObject(0);
                //SD1 = new StoreBarDetails(job2.getString("ProductName"),job2.getString("ProductType"),Integer.parseInt(job2.getString("Price")),job2.getString("BarcodeNumber"),job2.getString("ManufacturingDate"),job2.getString("ExpiryDate"),Integer.parseInt(job2.getString("ShelfNumber")),Integer.parseInt(job2.getString("AisleNumber")),Integer.parseInt(job2.getString("ProductId")),job2.getString("Description"));
                pn.setText(job2.getString("ProductName"));
                pt.setText(job2.getString("ProductType"));
                sn.setText(job2.getString("ShelfNumber"));
                an.setText(job2.getString("AisleNumber"));
                s="Product Name is "+job2.getString("ProductName")+" Product Type is "+ job2.getString("ProductType")+" Product is located in store Big Bazaar " +" in Shelf number "+ job2.getString("ShelfNumber")+" and aisle number  "+job2.getString("AisleNumber");
                Log.e("Strin S is ",s);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // this code will be executed after 2 seconds
                        speak(s);
                    }
                }, 2000);
                //speak(s);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        speak("Say 1 to exit the application.        Say 2 to go to main menu");
                        listen();
                    }
                }, 18000);


               /* Log.e("STOREDATA2", SD1.getpName());
                Log.e("Storedata3", SD1.getpType());
                Log.e("Storedata4", SD1.getPid()+"");*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        catch (Exception ex)
        {
        }

    }
    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(SearchResults.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                int d=Integer.parseInt(result.get(0));
                if(d==2)
                {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else if(d==1)
                {
                    Intent n=new Intent(SearchResults.this,Internetservice.class);
                    stopService(n);
                    finish();
                    moveTaskToBack(true);
                    return;
                }
            }
        }
    }
    private void speak(String text){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
