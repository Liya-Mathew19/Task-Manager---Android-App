package com.example.task;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class PendingActivity extends AppCompatActivity {
    EditText fromdate,todate;
    private ProgressDialog pDialog;
    String taskfrom;
    String taskto;
    public static final int READ_TIMEOUT=15000;
    public static final int CONNECTION_TIMEOUT=50000;
    String result="";
    Gson gson;
    ListView l2;
    TextView currentdate,t_name,tdate,type,taskid;
    ArrayList<Model> list;
    Button applyfilter;
    PendingCustomAdapter adapter;
    String deviceid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.pending_activity);
        final Telephony telephony=new Telephony(PendingActivity.this);
        if(ContextCompat.checkSelfPermission(PendingActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }
        else
        {
            deviceid=telephony.GetDeviceUniqueId();
        }
        list=new ArrayList<>();
        l2=(ListView)findViewById(R.id.pendinglistview);
        adapter = new PendingCustomAdapter(list,PendingActivity.this);
        applyfilter =(Button)findViewById(R.id.applyfilter);
        fromdate=(EditText)findViewById(R.id.fromdate);
        todate=(EditText)findViewById(R.id.todate);
        l2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, final long id) {
                l2.setAdapter(adapter);
                currentdate = (TextView) view.findViewById(R.id.curdate);
                t_name = (TextView) view.findViewById(R.id.task);
                type = (TextView) view.findViewById(R.id.priority);
                tdate = (TextView) view.findViewById((R.id.dateofcompletion));
                taskid = (TextView) view.findViewById(R.id.taskid);
            }
            });

        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                final int mYear = mcurrentDate.get(Calendar.YEAR);
                final int mMonth = mcurrentDate.get(Calendar.MONTH);
                final int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(PendingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        int year=selectedyear;
                        int month=selectedmonth+1;
                        int day=selectedday;
                        fromdate.setText(day + "/" + month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                final int mYear = mcurrentDate.get(Calendar.YEAR);
                final int mMonth = mcurrentDate.get(Calendar.MONTH);
                final int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(PendingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        int year=selectedyear;
                        int month=selectedmonth+1;
                        int day=selectedday;
                        todate.setText(day + "/" + month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        applyfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskfrom=fromdate.getText().toString();
                taskto=todate.getText().toString();
                if(taskfrom.isEmpty()){
                    fromdate.setError("Select a date..");
                }
                else if(taskto.isEmpty()){
                    todate.setError("Select a date..");
                }
                else {
                    pendingList pending = new pendingList();
                    pending.execute();
                }
            }
        });
    }

    private class pendingList extends AsyncTask<String,String,String > {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://tsmith.co.in/task/api/PendingTask/Status/Not Completed");
                HttpURLConnection getconnection = (HttpURLConnection) url.openConnection();
                getconnection.setRequestMethod("GET");
                getconnection.setReadTimeout(READ_TIMEOUT);
                getconnection.setConnectTimeout(CONNECTION_TIMEOUT);
                getconnection.setRequestProperty("device_id",deviceid);
                getconnection.setRequestProperty("Content-Type", "application/json");
                getconnection.setRequestProperty("auth_key", "5C55560E-D48F-4DB4-9AAD-099A4B6BDC2F");
                getconnection.setRequestProperty("from_date",taskfrom);
                getconnection.setRequestProperty("to_date",taskto);
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            PendingTk pPL;
            gson = new Gson();
            pPL = gson.fromJson(result, PendingTk.class);
             if(pPL.ErrorStatus==0){
                    try {
                       // l2.setVisibility(View.VISIBLE);
                        JSONObject obj=new JSONObject(str);
                        JSONArray arr=obj.getJSONArray("list");
                        for(int i=0;i< arr.length();i++) {
                           JSONObject aobj = arr.getJSONObject(i);
                           list.add(new Model(aobj.getString("Id"),""+aobj.getString("Name"),
                                  "Date of Completion : " +aobj.getString("DateOfCompletion"),"Priority Level : "+aobj.getString("type"),
                                   "Date : "+aobj.getString("Date"),"Status : "+aobj.getString("Status")));
                            l2.setAdapter(adapter);
                    }
                }
                    catch (Exception e) {
                        e.printStackTrace();
                }

            }
            else {
                Toast.makeText(PendingActivity.this,""+pPL.Message,Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(ScrollingActivity.this,""+listid,Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PendingActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

    }
    }
