package com.example.task;

public class Model {
    String taskid;
    String task_name;
    String date;
    String type;
    String tdate;
    String tstatus;

public Model(String taskid, String task_name, String date, String type, String tdate,String tstatus){
    this.taskid=taskid;
    this.task_name=task_name;
    this.date=date;
    this.type=type;
    this.tdate=tdate;
    this.tstatus=tstatus;
}
public String gettaskId(){
    return taskid;
}
    public String getTask_name() {
        return task_name;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
    public String getcurDate(){
        return tdate;
    }
    public String getStatus(){
    return tstatus;
    }
}



