package com.shivam.pt.barcodescanner.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.shivam.pt.barcodescanner.R;
import com.shivam.pt.barcodescanner.adapter.StoreBarDetails;
import com.shivam.pt.barcodescanner.database.DatabaseHelper;
import com.shivam.pt.barcodescanner.fragment.BarcodeFragment;
import com.shivam.pt.barcodescanner.fragment.LicenseFragment;
import com.shivam.pt.barcodescanner.model.Product;
import com.shivam.pt.barcodescanner.utils.ConnectionDetector;
import com.shivam.pt.barcodescanner.utils.Internetservice;
import com.shivam.pt.barcodescanner.utils.JsonParser;

import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BarcodeFragment.ScanRequest {

    private Context context ;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static final String BARCODE_KEY = "BARCODE";
    private String barcodeResult;
    private final String TAG = MainActivity.class.getSimpleName() ;
    private final int MY_PERMISSION_REQUEST_CAMERA = 1001;
    private ItemScanned itemScanned ;
    ProgressDialog dialog2;
    ArrayList<StoreBarDetails> arraylist = new ArrayList<StoreBarDetails>();
    ListAdapter adapter;
    JsonParser jsonparser=new JsonParser();
    String resultedData3,h2,resultedData4;
    private TextToSpeech tts;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    String listened="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

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
                    finish();
                    moveTaskToBack(true);
                    return;
                }
            }, 4000);

        }
        Intent n=new Intent(MainActivity.this,Internetservice.class);
        startService(n);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Say one to scan new product after a peep. Say two to search a product ");
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        }, 8000);

    }
    private void aa()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        }, 8000);
    }
    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MainActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                int d=0;
                if(result.get(0).length()==1)
                        d=Integer.parseInt(result.get(0));
                    if(d==1)
                    {
                        speak(".  Place   your    rear   camera   near   the   barcode of the   product");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scanBarcode();
                            }
                        }, 3000);
                    }
                    else if(d==2)
                    {
                        speak("Say the name of the product after a peep");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listen();
                            }
                        }, 3000);

                    }
                    else
                    {
                        listened=result.get(0);
                        LoadJS3 ll=new LoadJS3();
                        ll.execute("");
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

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BarcodeFragment(), "Barcode Scanner");
       // adapter.addFragment(new ProductListFragment(), "Scan Item");
        viewPager.setAdapter(adapter);
    }

    public String getScanTime() {
     DateFormat timeFormat = new SimpleDateFormat("hh:mm a" , Locale.getDefault());
        return  timeFormat.format(new Date());
    }

    public String getScanDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    public void scanBarcode() {
        /** This method will listen the button clicked passed form the fragment **/
         checkPermission();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.item_share:
                openShare();
                break;
            case R.id.item_license:
                openLisence();
                break;
        }

        return super.onOptionsItemSelected(item);
    }




    private void openShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        String appLink = "https://github.com/shivam9143/Netra";
        sharingIntent.setType("text/plain");
        String shareBodyText = "Check Out The Cool Barcode Reader App \n Link: "+appLink +" \n" +
                " #Barcode #Android";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Barcode Reader Android App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    private void openLisence() {
        LicenseFragment licensesFragment = new LicenseFragment();
        licensesFragment.show(getSupportFragmentManager().beginTransaction(), "dialog_licenses");
    }

    private void showDialog(final String scanContent, final String currentTime, final String currentDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(scanContent)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                databaseHelper.addProduct(new Product(scanContent,currentTime,currentDate));
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(1);


            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "Not Saved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are You Sure? ")
                .setTitle(R.string.exit_title);
        builder.setPositiveButton(R.string.ok_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                  MainActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                      dialog.dismiss();
            }
        });

        builder.show();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG , getResources().getString(R.string.camera_permission_granted));
            startScanningBarcode();
        } else {
            requestCameraPermission();

        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                    ActivityCompat.requestPermissions(MainActivity.this,  new String[] {Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);

        } else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void startScanningBarcode() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(MainActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode.rawValue;
                        LoadJS2 js=new LoadJS2();
                        js.execute("");

                     //   showDialog(barcode.rawValue+resultedData3 , getScanTime(),getScanDate());
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MY_PERMISSION_REQUEST_CAMERA && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanningBarcode();
        } else {
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.sorry_for_not_permission), Snackbar.LENGTH_SHORT)
                    .show();
        }

    }


    public interface  ItemScanned{
        void itemUpdated();
    }


    private class LoadJS2 extends AsyncTask<String, String, String>
    {


        @Override
        protected void onPreExecute() {
            dialog2=new ProgressDialog(MainActivity.this);
            dialog2.setMessage("Loading...");
            dialog2.show();
        }
        @Override
        protected void onPostExecute(String r)
        {
            Log.e("On post Execute","hello");
            StoreBarDetails SD1;
            //Context con1;
            if ( dialog2!=null && dialog2.isShowing() )
            {
                Log.e("dialogue dismissed","drfc");
                dialog2.dismiss();
                dialog2=null;

            }
            try {

              /* JSONObject job2;

                JSONArray jarray = null;*/

                    /*jarray = new JSONArray(r);
                    job2 = jarray.getJSONObject(0);
                        SD1 = new StoreBarDetails(job2.getString("ProductName"),job2.getString("ProductType"),Integer.parseInt(job2.getString("Price")),job2.getString("BarcodeNumber"),job2.getString("ManufacturingDate"),job2.getString("ExpiryDate"),Integer.parseInt(job2.getString("ShelfNumber")),Integer.parseInt(job2.getString("AisleNumber")),Integer.parseInt(job2.getString("ProductId")),job2.getString("Description"));
                    Log.e("STOREDATA2", SD1.getpName());
                    Log.e("Storedata3", SD1.getpType());
                    Log.e("Storedata4", SD1.getPid()+"");*/
                    Intent i =new Intent(MainActivity.this,Productdetails.class);
                    i.putExtra("Details",resultedData3);
                    startActivity(i);

            }
            catch (Exception ex)
            {
            }
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try{
                URL url2 = new URL("http://www.shivam.somee.com/Default.asmx/getProductdetails?str="+barcodeResult+"");
                h2=url2.toString();
                Log.e("Rrrrrrrrrrrr",h2);
                resultedData3 = jsonparser.getJSON(h2);
                Log.e("Rrrrrrrrrr",resultedData3);
            }
            catch(Exception ex)
            {
                resultedData3 = "There's an error, that's all I know right now.33333333333. :(";
            }
            return resultedData3;
        }
    }


    private class LoadJS3 extends AsyncTask<String, String, String>
    {


        @Override
        protected void onPreExecute() {
            dialog2=new ProgressDialog(MainActivity.this);
            dialog2.setMessage("Loading...");
            dialog2.show();
        }
        @Override
        protected void onPostExecute(String r)
        {
            Log.e("On post Execute","hello");
            StoreBarDetails SD1;
            //Context con1;
            if ( dialog2!=null && dialog2.isShowing() )
            {
                Log.e("dialogue dismissed","drfc");
                dialog2.dismiss();
                dialog2=null;

            }
            try {

              /* JSONObject job2;

                JSONArray jarray = null;*/

                    /*jarray = new JSONArray(r);
                    job2 = jarray.getJSONObject(0);
                        SD1 = new StoreBarDetails(job2.getString("ProductName"),job2.getString("ProductType"),Integer.parseInt(job2.getString("Price")),job2.getString("BarcodeNumber"),job2.getString("ManufacturingDate"),job2.getString("ExpiryDate"),Integer.parseInt(job2.getString("ShelfNumber")),Integer.parseInt(job2.getString("AisleNumber")),Integer.parseInt(job2.getString("ProductId")),job2.getString("Description"));
                    Log.e("STOREDATA2", SD1.getpName());
                    Log.e("Storedata3", SD1.getpType());
                    Log.e("Storedata4", SD1.getPid()+"");*/
                Intent i =new Intent(MainActivity.this,SearchResults.class);
                i.putExtra("Details",resultedData4);
                startActivity(i);

            }
            catch (Exception ex)
            {
            }
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try{
                String li = URLEncoder.encode(listened, "utf-8");
                URL url2 = new URL("http://www.shivam.somee.com/Default.asmx/searchProduct?str="+li+"");
                h2=url2.toString();
                Log.e("Rr44",h2);
                resultedData4 = jsonparser.getJSON(h2);
                Log.e("R444",resultedData4);
            }
            catch(Exception ex)
            {
                resultedData4 = "There's an error, that's all I know right now.33333333333. :(";
            }
            return resultedData4;
        }
    }
}
