package org.example.model.fileExport;

import org.example.model.database.entity.Employee;
import org.example.model.database.service.EmployeeService;
import java.util.List;

/**
 * A utility for importing employee data from Excel into the database
 * and exporting it back to Excel.
 *
 * Before use, it must be initialized with an existing EmployeeService
 * so it can access the database.
 *
 * Return codes for Excel import/export:
 * -1 → service not initialized
 * -2 → database error
 * -3 → no valid records in Excel
 * >=0 → number of successfully processed employees
 */
public class EmplExelToDB {
    private static EmployeeService ser = null;
    public static void init(EmployeeService service){
        ser = service;
    }
    public static int toExel(String filePath, int compId){
        if (ser == null){
            return -1;
        }
        try {
            List<Employee> list = ser.getEmployeesByCompanyId(compId);
            EmplExelReader.exportToExcel(list, filePath);
            return list.size();
        }
        catch (Exception e){
            return -2;
        }
    }
    public static int fromExel(String filePath, int compId){
        if (ser == null){
            return -1;
        }
        List<Employee> list = EmplExelReader.importFromExcel(filePath, compId);
        if (list.isEmpty()){
            return -3;
        }
        int cnt = 0;

        for (int i = 0; i < list.size(); i++) {
            try {
                ser.addEmployee(list.get(i));
                cnt++;
            }
            catch (Exception e){
            }
        }
        return cnt;
    }
    public static int toExel(int compId){
        return toExel("emploees.xlsx", compId);
    }
    public static int fromExel(int compId){

        return fromExel("emploees.xlsx", compId);
    }
}
