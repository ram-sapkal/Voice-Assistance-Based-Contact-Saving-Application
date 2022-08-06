package com.example.contactlist.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.contactlist.EditContact;
import com.example.contactlist.R;

public class MessageDialog extends AppCompatDialogFragment {
    private EditText message;
    private DialogListner listner;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.message_layout,null);

        builder.setView(view)
                .setTitle("Enter Message")
                .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(message.getText().toString().trim().isEmpty()){
                    message.setError("please enter message");
                    message.requestFocus();
                }else {
                    String text = message.getText().toString().trim();
                    listner.applyMessage(text);
                }
            }
        });

        message = view.findViewById(R.id.editMessageText);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listner=(DialogListner)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogListner");
        }
    }

    public interface DialogListner{
        void applyMessage(String mesg);
    }
}
