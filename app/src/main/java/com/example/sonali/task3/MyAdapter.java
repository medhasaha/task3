package com.example.sonali.task3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Sonali on 11-09-2018.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final ArrayList<persons> list;
   // MyDBHandler handler=null;
    String name=null;

    public MyAdapter(Context context, ArrayList<persons> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getItemViewType(int position) {
        persons p = list.get(position);
        String a=p.get_role();

            if(a.equals("A")){
                return 0;}
            else if(a.equals("B")){
                return 1;}
            else {
                return 2;
            }
        }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            LayoutInflater inflater1=LayoutInflater.from((parent.getContext()));
            View view=inflater1.inflate(R.layout.custom_row,parent,false);
            return new MyViewHolder(view);
        }
        else if(viewType==1){
            LayoutInflater inflater1=LayoutInflater.from((parent.getContext()));
            View view=inflater1.inflate(R.layout.custom_row1,parent,false);
            return new MyViewHolder1(view);
        }
        else{
            LayoutInflater inflater1=LayoutInflater.from((parent.getContext()));
            View view=inflater1.inflate(R.layout.custom_row2,parent,false);
            return new MyViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
           switch(getItemViewType(position)){
               case 0:
               {
                   persons p = list.get(position);
                   byte[] bitmapdata=p.get_img();
                   Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                   MyViewHolder myholder=(MyViewHolder) holder;
                   myholder.et_name.setText(p.get_name());
                   myholder.et_role.setText(p.get_role());
                   myholder.et_location.setText(p.get_loc());
                   myholder.imgView.setImageBitmap(bitmap);
                   name=p.get_name();
                   myholder.imgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteItem(position);
                        }
                    });


               }
               break;
               case 1:
               {
                   persons p = list.get(position);
                   byte[] bitmapdata=p.get_img();
                   Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                   MyViewHolder1 myholder1 = (MyViewHolder1) holder;
                   myholder1.et_name.setText(p.get_name());
                   myholder1.et_role.setText(p.get_role());
                   myholder1.et_location.setText(p.get_loc());
                   myholder1.imgView.setImageBitmap(bitmap);
                   name=p.get_name();
                   myholder1.imgView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           deleteItem(position);
                       }
                   });

               }
               break;
               case 2:
               {

                   persons p = list.get(position);
                   byte[] bitmapdata=p.get_img();
                   Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                   MyViewHolder2 myholder2 = (MyViewHolder2) holder;
                   myholder2.et_name.setText(p.get_name());
                   myholder2.et_role.setText(p.get_role());
                   myholder2.et_location.setText(p.get_loc());
                   myholder2.imgView.setImageBitmap(bitmap);
                   name=p.get_name();
                   myholder2.imgView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           deleteItem(position);
                       }
                   });

               }
               break;
               default:
                   break;
           }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        EditText et_name, et_role, et_location;
        ImageView imgView;

        public MyViewHolder(View itemView) {
            super(itemView);

            et_name = (EditText) itemView.findViewById(R.id.et_name);
            et_role = (EditText) itemView.findViewById(R.id.et_role);
            et_location = (EditText) itemView.findViewById(R.id.et_location);
            imgView=(ImageView)itemView.findViewById(R.id.imageView1);
        }
    }

   class MyViewHolder1 extends RecyclerView.ViewHolder  {
        EditText et_name, et_role, et_location;
        ImageView imgView;

        public MyViewHolder1(View itemView) {
            super(itemView);

            et_name = (EditText) itemView.findViewById(R.id.et_name);
            et_role = (EditText) itemView.findViewById(R.id.et_role);
            et_location = (EditText) itemView.findViewById(R.id.et_location);
            imgView=(ImageView)itemView.findViewById(R.id.imageView1);
        }
   }

    class MyViewHolder2 extends RecyclerView.ViewHolder{
        EditText et_name, et_role, et_location;
        ImageView imgView;

        public MyViewHolder2(View itemView) {
            super(itemView);

            et_name = (EditText) itemView.findViewById(R.id.et_name);
            et_role = (EditText) itemView.findViewById(R.id.et_role);
            et_location = (EditText) itemView.findViewById(R.id.et_location);
            imgView=(ImageView)itemView.findViewById(R.id.imageView1);
        }
    }

    void deleteItem(int index)
    {
        MyDBHandler handler=new MyDBHandler(context,null,null,1);
        String n=list.get(index).get_name();
        handler.delete(n);
        list.remove(index);
        notifyItemRemoved(index);
    }

}

