import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CSVReader {
    private BufferedReader reader;
    private String delimiter;
    private boolean hasHeader;
    private List<String> header;

    List<String> columnLabels = new ArrayList<>();
    Map<String,Integer> columnLabelsToInt = new HashMap<>();

    String[] current;


    /**
     * @param filename  - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */

    public CSVReader(String filename, String delimiter, boolean hasHeader) throws IOException {
        this(new FileReader(filename), delimiter, hasHeader);
    }

    public CSVReader(String filename, String delimiter) throws IOException {
        this(filename, delimiter, false);
    }

    public CSVReader(String filename) throws IOException {
        this(filename, ",", false);
    }

    public CSVReader(Reader reader, String delimiter, boolean hasHeader) {
        this.reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;

        if (hasHeader) {
            parseHeader();
        }
    }

    public CSVReader(String filename, String delimiter, boolean hasHeader, Charset charset) throws IOException {
        this(new InputStreamReader(new FileInputStream(filename), charset), delimiter, hasHeader);
    }

    void parseHeader() {
        try {
            String line = reader.readLine();
            if (line == null) {
                return;
            }

            String[] header = line.split(delimiter);

            for (int i = 0; i < header.length; i++) {
                String colName = header[i];
                columnLabels.add(colName);
                columnLabelsToInt.put(colName, i);
            }

        } catch (IOException e) {
            throw new RuntimeException("Błąd", e);
        }
    }

    public boolean next() {
        try {
            String line = reader.readLine();
            if (line == null) {
                current = null;
                return false;
            }

            current = splitCSV(line, delimiter);

            for (int i = 0; i < current.length; i++) {
                current[i] = current[i].replaceAll("^\"|\"$", "");
            }

            return true;

        } catch (IOException e) {
            current = null;
            return false;
        }
    }

    public List<String> getColumnLabels() {
        return columnLabels;
    }

    public int getRecordLength() {
        return current == null ? 0 : current.length;
    }


    public boolean isMissing(int columnIndex) {
        if (current == null || columnIndex < 0 || columnIndex >= current.length)
            return true;

        return current[columnIndex].isEmpty();
    }

    public boolean isMissing(String columnLabel) {
        Integer idx = columnLabelsToInt.get(columnLabel);
        if (idx == null) return true;
        return isMissing(idx);
    }

    public String get(int columnIndex) {
        if (isMissing(columnIndex))
            return "";
        return current[columnIndex];
    }

    public String get(String columnLabel) {
        Integer idx = columnLabelsToInt.get(columnLabel);
        if (idx == null) return "";
        return get(idx);
    }

    public int getInt(int columnIndex) {
        try {
            return Integer.parseInt(get(columnIndex));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getInt(String columnLabel) {
        try {
            return Integer.parseInt(get(columnLabel));
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(int columnIndex) {
        try {
            return Long.parseLong(get(columnIndex));
        } catch (Exception e) {
            return 0L;
        }
    }

    public long getLong(String columnLabel) {
        try {
            return Long.parseLong(get(columnLabel));
        } catch (Exception e) {
            return 0L;
        }
    }

    public double getDouble(int columnIndex) {
        try {
            return Double.parseDouble(get(columnIndex));
        } catch (Exception e) {
            return 0.0;
        }

    }

    public double getDouble(String columnLabel) {
        try {
            return Double.parseDouble(get(columnLabel));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String[] splitCSV(String line, String delimiter) {
        String regex = String.format("%s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", Pattern.quote(delimiter));
        return line.split(regex, -1);
    }

    public LocalTime getTime(int columnIndex, String format) {
        String value = get(columnIndex);
        if (isMissing(columnIndex)) return null;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalTime.parse(value, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public LocalTime getTime(String columnLabel, String format) {
        Integer idx = columnLabelsToInt.get(columnLabel);
        if (idx == null) return null;
        return getTime(idx, format);
    }

}
