package com.example.task;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TaskPL {
    public TaskPL(){
        Id=0;
        Name= "";
        Date= "";
        TS= "";
        DateOfCompletion= "";
        type= "";
        Status= "";
        ErrorStatus= 0;
        Message= "";
    }

    public int Id;
    public String Name;
    public String Date;
    public String TS;
    public String DateOfCompletion;
    public String type;
    public String Status;
    public int ErrorStatus;
    public String Message;

}
