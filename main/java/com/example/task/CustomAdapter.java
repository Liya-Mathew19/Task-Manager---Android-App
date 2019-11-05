package com.example.task;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Model> implements View.OnClickListener {
    ArrayList<Model> list;
    Context mContext;
    private static class ViewHolder {

       TextView t_name,t_id;
       TextView tdate;
       TextView type, currentdate,tskstatus;
       ImageView delete,done;
       Button update;

    }
    public CustomAdapter(ArrayList<Model> listdata,Context context) {

        /********** Take passed values **********/
        super(context,R.layout.list_item,listdata);
        this.list=listdata;
        this.mContext=context;
        //inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Model model =getItem(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView= inflater.inflate(R.layout.list_item,parent, false);
            holder.t_id=convertView.findViewById(R.id.taskid);
            holder.t_name = convertView.findViewById(R.id.task);
            holder.tdate = convertView.findViewById(R.id.dateofcompletion);
            holder.type = convertView.findViewById(R.id.priority);
            holder.currentdate = convertView.findViewById(R.id.curdate);
            holder.tskstatus=convertView.findViewById(R.id.tskstatus);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.done = convertView.findViewById(R.id.done);
            holder.update=convertView.findViewById(R.id.update);
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
            holder.tskstatus.setText(model.getStatus());
            if(holder.tskstatus.getText().toString().equals("Completed"))
            {
                convertView.setBackgroundColor(Color.parseColor("#a4f4a1"));
                holder.t_name.setPaintFlags(holder.t_name.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else
            {
                convertView.setBackgroundColor(Color.WHITE);
            }
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