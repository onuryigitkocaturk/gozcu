package com.onuryigitkocaturk.query_monitor.export;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class PdfExportService {

    private static final DateTimeFormatter EXPORTED_AT_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("tr", "TR"));

    // fontun sığması için her kolona ayrılan genişlik
    private static final float MIN_COLUMN_WIDTH = 65f;

    public byte[] toPdf(String queryName, List<Map<String, Object>> rows) {
        Set<String> columns = new LinkedHashSet<>();
        for (Map<String, Object> row : rows) {
            columns.addAll(row.keySet());
        }

        // kolon sayısı arttıkça sayfayı genişlet
        Rectangle landscapeA4 = PageSize.A4.rotate();
        float pageWidth = Math.max(landscapeA4.getWidth(), columns.size() * MIN_COLUMN_WIDTH);
        Rectangle pageSize = new Rectangle(pageWidth, landscapeA4.getHeight());

        Document document = new Document(pageSize, 24, 24, 24, 24);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10);
            // kolon sayisi arttikca yazi tipini kucult, yoksa yine sigmaz
            float dataFontSize = columns.size() > 20 ? 6f : columns.size() > 10 ? 7.5f : 9f;
            Font headerFont = new Font(Font.HELVETICA, dataFontSize, Font.BOLD);
            Font cellFont = new Font(Font.HELVETICA, dataFontSize);

            document.add(new Paragraph("Sorgu: " + queryName, titleFont));
            document.add(new Paragraph("Alınma zamanı: " + EXPORTED_AT_FORMATTER.format(LocalDateTime.now()), normalFont));
            document.add(new Paragraph(" "));

            if (columns.isEmpty()) {
                document.add(new Paragraph("Eşleşen kayıt yok.", normalFont));
            } else {
                PdfPTable table = new PdfPTable(columns.size());
                table.setWidthPercentage(100);

                for (String column : columns) {
                    PdfPCell headerCell = new PdfPCell(new Paragraph(column, headerFont));
                    headerCell.setPadding(3f);
                    table.addCell(headerCell);
                }

                for (Map<String, Object> row : rows) {
                    for (String column : columns) {
                        Object value = row.get(column);
                        PdfPCell cell = new PdfPCell(new Paragraph(value == null ? "" : String.valueOf(value), cellFont));
                        cell.setPadding(3f);
                        table.addCell(cell);
                    }
                }

                document.add(table);
            }
        } catch (DocumentException e) {
            throw new IllegalStateException("PDF dosyası oluşturulamadı", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }
}
