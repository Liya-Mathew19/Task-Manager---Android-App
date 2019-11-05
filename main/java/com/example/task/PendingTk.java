package com.example.task;

import java.util.ArrayList;
import java.util.List;

public class PendingTk {
    public PendingTk() {
        list = new ArrayList<TaskPL>();
        ErrorStatus = 0;
        Message = "";
    }

    public List<TaskPL> list;
    public int ErrorStatus;
    public String Message;
}

