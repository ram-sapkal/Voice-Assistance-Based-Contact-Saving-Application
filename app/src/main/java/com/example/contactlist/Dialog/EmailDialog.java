package com.example.contactlist.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.contactlist.R;

public class EmailDialog extends AppCompatDialogFragment {
    private EditText subject,body;
    private EmailDialog.DialogListner listner;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mail,null);

        builder.setView(view)
                .setTitle("Enter Message")
                .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(subject.getText().toString().trim().isEmpty()){
                    subject.setError("mail subject empty");
                    subject.requestFocus();
                }else if(body.getText().toString().trim().isEmpty()){
                    body.setError("mail body empty");
                    body.requestFocus();
                }else {
                    String text = subject.getText().toString().trim();
                    String text2 = body.getText().toString().trim();
                    listner.applyEmail(text,text2);
                }
            }
        });

        subject=view.findViewById(R.id.mailSubject);
        body=view.findViewById(R.id.mailBody);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listner=(EmailDialog.DialogListner)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogListner");
        }
    }

    public interface DialogListner{
        void applyEmail(String text1,
                        String text2);
    }
}
