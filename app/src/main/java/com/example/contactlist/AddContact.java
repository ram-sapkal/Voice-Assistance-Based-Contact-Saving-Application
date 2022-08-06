package com.example.contactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contactlist.Data.MyDBHandler;
import com.example.contactlist.model.Contact;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.UUID;

public class AddContact extends AppCompatActivity {

    private Uri mPhotoUri;          //
    ImageView mPhoto;//

    TextView addContactName,addContactPhNumber,addContactEmail;
    FloatingActionButton addContactDone;

    String generatedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        getSupportActionBar().setTitle("Add New Contact");

        addContactName = findViewById(R.id.nameEditText);
        addContactPhNumber = findViewById(R.id.phoneEditText);
        addContactEmail = findViewById(R.id.emailEditText);
        addContactDone = findViewById(R.id.addContactDoneBtn);

        mPhoto = findViewById(R.id.profile_image);//
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                Toast.makeText(getApplicationContext(), "tap", Toast.LENGTH_SHORT).show();
            }
        });


        addContactDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = addContactName.getText().toString().trim();
                String phnumber = addContactPhNumber.getText().toString().trim();
                String email = addContactEmail.getText().toString().trim();
                //String imageUrl = getImageFirebaseUrl(phnumber);
                if(name.isEmpty()){
                    addContactName.setError("Please Enter Name");
                    addContactName.requestFocus();
                }else if(phnumber.isEmpty()){
                    addContactPhNumber.setError("Please Enter Name");
                    addContactPhNumber.requestFocus();
                }else if(email.isEmpty()){
                    addContactEmail.setError("Please Enter Name");
                    addContactEmail.requestFocus();
                }else {
                    MyDBHandler db = new MyDBHandler(AddContact.this);

                    Contact contact = new Contact(name,phnumber,email,generatedFilePath);
                    Log.d("ram", "successfully added to the db"+contact.getName());
                    db.addContacts(contact);

                    Toast.makeText(getApplicationContext(), "Contact Added Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddContact.this,MainActivity.class));
                }
            }
        });

    }

    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intent_type));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mPhotoUri = data.getData();
                mPhoto.setImageURI(mPhotoUri);
                mPhoto.invalidate();
                String uniqueID = UUID.randomUUID().toString();

                //-----------
                final ProgressDialog dialog=new ProgressDialog(this);
                dialog.setTitle("File Uploader");
                dialog.show();


                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference uploader=storage.getReference().child(uniqueID);
                uploader.putFile(mPhotoUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        generatedFilePath=uri.toString();
                                    }
                                });
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                float percent=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded :"+(int)percent+" %");
                            }
                        });
                //-----------
            }
        }
    }
}