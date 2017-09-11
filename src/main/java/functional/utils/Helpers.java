package functional.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Helpers {
    FileUtil fileUtil = new FileUtil();
    BatchUtil batchUtil = new BatchUtil();

    public boolean compareCountsOfDBAndLog(List table, String logFile){
        boolean verdict = true;
        if(table.size() != getCountFromLogFile(logFile)) {
            verdict = false;
        }
        return verdict;
    }

    /**
     * Counts the number of records/lines in a text file
     * @param file to read from, absolute path
     * @return number of records
     */
    public int getCountFromLogFile(String file){
        String fileContents = fileUtil.getFileContents(file);
        return fileContents.split("\n").length;
    }

    /**
     * Compares the values from DB table with those in text file
     * @param dbData - List of maps containing table records
     * @param column - List of columns to be compared
     * @param logFile - text file to be compared against
     * @param field - position of field in the log file to be compared
     * @param separator - indicates how values are separated in the text file (space, tab, comma, etc)
     * @return true if both counts and all values match, false otherwise
     */
    public boolean compareDbAndValuesFromLog(List dbData, String column, String logFile,int field, String separator){
        ArrayList logData = getNumberOfRecordsFromLogFile(logFile, field, separator);

        boolean verdict = true;
        try {
            int logCount = logData.size();
            int dbCount = dbData.size();
            if (logCount != dbCount)
                verdict = false;
            if (logCount > 0 && dbCount > 0) {
                for (int i = 0; i < logCount - 1; i++) {
                    Map dbEntry = (Map) dbData.get(i);
                    String value = (String)dbEntry.get(column);
                    if (value != logData.get(i))
                       verdict = false;
                }
            }
        }catch(Exception e){
            System.out.println("Exception comparing db and log: " + e.toString());
        }
        return verdict;
    }
    /** Method gets specific values from a text file
     * @param file to read, absolute path
     * @param field, position of the field in file to read; starting with 1 from left
     * @return List of values with leading zeros trimmed
     */
    public ArrayList<String> getNumberOfRecordsFromLogFile(String file, int field, String separator){
        String fileContents = fileUtil.getFileContents(file);
        assert !fileContents.isEmpty();
        ArrayList<String> records = new ArrayList<String>();
        try {
            String[] lines = fileContents.split("\n");
            for (String line : lines) {
                String strNumberOfRecords = line.split(separator)[field];
                String numberOfRecords = trimLeadingZeros(strNumberOfRecords);
                records.add(numberOfRecords);
            }
        }catch(Exception e){
            System.out.println("Exception getting columns from log: " + e.toString());
        }
        return records;
    }

    public boolean compareTwoDbTables(List leftTable, String columns_1, List rightTable, String columns_2){
        boolean verdict = true;

        List<String> leftColumns = Arrays.asList(columns_1.split("\\s*,\\s*"));
        List<String> rightColumns = Arrays.asList(columns_2.split("\\s*,\\s*"));

        assert leftColumns.size() > 0;
        assert rightColumns.size() > 0;
        assert leftColumns.size() == rightColumns.size();
        assert leftTable.size() == rightTable.size();

        int columnCount = leftColumns.size() - 1;
        int rowCount = leftTable.size() - 1;

        for(int i = 0; i <= rowCount; i++) {
            for (int j = 0; j <= columnCount; j++) {
                Map leftRow = (Map)leftTable.get(i);
                Map rightRow = (Map)rightTable.get(i);
                String firstEntry = (String)leftRow.get(leftColumns.get(j));
                String secondEntry = (String)rightRow.get(rightColumns.get(j));
                if (firstEntry != secondEntry)
                    verdict = false;
            }
        }
        return verdict;
    }
    /**
     * Counts the number of records in db table
     * @param table - List of maps containing records from db
     * @return integer value of records
     */

    public int getDbRowCounts(List table){
        return table.size();
    }

    /**
     * Compares the counts of records in two db tables
     * @param table_1 list of maps containing records from first db table
     * @param table_2 list of maps containing records from second db table
     * @return true if counts of records match, aflse otherwise
     */
    public boolean compareDbRowCounts(List table_1, List table_2){
        if (table_1.size() == table_2.size()) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if a job has completed successfully by search specific keyword or phrase
     * in a log file. A timestamp is taken when a job runs and the record in the file
     * will be validated against this timestamp to ensure the record is not for an old job
     * @param log file to be analysed, absolute pth
     * @param keyword or phrase to be searched for
     * @return true if found and later that than jon timestamp or false otherwise
     */
    public boolean verifyJobSuccessInLogFile(String log, String keyword){
        String fileContents = fileUtil.getFileContents(log);
        boolean  found = false;
        String[] lines = fileContents.split("\n");
        for (String line: lines){
            int position = line.length() - 20;
            if(line.contains(keyword)) {
                String strTimestamp = line.substring(position);
                long longTimestamp = convertDateToLong(strTimestamp);
                if(batchUtil.getJobStart() <= longTimestamp)
                    found = true;
            }
        }
        return found;
    }
    /**
     * static method to convert date/time string to long
     * @param dateString
     * @return date/time in long format
     */
    private long convertDateToLong(String dateString){
        long milliseconds = 0;
        SimpleDateFormat f = new SimpleDateFormat("MMM dd HH:mm:ss YYYY");
        try {
            Date d = f.parse(dateString);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
    /**
     * trims leading zeros
     * @param source with leading zeros
     * @return number without leading zeros
     */
    private String trimLeadingZeros(String source){
        int length = source.length();
        if (length < 2)
            return source;
        int i;
        for (i = 0; i < length-1; i++)
        {
            char c = source.charAt(i);
            if (c != '0')
                break;
        }
        if (i == 0)
            return source;
        return source.substring(i);
    }
}
