package com.virtual_guide.virtualguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddInfo extends AppCompatActivity {

    private ImageView addImage;
    private static final int pick = 1;
    Uri imageUri, photoURI;
    private EditText imgdesc, title, desc;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public  static final int RequestPermissionCode  = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        addImage = findViewById(R.id.img);
        imgdesc = findViewById(R.id.imgdesc);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Image"), pick);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == pick && resultCode == RESULT_OK && data != null) {
            try{
                imageUri = data.getData();
                Log.d("~~~~~~~~~~~~~~~ImageURI",imageUri.toString());
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    addImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void saveInfo(View v) {

//        if(uploadTask != null && uploadTask.isInProgress()){
//          //  do nothing
//        }

        final ProgressDialog p = new ProgressDialog(AddInfo.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        p.setMessage("Please wait...Uploading");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();

        Intent intent = getIntent();
        final String username = intent.getStringExtra("user");
        final String userId = intent.getStringExtra("id");
        final String place = intent.getStringExtra("landmark");

        final CollectionReference notebookRef = db.collection(place);

        Log.d("*****User********", username);

        final String imgTitleString = imgdesc.getText().toString();
        final String titleString = title.getText().toString();
        final String descriptionString = desc.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        final String currentDateandTime = "on " + sdf.format(new Date());

        if (imageUri != null) {
            Log.d("~~~~~~~2~~~~~~~ImageURI",imageUri.toString());
            StorageReference riversRef = storageReference.child(userId + '/' + imgTitleString + "." + getFileExtension(imageUri));

            riversRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    p.setProgress(0);
                                }
                            }, 3000);
                            Toast.makeText(AddInfo.this, "Upload successful", Toast.LENGTH_LONG).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            String imageUrl = downloadUrl.toString();

                            Log.d("imageUrl_______$%%",imageUrl);
                            Note note = new Note(username, currentDateandTime, titleString, imgTitleString, imageUrl, descriptionString, place.toLowerCase(), 0);

                            notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    p.dismiss();
                                    Toast.makeText(AddInfo.this, "Note Saved!", Toast.LENGTH_SHORT).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    p.dismiss();
                                    Toast.makeText(AddInfo.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                    Log.d("AddInfo", e.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(AddInfo.this, "Image File not Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    p.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        } else {
            Toast.makeText(AddInfo.this, "No Image File Selected", Toast.LENGTH_SHORT).show();
            String imageUrl = "";
            Note note = new Note(username, currentDateandTime, titleString, imgTitleString, imageUrl, descriptionString, place.toLowerCase(), 0);

            notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    p.dismiss();
                    Toast.makeText(AddInfo.this, "Note Saved!", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    p.dismiss();
                    Toast.makeText(AddInfo.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                    Log.d("AddInfo", e.toString());
                }
            });
        }

    }

}