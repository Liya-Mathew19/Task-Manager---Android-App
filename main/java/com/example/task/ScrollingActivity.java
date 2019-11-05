package com.example.task;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {
    String deviceid;                                                        //Variable to store deviceid
    CheckBox cb1,cb2;                                                       //Checkboxes in update_task
    ArrayList<Model> list;                                                  //List of tasks
    List<TaskPL> lst;                                                       //Object of class TaskPL
    ListView l1;                                                            //Listview in content_scrolling
    private ProgressDialog pDialog;                                         //Progress dialog while loading
    TextView currentdate,t_name,type,tdate,taskid,tskstatus,updateid;       //Textviews in list_item and update_task
    EditText taskname,completion,status,taskname_update,completion_update,status_update;  //EditTexts in add_task and update_task
    Spinner spin,spin_update;                                               //Spinners in add_task and update_task
    String[] spinnerValueHoldValue = {"--Select--","High", "Medium", "Low"};//Spinner Values
    String result="";                                                       //String variable to store the JSON result
    int tid,pos,pos1,tcanceled;                                             //Variable to store the id of task
    String lineid;                                                          //Variable to store the value of TextView taskid in list_item
    String myURL="http://tsmith.co.in/task/api/SaveTask";                   //API of add & update task
    public static final String REQUEST_METHOD="POST";                       //Request method of add & update
    public static final String REQUEST_METHOD2="GET";                       //Request method of view details
    public static final int READ_TIMEOUT=15000;                             //Read_timeOut variable
    public static final int CONNECTION_TIMEOUT=50000;                       //Connection timeout variable
    String tsknme,tskdate,tskpriority,tskcurdate,taskstatus,
    tsknmeupdate,tskdateupdate,tskpriorityupdate,tskcurdateupdate;          //Variables to store the values of Edittexts in add_task
    String tname,tcurdate,tdoc,tsktype,tstatus;                             //Variables to store the value of JSON
    String a,b,s,i;                                                         //Variables to store the values of list_details
    TextView display_taskname,display_taskdoc,display_tasktype,display_status;//Textviews to view details of a task in list_details
    Gson gson,gson1;                                                        //Gson variables to convert JSON
    ImageView done,delete;                                                  //ImageView in list_item to set completion of task
    Button update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        l1=(ListView)findViewById(R.id.listview);
        final Telephony telephony=new Telephony(ScrollingActivity.this);
        if(ContextCompat.checkSelfPermission(ScrollingActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }
        else
        {
            deviceid=telephony.GetDeviceUniqueId();
            Toast.makeText(this, "Device id: "+deviceid, Toast.LENGTH_SHORT).show();
        }
        loadData();
        l1.setAdapter(new CustomAdapter(list,getApplicationContext()));
        FloatingActionButton pendingtasks=(FloatingActionButton) findViewById(R.id.pendingtasks);
        currentdate = (TextView) findViewById(R.id.curdate);    // TextView in list_item
        t_name = (TextView) findViewById( R.id.task);           // TextView in list_item
        type = (TextView) findViewById(R.id.priority);          // TextView in list_item
        tdate=(TextView) findViewById((R.id.dateofcompletion)); // TextView in list_item
        taskid=(TextView)findViewById(R.id.taskid);             // TextView in list_item
        tskstatus=(TextView) findViewById(R.id.tskstatus);      // TextView in list_item
        done=(ImageView)findViewById(R.id.done);                // ImageView in list_item
        cb1=(CheckBox)findViewById(R.id.notcompleted);          // Checkbox in update_task
        cb2=(CheckBox)findViewById(R.id.completed);             // Checkbox in update_task
        delete=(ImageView)findViewById(R.id.delete);            // Button in list_view

        //Button on activity_scrolling to view pending tasks

        pendingtasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ScrollingActivity.this, PendingActivity.class);
                startActivity(i);
            }
        });


        //ListView to view the tasks

        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int position, final long id) {
                currentdate = (TextView) view.findViewById(R.id.curdate);
                t_name = (TextView) view.findViewById(R.id.task);
                type = (TextView) view.findViewById(R.id.priority);
                tdate = (TextView) view.findViewById((R.id.dateofcompletion));
                taskid=(TextView)view.findViewById(R.id.taskid);
                tskstatus=(TextView)view.findViewById(R.id.tskstatus);

                //Button to delete tasks

                delete=(ImageView)view.findViewById(R.id.delete);
                delete.setTag(position);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pos1=(Integer)view.getTag();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
                        builder.setTitle("Are you sure you want to delete this?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletetask x=new deletetask();
                                x.execute();
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
                });
                ViewTask viewTask=new ViewTask();
                viewTask.execute();
                lineid= taskid.getText().toString();
                final Dialog dialog = new Dialog(ScrollingActivity.this);
                dialog.setContentView(R.layout.list_details);
                dialog.setTitle("Task Details");
                dialog.setCancelable(true);
                display_taskname=(TextView)dialog.findViewById(R.id.display_taskname);
                display_taskdoc=(TextView) dialog.findViewById(R.id.display_taskdoc);
                display_tasktype=(TextView)dialog.findViewById(R.id.display_taskpriority);
                display_status=(TextView)dialog.findViewById(R.id.display_status);


                //OK Button on alert Box.[list_details]

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

                //UPDATE Button on alert Box.[list_details]

                update=(Button)dialog.findViewById(R.id.update);
                update.setEnabled(false);
                update.setBackgroundColor(Color.parseColor("#f8dafb"));
                update.setTag(position);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos=(Integer)v.getTag();
                        LayoutInflater inflater1 = getLayoutInflater();
                        final View updateLayout = inflater1.inflate(R.layout.update_task, null);
                        updateid=(TextView)updateLayout.findViewById(R.id.updateid);
                        taskname_update=(EditText)updateLayout.findViewById(R.id.taskname_update);
                        completion_update=(EditText)updateLayout.findViewById(R.id.completion_update);
                        spin_update=(Spinner) updateLayout.findViewById(R.id.priority_update);
                        status_update= (EditText)updateLayout.findViewById(R.id.status_update);
                        cb1=(CheckBox)updateLayout.findViewById(R.id.notcompleted);
                        cb2=(CheckBox)updateLayout.findViewById(R.id.completed);
                        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            cb2.setChecked(false);
                            }
                        });
                        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            cb1.setChecked(false);
                            }
                        });
                        final ArrayAdapter<String> spinadapterupdate = new ArrayAdapter<>(ScrollingActivity.this, android.R.layout.simple_list_item_1, spinnerValueHoldValue);
                        spin_update.setAdapter(spinadapterupdate);
                        spin_update.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        a=display_taskname.getText().toString();
                        b=display_taskdoc.getText().toString();
                        s=display_status.getText().toString();
                        i=display_tasktype.getText().toString();
                        lineid=taskid.getText().toString();
                        taskname_update.setText(a);
                        completion_update.setText(b);
                        status_update.setText(s);
                        updateid.setText(lineid);
                        spin_update.setSelection(spinadapterupdate.getPosition(i));
                        if(s.equals("Not Completed"))
                        {
                            cb1.setChecked(true);
                        }
                        else
                        {
                            cb2.setChecked(true);
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        Calendar c = Calendar.getInstance();
                        final String curdate_update = sdf.format(c.getTime());

                        completion_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar mcurrentDate = Calendar.getInstance();
                                final int mYear = mcurrentDate.get(Calendar.YEAR);
                                final int mMonth = mcurrentDate.get(Calendar.MONTH);
                                final int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                                DatePickerDialog mDatePicker = new DatePickerDialog(ScrollingActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                        int year=selectedyear;
                                        int month=selectedmonth+1;
                                        int day=selectedday;
                                        completion_update.setText(day + "/" + month + "/" + year);
                                    }
                                }, mYear, mMonth, mDay);
                                mDatePicker.setTitle("Select date");
                                mDatePicker.show();
                            }
                        });

                        //AlertBox of Updating tasks

                        AlertDialog.Builder alert = new AlertDialog.Builder(ScrollingActivity.this);
                        alert.setTitle("Update Task Details");
                        alert.setView(updateLayout);
                        alert.setCancelable(false);
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alert.setPositiveButton("UPDATE TASK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cb1=(CheckBox)updateLayout.findViewById(R.id.notcompleted);
                                cb2=(CheckBox)updateLayout.findViewById(R.id.completed);
                                if (taskname_update.getText().toString().isEmpty()) {
                                    Toast.makeText(ScrollingActivity.this, "Field Cannot be empty..Please enter your task !!", Toast.LENGTH_LONG).show();
                                }
                                else
                                    {
                                    lineid = taskid.getText().toString();
                                    tsknmeupdate = taskname_update.getText().toString();
                                    tskdateupdate = completion_update.getText().toString();

                                    if (spin_update.getSelectedItem() == "--Select--")
                                    {
                                        tskpriorityupdate = "No priority";
                                    }
                                    else
                                    {
                                        tskpriorityupdate = spin_update.getSelectedItem().toString();
                                    }

                                    tskcurdateupdate = curdate_update;

                                    if (cb1.isChecked())
                                    {
                                        cb2.setChecked(false);
                                        taskstatus = cb1.getText().toString();
                                    }
                                    if (cb2.isChecked())
                                    {
                                        cb1.setChecked(false);
                                        taskstatus = cb2.getText().toString();
                                    }

                                    updatetask updatetask = new updatetask();
                                    updatetask.execute();

                                    dialog.dismiss();
                                }

                            }
                        });

                        AlertDialog dialog = alert.create();
                        dialog.show();
                    }
                });
            }
        });

        //Button to Delete all tasks

        FloatingActionButton clear=(FloatingActionButton)findViewById(R.id.clearall);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog=new AlertDialog.Builder(ScrollingActivity.this)
                        .setTitle("Delete all tasks?")
                        .setMessage("Do you want to delete all tasks ?")
                        .setPositiveButton("Delete all tasks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.clear();
                                l1.setAdapter(new CustomAdapter(list,getApplicationContext()));
                                saveData();
                                Toast.makeText(getApplicationContext(),"Deleted all the tasks",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create();
                dialog.show();
            }
        });

                //Button to add a new task

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       LayoutInflater inflater = getLayoutInflater();
                        final View alertLayout = inflater.inflate(R.layout.add_task, null);
                        taskname = (EditText) alertLayout.findViewById(R.id.taskname);
                        completion = (EditText) alertLayout.findViewById(R.id.completion);
                        spin=(Spinner) alertLayout.findViewById(R.id.priority);
                        status=(EditText) alertLayout.findViewById(R.id.status);
                        final ArrayAdapter<String> adapter = new ArrayAdapter<>(ScrollingActivity.this, android.R.layout.simple_list_item_1, spinnerValueHoldValue);
                        spin.setAdapter(adapter);
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                 }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                        completion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar mcurrentDate = Calendar.getInstance();
                                final int mYear = mcurrentDate.get(Calendar.YEAR);
                                final int mMonth = mcurrentDate.get(Calendar.MONTH);
                                final int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                                DatePickerDialog mDatePicker = new DatePickerDialog(ScrollingActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                        int selectedy=selectedyear;
                                        int selectedm=selectedmonth+1;
                                        int selectedd=selectedday;
                                            completion.setText(selectedd + "/" + selectedm + "/" + selectedy);
                                    }
                                }, mYear, mMonth, mDay);
                                mDatePicker.setTitle("Select date");
                                mDatePicker.show();

                            }
                        });

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        Calendar c = Calendar.getInstance();
                        final String curdate = sdf.format(c.getTime());

                        //AlertBox to add new task

                        AlertDialog.Builder alert = new AlertDialog.Builder(ScrollingActivity.this);
                        alert.setTitle("Add a New Task");
                        alert.setView(alertLayout);
                        alert.setCancelable(false);
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alert.setPositiveButton("ADD TASK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                taskname=(EditText)alertLayout.findViewById(R.id.taskname);
                                if (taskname.getText().toString().isEmpty())
                                {
                                   Toast.makeText(ScrollingActivity.this, "Field Cannot be empty..Please enter your task !!", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    tsknme = taskname.getText().toString();
                                    if(completion.getText().toString().isEmpty())
                                    {
                                        tskdate.equals("No date selected!");
                                    }
                                    else
                                    {
                                        tskdate = completion.getText().toString();
                                    }
                                    if (spin.getSelectedItem() == "--Select--")
                                    {
                                        tskpriority = "No priority";
                                    }
                                    else
                                    {
                                        tskpriority = spin.getSelectedItem().toString();
                                    }

                                    tskcurdate = curdate;
                                    taskstatus = status.getText().toString();

                                    myAsyncTask runner = new myAsyncTask();
                                    runner.execute();

                                }
                            }
                        });

                        AlertDialog dialog = alert.create();
                        dialog.show();
                    }

                });
            }

            //Function to Save data in Shared Preferences

            private void saveData()
            {
                SharedPreferences sp=getSharedPreferences("Myprefs",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                Gson getgson=new Gson();
                String nextstring=getgson.toJson(list,new TypeToken<ArrayList<Model>>(){}.getType());
                editor.putString("tasks",nextstring);
                editor.apply();
            }

            //Function to read data from Shared Preferences

            private void loadData()
            {
                SharedPreferences sp=getSharedPreferences("Myprefs",MODE_PRIVATE);
                Gson getgson=new Gson();
                String json=sp.getString("tasks",null);
                Type typelist=new TypeToken<ArrayList<Model>>(){}.getType();
                list=getgson.fromJson(json,typelist);
                if(list==null)
                {
                    list=new ArrayList<>();
                }
            }


       /*AsyncTask For Add a new Task*/

    private class myAsyncTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(myURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                TaskPL clsPl=new TaskPL();
                clsPl.Name=tsknme;
                clsPl.Date=tskcurdate;
                clsPl.DateOfCompletion=tskdate;
                clsPl.type=tskpriority;
                clsPl.Status=taskstatus;
                gson=new Gson();
                String requestjson=gson.toJson(clsPl);
                connection.setRequestProperty("device_id",deviceid);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("auth_key", "5C55560E-D48F-4DB4-9AAD-099A4B6BDC2F");
                connection.connect();
                try {
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.append(requestjson);
                    wr.flush();

                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder sb = new StringBuilder();
                    String inputLine = "";
                    while ((inputLine = reader.readLine()) != null) {
                        sb.append(inputLine);
                        break;
                    }
                    reader.close();
                    result= sb.toString();
                }
                finally
                {
                    connection.disconnect();
                }
            }
            catch(Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
                return result;

        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (pDialog.isShowing())
                pDialog.dismiss();
            TaskPL clsTL;
            gson1 = new Gson();
            clsTL = gson1.fromJson(result, TaskPL.class);
            if(clsTL.ErrorStatus==0){
                    try {
                        tid=clsTL.Id;
                        tname = clsTL.Name;
                        tcurdate =clsTL.Date;
                        tdoc = clsTL.DateOfCompletion;
                        tsktype = clsTL.type;
                        tstatus=clsTL.Status;
                            list.add(new Model("" + tid, "" + tname, "" + tdoc,
                                    "" + tsktype, "Date: " + tcurdate, "" + tstatus));
                            Toast.makeText(ScrollingActivity.this, "Task added Successfully!! ", Toast.LENGTH_LONG).show();
                            CustomAdapter adapters = new CustomAdapter(list, ScrollingActivity.this);
                            l1.setAdapter(adapters);
                            saveData();

                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
        else {
                Toast.makeText(ScrollingActivity.this,""+clsTL.Message,Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ScrollingActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
    }

    //Async Task to Update the existing task

    private class updatetask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(myURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                TaskPL clsPl=new TaskPL();
                clsPl.Id=Integer.parseInt(lineid);
                clsPl.Name=tsknmeupdate;
                clsPl.Date=tskcurdateupdate;
                clsPl.DateOfCompletion=tskdateupdate;
                clsPl.type=tskpriorityupdate;
                clsPl.Status=taskstatus;
                gson=new Gson();
                String requestjson=gson.toJson(clsPl);
                connection.setRequestProperty("device_id",deviceid);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("auth_key", "5C55560E-D48F-4DB4-9AAD-099A4B6BDC2F");
                connection.connect();
                try {
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.append(requestjson);
                    wr.flush();

                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder sb = new StringBuilder();
                    String inputLine = "";
                    while ((inputLine = reader.readLine()) != null) {
                        sb.append(inputLine);
                        break;
                    }
                    reader.close();
                    result= sb.toString();
                }
                finally
                {
                    connection.disconnect();
                }
            }
            catch(Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return result;

        }

        @Override
        protected void onPostExecute(String str) {
            //Log.i("INFO", str);
            super.onPostExecute(str);
            TaskPL clsTL;
            gson1 = new Gson();
            clsTL = gson1.fromJson(result, TaskPL.class);
            if(clsTL.ErrorStatus==0){
                try {
                    tid = clsTL.Id;
                    tname = clsTL.Name;
                    tcurdate = clsTL.Date;
                    tdoc = clsTL.DateOfCompletion;
                    tsktype = clsTL.type;
                    tstatus = clsTL.Status;
                        list.set(pos, new Model("" + tid, "" + tname, "" + tdoc, "" + tsktype, "Date: " + tcurdate, "" + tstatus));
                        Toast.makeText(ScrollingActivity.this, "Task updated Successfully!! ", Toast.LENGTH_LONG).show();
                        CustomAdapter adapters = new CustomAdapter(list, ScrollingActivity.this);
                        l1.setAdapter(adapters);
                        saveData();

                }

                catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else {
                Toast.makeText(ScrollingActivity.this,""+clsTL.Message,Toast.LENGTH_SHORT).show();
            }
        }
    }


            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu_scrolling, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_settings) {

                    return true;
                }
                return super.onOptionsItemSelected(item);
            }


            /*AsyncTask for Get Details of the task*/

     private class ViewTask extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://tsmith.co.in/task/api/Details/Id/" + lineid);
                HttpURLConnection getconnection = (HttpURLConnection) url.openConnection();
                getconnection.setRequestMethod(REQUEST_METHOD2);
                getconnection.setReadTimeout(READ_TIMEOUT);
                getconnection.setConnectTimeout(CONNECTION_TIMEOUT);
                getconnection.setRequestProperty("Content-Type", "application/json");
                getconnection.setRequestProperty("auth_key", "5C55560E-D48F-4DB4-9AAD-099A4B6BDC2F");
                getconnection.connect();
                try {

                    InputStreamReader streamReader = new InputStreamReader(getconnection.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder sb = new StringBuilder();
                    String inputLine = "";
                    while ((inputLine = reader.readLine()) != null) {
                        sb.append(inputLine);
                        break;
                    }
                    reader.close();
                    result= sb.toString();
                }
                finally
                {
                    getconnection.disconnect();
                }
            }
            catch(Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return result;

        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            update.setEnabled(true);
            update.setBackgroundColor(Color.parseColor("#ce1141"));
            if (pDialog.isShowing())
                pDialog.dismiss();
            Details clssPL;
            gson1 = new Gson();
            clssPL = gson1.fromJson(result, Details.class);
            if(clssPL.ErrorStatus==0){
                try {
                    lst=clssPL.lst;
                    for(int i=0;i<lst.size();i++) {
                        TaskPL x = lst.get(i);
                        display_taskname.setText(x.Name);
                        display_taskdoc.setText(x.DateOfCompletion);
                        display_tasktype.setText(x.type);
                        display_status.setText(x.Status);

                    }
                }

                catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else {
                Toast.makeText(ScrollingActivity.this,""+clssPL.Message,Toast.LENGTH_SHORT).show();
            }
        }
         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             // Showing progress dialog
             pDialog = new ProgressDialog(ScrollingActivity.this);
             pDialog.setMessage("Please wait...");
             pDialog.setCancelable(true);
             pDialog.show();

         }
    }

             //AsyncTask to delete task

    private class deletetask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://tsmith.co.in/task/api/DeleteTask");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                TaskDeletePL delclspl=new TaskDeletePL();
                delclspl.Id=Integer.parseInt(lineid);
                gson=new Gson();
                String requestjson=gson.toJson(delclspl);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("auth_key", "5C55560E-D48F-4DB4-9AAD-099A4B6BDC2F");
                connection.connect();
                try {
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.append(requestjson);
                    wr.flush();

                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder sb = new StringBuilder();
                    String inputLine = "";
                    while ((inputLine = reader.readLine()) != null) {
                        sb.append(inputLine);
                        break;
                    }
                    reader.close();
                    result= sb.toString();
                }
                finally
                {
                    connection.disconnect();
                }
            }
            catch(Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return result;

        }

        @Override
        protected void onPostExecute(String str) {
            //Log.i("INFO", str);
            super.onPostExecute(str);
           TaskDeletePL delclsTL;
            gson1 = new Gson();
            delclsTL = gson1.fromJson(result, TaskDeletePL.class);
            if(delclsTL.ErrorStatus==0){
                try {
                    tid=delclsTL.Id;
                    tname = delclsTL.Name;
                    tcurdate =delclsTL.Date;
                    tdoc = delclsTL.DateOfCompletion;
                    tsktype = delclsTL.type;
                    tstatus=delclsTL.Status;
                    tcanceled=delclsTL.Cancelled;
                    list.remove(pos1);
                    CustomAdapter adapters = new CustomAdapter(list, ScrollingActivity.this);
                    l1.setAdapter(adapters);
                    saveData();

                }

                catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else {
                Toast.makeText(ScrollingActivity.this,""+delclsTL.Message,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Function to do on backpress..Exit from the app

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
        builder.setMessage("Are you sure want to exit from the application???");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
