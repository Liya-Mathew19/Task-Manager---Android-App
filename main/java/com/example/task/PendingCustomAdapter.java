package com.example.task;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class PendingCustomAdapter extends ArrayAdapter<Model> implements View.OnClickListener {
    ArrayList<Model> list;
    Context mContext;
    private static class ViewHolder {

        TextView t_name,t_id;
        TextView tdate;
        TextView type, currentdate;
        ImageView delete,done;

    }
    public PendingCustomAdapter(ArrayList<Model> listdata,Context context) {

        /********** Take passed values **********/
        super(context,R.layout.pending_list_items,listdata);
        this.list=listdata;
        this.mContext=context;
        //inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Model model = getItem(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView= inflater.inflate(R.layout.pending_list_items,parent, false);
            holder.t_id=convertView.findViewById(R.id.taskid);
            holder.t_name = convertView.findViewById(R.id.task);
            holder.tdate = convertView.findViewById(R.id.dateofcompletion);
            holder.type = convertView.findViewById(R.id.priority);
            holder.currentdate = convertView.findViewById(R.id.curdate);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.done = convertView.findViewById(R.id.done);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.t_id.setText(model.gettaskId());
        holder.t_name.setText(model.getTask_name());
        holder.tdate.setText(model.getDate());
        holder.type.setText(model.getType());
        holder.currentdate.setText(model.getcurDate());
        /*holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Are you sure you want to delete this?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        list.remove(model);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });*/
        return convertView;
    }


    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

}