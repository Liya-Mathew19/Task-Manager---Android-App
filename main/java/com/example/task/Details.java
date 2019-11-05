package com.example.task;

import java.util.ArrayList;
import java.util.List;

public class Details {
    public Details()
    {
        lst = new ArrayList<TaskPL>();
        ErrorStatus= 0;
        Message= "";
    }

    public List<TaskPL> lst;
    public int ErrorStatus;
    public String Message;
}
