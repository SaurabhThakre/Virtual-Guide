package com.virtual_guide.virtualguide;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GeoQueryEventListener {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentUser;
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    private List<LatLng> dangerousArea;

    Button search, moreImages, seeMap, addInfo, moreInfo, moreMore;
    LinearLayout seeMore;
    TextView title, desc, desc2;
    ImageView img;
    EditText landmark;
    ProgressDialog p;
    String url;
    String place;
    String placeTitle;
    String titles, imgSrc;
    Element link, head, image1, para1, para2;
    InputStream input;
    Bitmap bitmap;
    ScrollView scrollView;
    RelativeLayout relative;
    int flagText = 0;
    Element[] para3 = new Element[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_home);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside permission");
                        buildLocationRequest();
                        buildLocationCallback();
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);

                        //  Obtain the SupportMapFragment and get notified when the map is ready to be used.
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(HomeActivity.this);
                        mapFragment.getView().setVisibility(View.GONE);

                        initArea();

                        settingGeoFire();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(HomeActivity.this, "you must enable permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

        landmark = findViewById(R.id.landmark);
        title = findViewById(R.id.title);
        img = findViewById(R.id.img);
        desc = findViewById(R.id.description);
        desc2 = findViewById(R.id.description2);
        search = findViewById(R.id.search);
        scrollView = findViewById(R.id.scrollView);
        relative = findViewById(R.id.buttons);
        moreImages = findViewById(R.id.butt1);
        seeMap = findViewById(R.id.butt2);
        moreInfo = findViewById(R.id.butt3);
        addInfo = findViewById(R.id.butt4);
        seeMore = findViewById(R.id.Wiki);
        moreMore = findViewById(R.id.moreMore);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scrollView.setBackgroundResource(R.drawable.white);
                place = landmark.getText().toString();
                url = "https://en.wikipedia.org/wiki/" + place;
                try{
                    Content content = new Content();
                    content.execute();
                }
                catch (Exception e){
                    Toast.makeText(HomeActivity.this, "Invalid Landmark/Monument name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try{
            Intent intent = getIntent();
            String titleCamera = intent.getStringExtra("titleCamera");
            if(!titleCamera.isEmpty()){
                scrollView.setBackgroundResource(R.drawable.white);
                place = titleCamera;
                url = "https://en.wikipedia.org/wiki/" + place;
                Content content = new Content();
                content.execute();
            }
        }
        catch(Exception e){
            Log.d("^&%^&%Null",e.toString());
        }



        moreImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moreImages = new Intent(HomeActivity.this, MoreImages.class);
                moreImages.putExtra("Placename", placeTitle);
                startActivity(moreImages);
            }
        });

        addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String user = intent.getStringExtra("user");
                Log.d("%&%%&Username",user);
                String id = intent.getStringExtra("id");
                Intent intoAddInfo = new Intent(HomeActivity.this, AddInfo.class);
                intoAddInfo.putExtra("user", user);
                intoAddInfo.putExtra("id", id);
                intoAddInfo.putExtra("landmark", placeTitle);
                startActivity(intoAddInfo);
            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String user = intent.getStringExtra("user");
                String id = intent.getStringExtra("id");
                Intent intoMoreInfo = new Intent(HomeActivity.this, ViewInfo.class);
                intoMoreInfo.putExtra("user", user);
                intoMoreInfo.putExtra("id", id);
                intoMoreInfo.putExtra("landmark", placeTitle);
                startActivity(intoMoreInfo);
            }
        });

        seeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intoMap = new Intent(HomeActivity.this, MapsActivity.class);
                intoMap.putExtra("landmark", placeTitle);
                startActivity(intoMap);
            }
        });

    }

    public void seeMoreInfo(View view) {
        flagText = 1;
        moreMore.setVisibility(View.INVISIBLE);
        Content content = new Content();
        content.execute();
    }


    //To fetch and display details of monument/landmarks

    private class Content extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(HomeActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            p.setMessage("Please wait...Fetching details");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        protected Void doInBackground(Void... voids) {
            int i = 1;
            try {
                Document document = Jsoup.connect(url).get();
                titles = document.title();
                head = document.select("h1").first();
                placeTitle = head.text();
                while(i < 10){
                        if(document.select("p").get(i).text().isEmpty()){
                            i++;
                        }
                        else{
                            break;
                        }
                }
                para1 = document.select("p").get(i);
                para2 = document.select("p").get(i + 1);
                if(flagText == 1){
                    for(int k = 2; k < 100; k++){
                        try{
                            if(!document.select("p").get(i + k).text().isEmpty()){
                                para3[k-2] = document.select("p").get(i + k);
                            }
                            else{
                                break;
                            }
                        }
                        catch(Exception e){

                        }
                    }
                }
                link = document.select("table").first();
                image1 = link.select("img").first();
                imgSrc = image1.absUrl("src");
                input = new java.net.URL(imgSrc).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            title.setText(placeTitle);
            img.setImageBitmap(bitmap);
            desc.setText(para1.text());
            desc2.setText(para2.text());
            if(flagText == 1){
                for(int j = 0; j < 98; j++){
                    if(para3[j] != null){
                        TextView descView = new TextView(HomeActivity.this);
                        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        descParams.setMargins(0, dpToPX(20), 0, 0);
                        descView.setLayoutParams(descParams);
                        descView.setTextColor(Color.parseColor("black"));
//                        descView.setTextSize(18);
                        descView.setTypeface(null, Typeface.BOLD);
//                        descView.setFontFeatureSettings("arial");
                        descView.setText(para3[j].text());
                        seeMore.addView(descView);
                    }
                    else{
                        break;
                    }
                }

            }
            p.dismiss();
            if (flagText == 0){
                moreMore.setVisibility(View.VISIBLE);
            }
            relative.setVisibility(View.VISIBLE);
        }
    }


    public int dpToPX(int dp) {
        Resources r = this.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

        return px;
    }







    private void initArea() {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside init method");

        dangerousArea=new ArrayList<>();
        dangerousArea.add(new LatLng(21.1801020,79.0609900));
        dangerousArea.add(new LatLng(21.1801020,79.16));
        dangerousArea.add(new LatLng(21.1801020,79.26));
        dangerousArea.add(new LatLng(21.1801020,79.36));

        dangerousArea.add(new LatLng(21.299202,79.074669));
        dangerousArea.add(new LatLng(21.178952,79.041284));
        dangerousArea.add(new LatLng(21.217915,79.078138));
        dangerousArea.add(new LatLng(21.1801020,79.46));

    }

    private void settingGeoFire() {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside setting geofire");

        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire=new GeoFire(myLocationRef);


    }

    private void buildLocationCallback() {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside building location call back");

        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                if(mMap!=null) {

                    if (currentUser != null)
                        currentUser.remove();//remove old marker
                    currentUser = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude()))
                            .title("you"));
                    //move camera to this position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            currentUser.getPosition(),12.0f));

                    geoFire.setLocation("you", new GeoLocation(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (currentUser != null)
                                currentUser.remove();//remove old marker
                            currentUser = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(locationResult.getLastLocation().getLatitude(),
                                            locationResult.getLastLocation().getLongitude()))
                                    .title("you"));
                            //move camera to this position
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    currentUser.getPosition(),12.0f));
                        }
                    });
                }

            }
        };

    }

    private void buildLocationRequest() {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside build location request");

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside map ready");

        mMap = googleMap;


        mMap.getUiSettings().setZoomControlsEnabled(true);

        if(fusedLocationProviderClient!=null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            //add circle for dangerous area
            for(LatLng latLng:dangerousArea){
                mMap.addCircle(new CircleOptions().center(latLng)
                        .radius(500)
                        .strokeColor(Color.BLUE)
                        .fillColor(0x220000FF) //it is transparent code
                        .strokeWidth(5.0f)
                );

                //create GeoQuery when in dangerous location
                GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),0.5f);
                geoQuery.addGeoQueryEventListener(HomeActivity.this);

            }

        }
    }
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }


    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside on key entered");

        sendNotification("virtualGuide",String.format("checkout VirtualGuide Application to explore nearby places",key));
    }

    @Override
    public void onKeyExited(String key) {
        sendNotification("virtualGuide",String.format("is leave the dangerous area",key));

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        sendNotification("virtualGuide",String.format("is move within the dangerous area",key));

    }


    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside on geo query error");

        Toast.makeText(this,""+error.getMessage(),Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String title, String content) {
        System.out.println(",,,,,,,,,,,,,,,,,,,,.....................................inside sending notification");

        String NOTIFICATION_CHANNEL_ID="geofire_multiple_location";


        NotificationManager notificationManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,"my Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            //config
            notificationChannel.setDescription("channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));


        Notification notification=builder.build();

        notificationManager.notify(new Random().nextInt(),notification);
    }


}