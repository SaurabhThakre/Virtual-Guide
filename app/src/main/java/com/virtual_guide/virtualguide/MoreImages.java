package com.virtual_guide.virtualguide;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;

public class MoreImages extends AppCompatActivity {
//
    ProgressDialog p;
    String url;
    String[] imgSrc;
    Element link, para1, para2;
    Element[] image1;
    InputStream[] input;
    Bitmap[] bitmap;
    ScrollView scrollView;
    LinearLayout imageList;
//
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
//        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.moreimages);

        image1 = new Element[40];
        imgSrc = new String[40];
        input = new InputStream[40];
        bitmap = new Bitmap[40];

        scrollView = findViewById(R.id.scrollView);
        imageList = findViewById(R.id.imageList);

        Intent intent = getIntent();
        String str = intent.getStringExtra("Placename");

        scrollView.setBackgroundResource(R.drawable.back1);
        url = "https://unsplash.com/s/photos/" + str;
        MoreImages.Content content = new MoreImages.Content();
        content.execute();

    }

    private class Content extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(MoreImages.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            p.setMessage("Please wait...Fetching details");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        protected Void doInBackground(Void... voids) {
            int i = 0;
            try {
                Document document = Jsoup.connect(url).get();

                for(int j = 10; j < 40; j++){
                    try{
                        image1[i] = document.select("img").get(j);
                        imgSrc[i] = image1[i].absUrl("src");
                        input[i] = new java.net.URL(imgSrc[i]).openStream();
                        bitmap[i] = BitmapFactory.decodeStream(input[i]);
                        if(i < 20){
                            i++;
                            System.out.println("~~~~~~~~i(+)~~~~~~~"+i);
                        }
                        else{
                            break;
                        }
                    }
                    catch (Exception e){
                        System.out.println("********imageerror***********");
                        System.out.println("~~~~~~~~i(-)~~~~~~~"+i);
                    }
                    System.out.println("~~~~~~~~j(+)~~~~~~~"+j);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int k = 0;
            int i  = 0;
            while(i < 20) {
                try {
                    if(bitmap[k] == null){
                        break;
                    }
                    ImageView imageView = new ImageView(MoreImages.this);
                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(dpToPX(350), dpToPX(250));
                    imgParams.gravity = Gravity.CENTER_HORIZONTAL;
                    imgParams.setMargins(0, dpToPX(20), 0, 0);
                    imageView.setLayoutParams(imgParams);
                    imageView.setBackground(ContextCompat.getDrawable(MoreImages.this, R.drawable.addimg));
                    imageView.setImageBitmap(bitmap[k]);
                    imageList.addView(imageView);
                    i++;
                    System.out.println("~~~~~Post~~~i(+)~~~~~~~"+i);
                }
                catch(Exception e){

                }
                if(k!=20) {
                    k++;
                }
                else {
                    break;
                }
                System.out.println("~~~~~Post~~~k(+)~~~~~~~"+k);
            }

            p.dismiss();
            scrollView.setBackgroundResource(R.drawable.navbar);
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

}
