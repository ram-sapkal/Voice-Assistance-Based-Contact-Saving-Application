package com.example.contactlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.contactlist.Data.MyDBHandler;
import com.example.contactlist.Dialog.EmailDialog;
import com.example.contactlist.Dialog.MessageDialog;
import com.example.contactlist.model.Contact;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.UUID;

public class EditContact extends AppCompatActivity implements MessageDialog.DialogListner,EmailDialog.DialogListner {

    EditText eName,ePhone,eemail;
    ImageView eimage;
    FloatingActionButton eSave,eEdit,eCall,eWhatsapp,eMessage,eMail;
    String url;
    String cId;
    private Uri mPhotoUri;
    String generatedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        eName = findViewById(R.id.editContactName);
        ePhone = findViewById(R.id.editContactPhone);
        eemail = findViewById(R.id.editContactEmail);
        eSave = findViewById(R.id.editContactSaveBtn);
        eEdit = findViewById(R.id.editPImage);
        eCall = findViewById(R.id.editCallBtn);
        eWhatsapp=findViewById(R.id.editWhatsapp);
        eMessage = findViewById(R.id.editMessageBtn);
        eMail=findViewById(R.id.editMail);

        getSupportActionBar().setTitle("Edit Contact");

        eimage = findViewById(R.id.editContactProfile_image);

        Intent intent = getIntent();

        eName.setText(intent.getStringExtra("name"));
        ePhone.setText(intent.getStringExtra("phnum"));
        eemail.setText(intent.getStringExtra("email"));
        url=intent.getStringExtra("iurl");
        cId=intent.getStringExtra("id");
        Glide.with(getApplicationContext()).load(url).into(eimage);

        eSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=eName.getText().toString().trim();
                String phnum=ePhone.getText().toString().trim();
                String email=eemail.getText().toString().trim();
                if(name.isEmpty()){
                    eName.setError("Please Enter Name");
                    eName.requestFocus();
                }else if(phnum.isEmpty()){
                    ePhone.setError("Please Enter number");
                    ePhone.requestFocus();
                }else if(email.isEmpty()){
                    eemail.setError("Please Enter Email");
                    eemail.requestFocus();
                }else {
                    try{
                        MyDBHandler db = new MyDBHandler(EditContact.this);

                        Contact contact = new Contact(Integer.parseInt(cId),name, phnum, email,url);
                        db.updateContact1(contact);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        eEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trySelector();
                Toast.makeText(getApplicationContext(), "tap", Toast.LENGTH_SHORT).show();
                }
        });

        eCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = ePhone.getText().toString().trim();
                if(num.length()==10){
                    String s = "tel:" + num;
                    Intent intentt = new Intent(Intent.ACTION_CALL);
                    intentt.setData(Uri.parse(s));
                    startActivity(intentt);
                }
            }
        });

        eWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = ePhone.getText().toString().trim();
                if(number.length()==10){
                    String num = "+91" + number;
                    String msg = "Manoj Dada";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+num));
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "number is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        eMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogue("message");
            }
        });

        eMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogue("null");
            }
        });

    }

    private void openDialogue(String s) {
        if(s.equals("message")) {
            MessageDialog messageDialog = new MessageDialog();
            messageDialog.show(getSupportFragmentManager(), "Message Dialog");
        }else{
            EmailDialog emailDialog=new EmailDialog();
            emailDialog.show(getSupportFragmentManager(),"Mail Dialog");
        }
    }
    @Override
    public void applyEmail(String mailSubject,String mailBody) {
        Toast.makeText(getApplicationContext(), mailSubject+"--"+mailBody, Toast.LENGTH_SHORT).show();
        sendMial(mailSubject,mailBody);
    }

    private void sendMial(String mailSubject, String mailBody) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{eemail.getText().toString().trim()});
        email.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
        email.putExtra(Intent.EXTRA_TEXT, mailBody);

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public void applyMessage(String mesg) {
        Toast.makeText(getApplicationContext(), mesg, Toast.LENGTH_SHORT).show();
        sendSms(mesg);
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
                eimage.setImageURI(mPhotoUri);
                eimage.invalidate();
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
                                        //Toast.makeText(getApplicationContext(), "id="+cId+"---"+generatedFilePath, Toast.LENGTH_SHORT).show();
                                        MyDBHandler db = new MyDBHandler(EditContact.this);

                                        Contact contact = new Contact(Integer.parseInt(cId),generatedFilePath);
                                        db.updateContact2(contact);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.delete :
                MyDBHandler db = new MyDBHandler(EditContact.this);
                db.deleteContact(Integer.parseInt(cId));
                Toast.makeText(getApplicationContext(), "Contact Deleted Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditContact.this,MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendSms(String s){
        String phoneNo = ePhone.getText().toString().trim();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,s,null,null);
            Toast.makeText(getApplicationContext(), "Message is sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "smsSend exce : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}