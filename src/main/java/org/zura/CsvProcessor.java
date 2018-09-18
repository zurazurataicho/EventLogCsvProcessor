package org.zura.EventLogCsvProcessor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.commons.csv.CSVRecord;


public class CsvProcessor {
    private Optional<Map<String, String>> optLogOnOff = null;
    private Optional<Map<String, String>> optPowOnOff = null;
    private Properties prop = null;
    private Charset charset = null;
    private EventListProcessor eventProc = null;

    CsvProcessor(Properties prop) {
        this.prop = prop;
        charset = Charset.forName(prop.getProperty("CSV_CHARSET"));
        eventProc = new EventListProcessor(this.prop);
    }

    /**
     * フィルタリング共通処理
     */
    private TreeMap<String, String> extractEvent(final String csvFilename, final List<String> eventIds) throws IOException, FileNotFoundException {
        final CsvManager csvManager = new CsvManager(prop.getProperty(csvFilename), charset);
        List<String> targetIds = new ArrayList<>();
        eventIds.forEach(str -> targetIds.add(prop.getProperty(str)));
        String colNameKey = prop.getProperty("EVENT_COLUMN_NAME_RECORD_DATETIME");
        String colNameValue = prop.getProperty("EVENT_COLUMN_NAME_EVENT_ID");
        return EventFilter.exec(csvManager, targetIds, colNameKey, colNameValue);
    }

    /**
     * ログオン・ログオフ抽出
     */
    private void extractLogonLogoffEvent() throws IOException, FileNotFoundException {
        List<String> idList = Arrays.asList("ID_LOGON_EVENT", "ID_LOGOFF_EVENT");
        optLogOnOff = Optional.ofNullable(extractEvent("LOGON_LOGOFF_FILENAME", idList));
    }

    /**
     * 電源オン・オフ抽出
     */
    private void extractPowerOnOffEvent() throws IOException, FileNotFoundException {
        List<String> idList = Arrays.asList("ID_POWER_ON_EVENT", "ID_POWER_OFF_EVENT");
        optPowOnOff = Optional.ofNullable(extractEvent("POWER_ON_OFF_FILENAME", idList));
    }

    /**
     * Excelワークシート作成
     */
    private void createEventList() throws IOException {
        List<String> columns = Arrays.asList("POWER_ON_EVENT", "LOGON_EVENT", "LOGOFF_EVENT", "POWER_OFF_EVENT");
        eventProc.prepare(columns);
    }

    /**
     * ワークシートにイベントログを追加
     */
    private void addEventList(Optional<Map<String, String>> optEventList) throws IOException {
        optEventList.ifPresent(ev -> eventProc.append(ev));
    }

    /**
     * Excelワークシート保存
     */
    private void saveEventList() throws IOException {
        eventProc.save(prop.getProperty("BOOK_FILENAME"));
    }

    /**
     * イベントログを日時順にする
     */
    private void adjustEventListByDatetime() throws IOException {
    }

    /**
     * 抽出処理
     */
    public void run() throws IOException, FileNotFoundException {
        // ログ抽出
        extractLogonLogoffEvent();
        extractPowerOnOffEvent();
        // Excelシート処理
        createEventList();
        addEventList(optLogOnOff);
        addEventList(optPowOnOff);
        // adjustEventListByDatetime();
        saveEventList();
        /* test
        optLogOnOff.ifPresent(ev -> ev.forEach((key, val) -> System.out.println("key=" + key + ", val=" + val)));
        optPowOnOff.ifPresent(ev -> ev.forEach((key, val) -> System.out.println("key=" + key + ", val=" + val)));
        */
    }
}
