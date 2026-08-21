package com.onuryigitkocaturk.query_monitor.export;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ExcelExportService {

    private static final DateTimeFormatter EXPORTED_AT_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("tr", "TR"));

    public byte[] toExcel(String queryName, List<Map<String, Object>> rows) {

        try (XSSFWorkbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Sonuçlar");

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);

            // hangi sorgunun sonucu
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Sorgu: " + queryName);
            titleCell.setCellStyle(titleStyle);

            Row exportedAtRow = sheet.createRow(1);
            exportedAtRow.createCell(0).setCellValue("Alınma zamanı: " + EXPORTED_AT_FORMATTER.format(LocalDateTime.now()));

            // 2. satır boş 3. satırdan devam
            Set<String> columns = new LinkedHashSet<>();
            for (Map<String,Object> row : rows) {
                columns.addAll(row.keySet());
            }
            Row headerRow = sheet.createRow(3);
            int colIndex = 0;
            for (String column : columns) {
                Cell headerCell = headerRow.createCell(colIndex++);
                headerCell.setCellValue(column);
                headerCell.setCellStyle(titleStyle);
            }

            int rowIndex = 4;
            for (Map<String, Object> row : rows) {
                Row excelRow = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (String column : columns) {
                    Cell cell = excelRow.createCell(colIndex++);
                    setCellValue(cell, row.get(column));
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Excel dosyası oluşturulamadı", e);
        }
    }

    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }
}
