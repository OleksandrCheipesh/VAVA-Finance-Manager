package org.example.model.fileExport;

import org.example.model.database.service.EmployeeService;

public class EmplExelToDB {
    private EmployeeService service;
    private static EmplExelToDB me;
    public static void init(EmployeeService service){
        me = new EmplExelToDB(service);
    }
    private EmplExelToDB(EmployeeService service){
        this.service = service;
    }
    public static int toExel(String filePath, int compId){

        return 0;
    }
    public static int fromExel(String filePath, int compId){

        return 0;
    }
}
