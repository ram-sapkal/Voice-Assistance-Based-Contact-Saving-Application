package com.example.contactlist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contactlist.EditContact;
import com.example.contactlist.R;
import com.example.contactlist.model.Contact;
import com.example.contactlist.parameters.Parameters;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{


    Context context;
    ArrayList<Contact> list;

    public MyAdapter(Context context, ArrayList<Contact> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contact_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Contact std = list.get(position);
        holder.Name.setText(std.getName());
        holder.phNumber.setText(std.getPhoneNumber());
        holder.eemail.setText(std.getEmail());

        try {
            /*Uri imageUri = Uri.parse(std.getPicture());
            holder.Picture.setImageURI(imageUri);*/
            Glide.with(holder.Picture.getContext()).load(std.getPicture()).into(holder.Picture);
        }
        catch (Exception e){
            Toast.makeText(context.getApplicationContext(), "Exception :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try{

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Contact con = list.get(position);
                    Intent intent = new Intent(context, EditContact.class);
                    intent.putExtra("id",con.getId()+"");
                    intent.putExtra("name",con.getName());
                    intent.putExtra("phnum",con.getPhoneNumber());
                    intent.putExtra("email",con.getEmail());
                    intent.putExtra("iurl",con.getPicture());
                    context.startActivity(intent);
                }
            });

        }catch (Exception e){
            Toast.makeText(context.getApplicationContext(), "Exception :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name,phNumber,eemail;
        ImageView Picture;
        LinearLayout contactDetails;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Name=itemView.findViewById(R.id.textName);
            phNumber=itemView.findViewById(R.id.textNumber);
            eemail=itemView.findViewById(R.id.textEmail);
            Picture=itemView.findViewById(R.id.imageContact);
            contactDetails=itemView.findViewById(R.id.contactDetail);
        }
    }
}
