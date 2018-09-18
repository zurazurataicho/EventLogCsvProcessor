package org.zura.EventLogCsvProcessor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Properties;
import java.lang.Thread;


public class Main {
    private static final String PROPERTIES_FILENAME = ".properties";

    public static void main(String[] args) {
        try {
            // jstat用キー待ち
//            Thread.sleep(30000);

            Properties prop = new Properties();
            prop.load(Files.newBufferedReader(Paths.get(PROPERTIES_FILENAME), StandardCharsets.UTF_8));
            CsvProcessor proc = new CsvProcessor(prop);
            proc.run();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

