package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.AddressDTO;
import com.arius.ecommerce.dto.UserDTO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserExcelExportUtil {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private List<UserDTO> listUsers;

    public UserExcelExportUtil(List<UserDTO> listUsers) {
        this.listUsers = listUsers;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("user");
    }

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        Cell cell = row.createCell(0);
        cell.setCellValue("User ID");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(0);

        cell = row.createCell(1);
        cell.setCellValue("First Name");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(1);

        cell = row.createCell(2);
        cell.setCellValue("Last Name");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(2);

        cell = row.createCell(3);
        cell.setCellValue("Email");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(3);

        cell = row.createCell(4);
        cell.setCellValue("Address");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(4);

        cell = row.createCell(5);
        cell.setCellValue("Phone Number");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(5);

    }

    private void writeDataRow() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        int rowCount = 1;
        for (UserDTO user : listUsers) {
            Row row = sheet.createRow(rowCount++);

            Cell cell = row.createCell(0);
            cell.setCellValue(user.getUserId());
            cell.setCellStyle(style);
            sheet.autoSizeColumn(0);

            cell = row.createCell(1);
            cell.setCellValue(user.getFirstName());
            cell.setCellStyle(style);
            sheet.autoSizeColumn(1);

            cell = row.createCell(2);
            cell.setCellValue(user.getLastName());
            cell.setCellStyle(style);
            sheet.autoSizeColumn(2);

            cell = row.createCell(3);
            cell.setCellValue(user.getEmail());
            cell.setCellStyle(style);
            sheet.autoSizeColumn(3);

            // Concatenate addresses into a single string
            List<String> addressStrings = user.getAddresses().stream()
                    .map(AddressDTO::toString)
                    .collect(Collectors.toList());
            String concatenatedAddresses = String.join(", ", addressStrings);
            cell = row.createCell(4);
            cell.setCellValue(concatenatedAddresses);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(4);

            cell = row.createCell(5);
            cell.setCellValue(user.getMobileNumber());
            cell.setCellStyle(style);
            sheet.autoSizeColumn(5);
        }
    }

    public void export(HttpServletResponse response) {
        writeHeaderRow();
        writeDataRow();

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
