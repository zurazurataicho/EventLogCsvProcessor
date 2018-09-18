package org.zura.EventLogCsvProcessor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Properties;
import org.apache.commons.csv.CSVRecord;

public class EventFilter {
    public static TreeMap<String, String> exec(final CsvManager csvManager, final List<String> filterIds, String colNameKey, String colNameValue) throws IOException {
        TreeMap<String, String> eventMap = new TreeMap<>();
        TreeMap<String, String> tempMap = new TreeMap<>();
        for (final CSVRecord record : csvManager.read()) {
            // カラム名の前後に空白文字がある場合があるので除去したMapを生成
            record.toMap().keySet().forEach(_key -> tempMap.put(_key.trim(), record.get(_key)));
            // 記録時刻をキーとしたMapを生成
            final String key = tempMap.get(colNameKey);
            final String val = tempMap.get(colNameValue);
            if (!filterIds.contains(val)) {
                continue;
            }
            eventMap.put(key, val);
        }
        return eventMap;
    }
}
