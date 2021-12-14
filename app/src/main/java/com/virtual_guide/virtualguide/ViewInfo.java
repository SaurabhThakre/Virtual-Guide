package com.virtual_guide.virtualguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewInfo extends AppCompatActivity {

    String user = "";
    String date = "";
    String title = "";
    String imgTitle = "";
    String imgUrl = "";
    String desc = "";
    String landmark = "";
    int liked = 0;

    LinearLayout linearLayout;
    LinearLayout linearLayout1;
    LinearLayout linearLayout2;
    ScrollView scrollView;
    TextView userView;
    TextView dateView;
    TextView titleView;
    TextView imgTitleView;
    TextView descView;
    ImageView imageView;
    ImageView imageView1;
    TextView likeView;

    int LikeCount = 0;

    int flag = 0;


    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_info);


        // do not show data stored in local cache
        // called 3 times in try catch
        try {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            db.setFirestoreSettings(settings);
        }
        catch (Exception e){
            Log.d("~~~~~~~~~~~~~~~Excepion",e.toString());
            try {
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false)
                        .build();
                db.setFirestoreSettings(settings);
            }
            catch (Exception e1){
                Log.d("~~~~~~~~~~~~~~~Excepion",e.toString());
                try {
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(false)
                            .build();
                    db.setFirestoreSettings(settings);
                }
                catch (Exception e2){
                    Log.d("~~~~~~~~~~~~~~~Excepion",e.toString());
                }
            }
        }


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //need only once
        // First Parent LinearLayout
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setPadding(dpToPX(10), dpToPX(20), dpToPX(10), dpToPX(10));
        linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.back3));
        linearLayout.setLayoutParams(params);

        // Scroll view inside linearLayout
        scrollView = new ScrollView(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(params1);
        linearLayout.addView(scrollView);

        linearLayout1 = new LinearLayout(this);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout1.setLayoutParams(params1);
        scrollView.addView(linearLayout1);


        setContentView(linearLayout);

    }

    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        final String places = intent.getStringExtra("landmark");
        final String users = intent.getStringExtra("user");

        final CollectionReference notebookRef = db.collection(places);

        notebookRef
                .orderBy("liked", Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(ViewInfo.this, "Error Loading", Toast.LENGTH_SHORT).show();
                    Log.d("#$#$#$#$#&*&*&*&*&", e.toString());
                    return;
                }

                for (final QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {


                        final Note note = documentSnapshot.toObject(Note.class);

                        note.setDocumentId(documentSnapshot.getId());

                        user = note.getUser();
                        date = note.getDate();
                        title = note.getTitle();
                        imgTitle = note.getImgTitle();
                        imgUrl = note.getImageUrl();
                        desc = note.getDesc();
                        landmark = note.getLandmark();
                        liked = note.getLiked();


                        linearLayout2 = new LinearLayout(ViewInfo.this);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        linearLayout2.setOrientation(LinearLayout.VERTICAL);
                        params2.setMargins(0, 0, 0, dpToPX(20));
                        linearLayout2.setPadding(dpToPX(10), dpToPX(10), dpToPX(10), dpToPX(10));
                        linearLayout2.setBackground(ContextCompat.getDrawable(ViewInfo.this, R.drawable.shadow3));
                        linearLayout2.setLayoutParams(params2);
                        linearLayout1.addView(linearLayout2);

                        userView = new TextView(ViewInfo.this);
                        LinearLayout.LayoutParams userParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        userParams.gravity = Gravity.CENTER_HORIZONTAL;
                        userView.setLayoutParams(userParams);
                        userView.setTextColor(Color.parseColor("white"));
                        userView.setTextSize(20);
                        userView.setText("By " + user);
                        linearLayout2.addView(userView);

                        dateView = new TextView(ViewInfo.this);
                        dateView.setLayoutParams(userParams);
                        dateView.setTextColor(Color.parseColor("white"));
                        dateView.setTextSize(15);
                        dateView.setText(date);
                        linearLayout2.addView(dateView);

                        titleView = new TextView(ViewInfo.this);
                        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        titleParams.gravity = Gravity.CENTER_HORIZONTAL;
                        titleParams.setMargins(0, dpToPX(40), 0, 0);
                        titleView.setLayoutParams(titleParams);
                        titleView.setLayoutParams(userParams);
                        titleView.setTextColor(Color.parseColor("white"));
                        titleView.setTextSize(30);
                        titleView.setTypeface(null, Typeface.BOLD);
                        titleView.setText(title);
                        linearLayout2.addView(titleView);

                        imageView = new ImageView(ViewInfo.this);
                        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(dpToPX(350), dpToPX(250));
                        imgParams.gravity = Gravity.CENTER_HORIZONTAL;
                        imageView.setLayoutParams(imgParams);
                        imageView.setBackground(ContextCompat.getDrawable(ViewInfo.this, R.drawable.addimg));
                        final ProgressDialog p = new ProgressDialog(ViewInfo.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                        p.setMessage("Please wait...Loading");
                        p.setIndeterminate(false);
                        p.setCancelable(false);
                        p.show();

                        Log.d("Image ______URL:",note.getImageUrl());
                        if(!note.getImageUrl().isEmpty()){
                            Picasso.get().load(note.getImageUrl()).into(imageView);
                        }

                        Log.d("__________ImageTitle:", imgTitle);

                        p.dismiss();


                        linearLayout2.addView(imageView);

                        imgTitleView = new TextView(ViewInfo.this);
                        imgTitleView.setLayoutParams(userParams);
                        imgTitleView.setTextColor(Color.parseColor("white"));
                        imgTitleView.setTextSize(15);
                        imgTitleView.setText(imgTitle);
                        linearLayout2.addView(imgTitleView);

                        descView = new TextView(ViewInfo.this);
                        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        descParams.setMargins(0, dpToPX(20), 0, 0);
                        descView.setLayoutParams(descParams);
                        descView.setTextColor(Color.parseColor("white"));
                        descView.setTextSize(18);
                        descView.setTypeface(null, Typeface.BOLD);
                        descView.setText(desc);
                        linearLayout2.addView(descView);

                        imageView1 = new ImageView(ViewInfo.this);
                        LinearLayout.LayoutParams imgParams1 = new LinearLayout.LayoutParams(dpToPX(50), dpToPX(50));
                        imageView1.setLayoutParams(imgParams1);
                        imageView1.setBackground(ContextCompat.getDrawable(ViewInfo.this, R.drawable.like));
                        linearLayout2.addView(imageView1);
                        imageView1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                final ProgressDialog p = new ProgressDialog(ViewInfo.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                                p.setMessage("Please wait...Updating");
                                p.setIndeterminate(false);
                                p.setCancelable(false);
                                p.show();

                                liked = note.getLiked();
                                LikeCount = liked + 1;
                                note.setLiked(LikeCount);

                                notebookRef.document(note.getDocumentId()).set(note, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                p.dismiss();
                                                Toast.makeText(ViewInfo.this, "Like Updated!", Toast.LENGTH_SHORT).show();
                                                try {
                                                    Thread.sleep(500);
                                                } catch (InterruptedException ex) {
                                                    ex.printStackTrace();
                                                }
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                p.dismiss();
                                                Toast.makeText(ViewInfo.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                                Log.d("AddInfo", e.toString());
                                            }
                                        });

                            }
                        });

                        likeView = new TextView(ViewInfo.this);
                        LinearLayout.LayoutParams likeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        likeView.setLayoutParams(likeParams);
                        likeView.setTextColor(Color.parseColor("white"));
                        likeView.setTextSize(18);
                        likeView.setTypeface(null, Typeface.BOLD);
                        Log.d("Likes_________________:", "" + liked);
                        likeView.setText("Total Likes:" + Integer.toString(liked));
                        linearLayout2.addView(likeView);

                        linearLayout.setBackground(ContextCompat.getDrawable(ViewInfo.this, R.drawable.navbar));


                    } else {
                        Toast.makeText(ViewInfo.this, "Document does not exist", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

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
