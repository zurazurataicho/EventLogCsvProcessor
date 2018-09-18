package org.zura.EventLogCsvProcessor;

import java.io.File.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class EventListProcessor {
    private Properties prop = null;
    private Workbook eventBook = new SXSSFWorkbook();
    private Font eventFont = null;
    private DataFormat eventFormat = null;
    private Sheet eventSheet = null;
    private Integer currentLine = 0;
    private CellStyle styleDateTime = null;
    private CellStyle styleEventMark = null;
    private CellStyle styleEventId = null;
    private String eventSymbol = null;
    private Map<String, String> columnSequence = null;
    private List<String> columnNames = new ArrayList<>();
    private List<String> columnEvenIds = new ArrayList<>();

    EventListProcessor(Properties prop) {
        this.prop = prop;
        eventFont = eventBook.createFont();
        eventFormat = eventBook.createDataFormat();
        eventSheet = eventBook.createSheet();
        if (eventSheet instanceof SXSSFSheet) {
            ((SXSSFSheet)eventSheet).trackAllColumnsForAutoSizing();
        }
        eventBook.setSheetName(0, prop.getProperty("SHEET_NAME"));
        styleDateTime = eventBook.createCellStyle();
        styleEventMark = eventBook.createCellStyle();
        styleEventId = eventBook.createCellStyle();
        eventSymbol = prop.getProperty("SYMBOL_CHECKED");
    }

    private void setHeaderStyles(CellStyle style) {
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private void setHeaderText(CellStyle style) {
        Integer column = 0;
        String colNameDatetime = prop.getProperty("EVENT_COLUMN_NAME_RECORD_DATETIME");
        String colNameEventId = prop.getProperty("EVENT_COLUMN_NAME_EVENT_ID");

        Row row = eventSheet.createRow(0);
        Cell cell;

        cell = row.createCell(column++);
        cell.setCellStyle(style);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(prop.getProperty(colNameDatetime));

        for (String name : columnNames) {
            cell = row.createCell(column);
            cell.setCellStyle(styleEventMark);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(name);
            column += 1;
        }

        cell = row.createCell(column);
        cell.setCellStyle(style);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(prop.getProperty(colNameEventId));

        // 先頭行固定
        eventSheet.createFreezePane(1, 1);
        // ヘッダ行のオートフィルタ設定
        eventSheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 1));
        // 列幅自動調整
        eventSheet.autoSizeColumn(0, true);
        eventSheet.autoSizeColumn(1, true);
    }

    private void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void setBodyStyles(CellStyle style, String format) {
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setFont(eventFont);
        if (format != null) {
            style.setDataFormat(eventFormat.getFormat(format));
        }
    }

    private void setBodyRow(String dateTime, Integer eventId) {
        Integer column = 0;
        currentLine += 1;

        Cell cell;
        Row row = eventSheet.createRow(currentLine);

        cell = row.createCell(column++);
        cell.setCellStyle(styleDateTime);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(dateTime);

        for (String id : columnEvenIds) {
            cell = row.createCell(column);
            cell.setCellStyle(styleEventMark);
            cell.setCellType(CellType.STRING);
            if (Integer.parseInt(id) == eventId) {
                cell.setCellValue(eventSymbol);
            } else {
                cell.setCellValue("");
            }
            column += 1;
        }

        cell = row.createCell(column);
        cell.setCellStyle(styleEventId);
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(eventId);
    }

    public void prepare(List<String> columns) {
        this.columnSequence = columnSequence;

        eventFont.setFontName(prop.getProperty("SHEET_FONT"));
        eventFont.setFontHeightInPoints(Short.parseShort(prop.getProperty("SHEET_FONT_POINT")));

        // カラム名リスト(ヘッダ表示用)と、イベントIDリスト(イベントマーク用)
        for (String column : columns) {
            columnNames.add(prop.getProperty("COLUMN_NAME_" + column));
            columnEvenIds.add(prop.getProperty("ID_" + column));
        }

        // ヘッダ
        CellStyle styleHeader = eventBook.createCellStyle();
        setBorders(styleHeader);
        setHeaderStyles(styleHeader);
        setBodyStyles(styleHeader, null);
        setHeaderText(styleHeader);

        // 日時表示用スタイル
        setBorders(styleDateTime);
        setBodyStyles(styleDateTime, prop.getProperty("SHEET_FORMAT_DATETIME"));

        // 文字列用スタイル
        setBorders(styleEventMark);

        // 整数用スタイル
        setBorders(styleEventId);
        setBodyStyles(styleEventId, prop.getProperty("SHEET_FORMAT_DATA"));
    }

    public void append(Map<String, String> cellData) {
        cellData.forEach((k, v) -> setBodyRow(k, Integer.parseInt(v)));
    }

    public void save(String saveFilename) throws IOException, FileNotFoundException {
        FileOutputStream fout = new FileOutputStream(saveFilename);
        eventBook.write(fout);
    }
}
