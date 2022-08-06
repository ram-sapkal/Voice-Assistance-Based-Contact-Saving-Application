package com.example.contactlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.contactlist.Adapter.MyAdapter;
import com.example.contactlist.Data.MyDBHandler;
import com.example.contactlist.model.Contact;
import com.example.contactlist.model.GetName;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    RecyclerView recyclerView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("All Contacts");

        MyDBHandler db = new MyDBHandler(MainActivity.this);

        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton search = findViewById(R.id.voiceSearch);
        recyclerView = findViewById(R.id.contactList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        List<Contact> allContacts = db.getAllContacts();
        for(Contact contactt: allContacts){
            Log.d("ram", "\nId: " + contactt.getId() + "\n" +
                    "Name: " + contactt.getName() + "\n"+
                    "Phone Number: " + contactt.getPhoneNumber() + "\n" +
                    "email: " + contactt.getEmail() + "\n");

        }
        myAdapter = new MyAdapter(this,(ArrayList<Contact>)allContacts);
        recyclerView.setAdapter(myAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddContact.class));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceToText();

            }
        });
    }

    private void voiceToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak call (name whom to call)");

        try {

                startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Voice to text exception :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
                if(resultCode == RESULT_OK && null !=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String command = result.get(0).trim().toLowerCase(Locale.ROOT);
                    Character firstLatter = command.charAt(0);

                    if(firstLatter.equals('c')){
                        String name = result.get(0).replace("call", "").trim();

                        MyDBHandler db = new MyDBHandler(MainActivity.this);
                        List<Contact> allContacts = db.getAllContacts();
                        for (Contact contactt : allContacts) {
                            try {
                                if (contactt.getName().equalsIgnoreCase(name)) {
                                    String s = "tel:" + contactt.getPhoneNumber().trim();

                                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                    Intent intentt = new Intent(Intent.ACTION_CALL);
                                    intentt.setData(Uri.parse(s));
                                    startActivity(intentt);

                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else if(command.equalsIgnoreCase("add new call") || command.equalsIgnoreCase("add new contact")){
                        startActivity(new Intent(MainActivity.this,AddContact.class));
                    }else if(firstLatter.equals('e')){
                       //-----
                        String name = result.get(0).replace("edit contact name", "").trim();

                        MyDBHandler db = new MyDBHandler(MainActivity.this);
                        List<Contact> allContacts = db.getAllContacts();
                        for (Contact contactt : allContacts) {
                            try {
                                if (contactt.getName().equalsIgnoreCase(name)) {

                                    Intent intent = new Intent(MainActivity.this, EditContact.class);
                                    intent.putExtra("id",contactt.getId()+"");
                                    intent.putExtra("name",contactt.getName());
                                    intent.putExtra("phnum",contactt.getPhoneNumber());
                                    intent.putExtra("email",contactt.getEmail());
                                    intent.putExtra("iurl",contactt.getPicture());
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                       //-----
                    }else if(firstLatter.equals('w')){
                        //------------------
                        String message = result.get(0).trim().toLowerCase(Locale.ROOT);
                        String []wordsMustContain={"whatsapp","message","to"};
                        int count=0;
                        for(String word:wordsMustContain){
                            if(message.contains(word)){
                                count++;

                            }
                        }
                        if(count==2){
                            message=message.replace("whatsapp message","").trim();
                            Toast.makeText(getApplicationContext(), "1: "+message, Toast.LENGTH_SHORT).show();
                            getMessage(message);
                        }
                        if(count==3){
                            message=message.replace("whatsapp message to","").trim();
                            Toast.makeText(getApplicationContext(), "2: "+message, Toast.LENGTH_SHORT).show();
                            getMessage(message);
                        }
                        //------------------
                    }else if(firstLatter.equals('t')){
                        //------------------
                        String message = result.get(0).trim().toLowerCase(Locale.ROOT);
                        String []wordsMustContain={"text","message","to"};
                        int count=0;
                        for(String word:wordsMustContain){
                            if(message.contains(word)){
                                count++;

                            }
                        }
                        if(count==2){
                            message=message.replace("text message","").trim();
                            //Toast.makeText(getApplicationContext(), "1: "+message, Toast.LENGTH_SHORT).show();
                            tgetMessage(message);
                        }
                        if(count==3){
                            message=message.replace("text message to","").trim();
                            //Toast.makeText(getApplicationContext(), "2: "+message, Toast.LENGTH_SHORT).show();
                            tgetMessage(message);
                        }
                        //------------------
                    }else if((result.get(0).trim().toLowerCase(Locale.ROOT)).contains("email")){
                        //------------------
                        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
                        String s = result.get(0).trim().toLowerCase(Locale.ROOT);
                        try {
                            String name = s.substring(0, s.indexOf("subject"));
                            name = name.replace("email", "").trim();

                            String subject = s.substring(s.indexOf("subject"), s.indexOf("body"));
                            subject = subject.replace("subject", "").trim();

                            String body = s.substring(s.indexOf("body"), s.length());
                            body = body.replace("body", "").trim();
                            Toast.makeText(this, name + "---" + subject + "--" + body, Toast.LENGTH_SHORT).show();
                            doMail(name, subject, body);
                        }catch (Exception e){
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        //------------------
                    }
                }
                break;
        }
    }

    private void doMail(String name, String subject, String body) {
        MyDBHandler db = new MyDBHandler(MainActivity.this);
        List<Contact> allContacts = db.getAllContacts();
        for (Contact contactt : allContacts) {
            try {
                if (contactt.getName().equalsIgnoreCase(name)) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{contactt.getEmail()});
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_TEXT, body);

                    //need this to prompts email client only
                    email.setType("message/rfc822");

                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getMessage(String msg) {
        String[] temp = msg.split("\\s");
        String n = "";
        for (String word : temp) {
            if (word.equals("message")) {
                msg = msg.replaceFirst(n, "").trim();
                msg = msg.replaceFirst("message", "").trim();
                break;
            } else {
                n = n + " " + word;
                n = n.trim();
            }
        }
        //n=name , msg=message

        //code to get Phone number
        MyDBHandler db = new MyDBHandler(MainActivity.this);
        List<Contact> allContacts = db.getAllContacts();
        for (Contact contactt : allContacts) {
            try {
                if (contactt.getName().equalsIgnoreCase(n)) {
                    String num = "+91" + contactt.getPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+num+"&text="+msg));
                    startActivity(intent);

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void tgetMessage(String msg) {
        String[] temp = msg.split("\\s");
        String n = "";
        for (String word : temp) {
            if (word.equals("message")) {
                msg = msg.replaceFirst(n, "").trim();
                msg = msg.replaceFirst("message", "").trim();
                break;
            } else {
                n = n + " " + word;
                n = n.trim();
            }
        }
        //n=name , msg=message

        //code to get Phone number
        MyDBHandler db = new MyDBHandler(MainActivity.this);
        List<Contact> allContacts = db.getAllContacts();
        for (Contact contactt : allContacts) {
            try {
                if (contactt.getName().equalsIgnoreCase(n)) {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contactt.getPhoneNumber(),null,msg,null,null);
                        Toast.makeText(getApplicationContext(), "Message is sent", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "smsSend exce : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}