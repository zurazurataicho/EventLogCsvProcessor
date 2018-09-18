package org.zura.EventLogCsvProcessor;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.lang.Exception;
import java.io.IOException;

public class CsvManager {
    private BufferedReader csvIn;
    CsvManager(String fileName, Charset charset) throws IOException {
        csvIn = Files.newBufferedReader(Paths.get(fileName), charset);
    }
    public Iterable<CSVRecord> read() throws IOException {
        return CSVFormat.RFC4180.withHeader().parse(csvIn);
    }
}
