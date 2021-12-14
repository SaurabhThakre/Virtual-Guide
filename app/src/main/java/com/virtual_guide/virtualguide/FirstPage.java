package com.virtual_guide.virtualguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;


public class FirstPage extends AppCompatActivity {

    ViewPager viewFlipper1, viewFlipper2, viewFlipper3;
    Button logout;

    int currentPage = 0, currentPage2 = 0, currentPage3 = 0;
    Timer timer, timer2, timer3;

    public static String user;
    public static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_first_page);

        logout = findViewById(R.id.logout);

        viewFlipper1 = findViewById(R.id.flip1);
        viewFlipper2 = findViewById(R.id.flip2);
        viewFlipper3 = findViewById(R.id.flip3);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        id = intent.getStringExtra("id");

        ImageAdapter adapter = new ImageAdapter(this);
        viewFlipper1.setAdapter(adapter);

        ImageAdapter2 adapter2 = new ImageAdapter2(this);
        viewFlipper2.setAdapter(adapter2);

        ImageAdapter3 adapter3 = new ImageAdapter3(this);
        viewFlipper3.setAdapter(adapter3);

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 6) {
                    currentPage = 0;
                }
                int i = currentPage++;
                viewFlipper1.setCurrentItem(i, true);

            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 0, 6000);

        final Handler handler2 = new Handler();
        final Runnable Update2 = new Runnable() {
            public void run() {
                if (currentPage2 == 6) {
                    currentPage2 = 0;
                }
                int i = currentPage2++;
                viewFlipper2.setCurrentItem(i, true);
            }
        };

        timer2 = new Timer(); // This will create a new Thread
        timer2.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler2.post(Update2);
            }
        }, 2000, 6000);

        final Handler handler3 = new Handler();
        final Runnable Update3 = new Runnable() {
            public void run() {
                if (currentPage3 == 6) {
                    currentPage3 = 0;
                }
                int i = currentPage3++;
                viewFlipper3.setCurrentItem(i, true);
            }
        };

        timer3 = new Timer(); // This will create a new Thread
        timer3.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler3.post(Update3);
            }
        }, 4000, 6000);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intoMain = new Intent(FirstPage.this, MainActivity.class);
                startActivity(intoMain);
            }
        });

    }
}


class ImageAdapter extends PagerAdapter{

    private Context mContext;
    private int[] images = new int[]{R.drawable.wall1, R.drawable.wall2, R.drawable.wall3, R.drawable.wall4, R.drawable.wall5, R.drawable.wall6};
    private LayoutInflater layoutInflater;

    ImageAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_swipe_layout,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(images[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}

class ImageAdapter2 extends PagerAdapter{

    private Context mContext;
    private int[] images = {R.drawable.camera1, R.drawable.camera2, R.drawable.camera3, R.drawable.camera4, R.drawable.camera5, R.drawable.camera6};
    private LayoutInflater layoutInflater2;

    ImageAdapter2(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater2 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater2.inflate(R.layout.activity_swipe_layout2,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(images[position]);

        Button camera = view.findViewById(R.id.goCamera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String user = FirstPage.user;
                String id = FirstPage.id;

                Intent intoClassifier = new Intent(mContext, ClassifierActivity.class);
                intoClassifier.putExtra("user", user);
                intoClassifier.putExtra("id", id);
                mContext.startActivity(intoClassifier);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}

class ImageAdapter3 extends PagerAdapter{

    private Context mContext;
    private int[] images = new int[]{R.drawable.wall6, R.drawable.wall5, R.drawable.wall4, R.drawable.wall3, R.drawable.wall2, R.drawable.wall1};
    private LayoutInflater layoutInflater3;

    ImageAdapter3(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater3 = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater3.inflate(R.layout.activity_swipe_layout3,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(images[position]);

        Button search = view.findViewById(R.id.goSearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String user = FirstPage.user;
                String id = FirstPage.id;
//                Log.d("#$#%~~~~~~~~~~~Username",user);
                Intent intoAddInfo = new Intent(mContext, HomeActivity.class);
                intoAddInfo.putExtra("user", user);
                intoAddInfo.putExtra("id", id);
                mContext.startActivity(intoAddInfo);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}