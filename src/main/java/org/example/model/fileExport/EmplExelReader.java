package org.example.model.fileExport;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.database.entity.Employee;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmplExelReader {
    protected static List<Employee> importFromExcel(String filePath, int compId) {
        List<Employee> load = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = null;
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    name = cell.getStringCellValue();
                    if (name.isBlank()){
                        continue;
                    }
                } else {
                    continue;
                }

                String surname = null;
                cell = row.getCell(1);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    surname = cell.getStringCellValue();
                    if (surname.isBlank()){
                        continue;
                    }
                } else {
                    continue;
                }

                String mail = null;
                cell = row.getCell(2);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    mail = cell.getStringCellValue();
                } else {
                    continue;
                }

                Integer age = null;
                cell = row.getCell(3);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    age = (int) cell.getNumericCellValue();
                    if (age < 16 || age > 80){
                        continue;
                    }
                } else {
                    continue;
                }

                String position = null;
                cell = row.getCell(4);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    position = cell.getStringCellValue();
                } else {
                    continue;
                }

                BigDecimal salary = null;
                cell = row.getCell(5);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    salary = BigDecimal.valueOf(cell.getNumericCellValue());
                    if (salary.compareTo(BigDecimal.ZERO) <= 0){
                        continue;
                    }
                }
                else {
                    continue;
                }

                OffsetDateTime hireDate = null;
                cell = row.getCell(6);
                if (cell != null && cell.getCellType() == CellType.NUMERIC
                        && DateUtil.isCellDateFormatted(cell)) {
                    hireDate = cell.getLocalDateTimeCellValue().atOffset(ZoneOffset.UTC);
                }
                else {
                    continue;
                }

                Employee emp = new Employee(compId, name, surname,mail, age, salary, position, hireDate);
                load.add(emp);

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  load;
    }

    protected static void exportToExcel(List<Employee> save,String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Employees");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Surname");
            header.createCell(2).setCellValue("Mail");
            header.createCell(3).setCellValue("Age");
            header.createCell(4).setCellValue("Position");
            header.createCell(5).setCellValue("Salary");
            header.createCell(6).setCellValue("Hire Date");

            int rowNum = 1;

            for (Employee emp : save) {

                Row row = sheet.createRow(rowNum);
                rowNum++;

                row.createCell(0).setCellValue(emp.getName());
                row.createCell(1).setCellValue(emp.getSurname());
                row.createCell(2).setCellValue(emp.getEmail());
                row.createCell(3).setCellValue(emp.getAge());
                row.createCell(4).setCellValue(emp.getPosition());
                row.createCell(5).setCellValue(emp.getSalary().doubleValue());

                OffsetDateTime hireDate = emp.getHiredAt();
                if (hireDate != null) {
                    Cell cell = row.createCell(6);
                    cell.setCellValue(Date.from(hireDate.toInstant()));

                    CellStyle dateStyle = workbook.createCellStyle();
                    CreationHelper createHelper = workbook.getCreationHelper();
                    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm"));
                    cell.setCellStyle(dateStyle);
                }
            }
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
