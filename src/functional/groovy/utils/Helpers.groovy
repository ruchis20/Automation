package utils

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Utility class with helper methods for various operations to be done outside step definitions
 */
class Helpers {
    def fileUtil = new FileUtil()
    def batchUtil = new BatchUtil()

    /**
     * Compares the counts of records in db table with records in text file
     * @param table - List of maps containing table records
     * @param logFile - absolute path of remote text file to be compared against
     * @return true if match, false otherwise
     */
    def compareCountsOfDBAndLog(List table, String logFile){
        def verdict = true
         if(table.size() != getCountFromLogFile(logFile)) {
            verdict = false
        }
        return verdict
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
    def compareDbAndValuesFromLog(List dbData, String column, String logFile,int field, String separator){
        List logData = getNumberOfRecordsFromLogFile(logFile, field, separator)
        def verdict = true
        int logCount = logData.size()
        int dbCount = dbData.size()
        if(logCount != dbCount)
            verdict = false
        if(logCount > 0 && dbCount > 0) {
            for (int i = 0; i < logCount - 1; i++) {
                Map dbEntry = dbData.get(i)
                def value = dbEntry.get(column)
                if (value != logData[i])
                    verdict = false
            }
        }
        return verdict
    }

    /**
     This helper method compares two tables from database
     * @param tables - List of List of maps containing first and second tables as well as columns to compare
     * @param columns - List of List of strings containing columns from two tables to be compared
     * @return if both counts and all values match, false otherwise
     */

    def compareTwoDbTables(List tables, List columns){
        def verdict = true
        assert tables.size() == 2
        List leftTable = (List)tables[0]
        List rightTable = (List)tables[1]

        List leftColumns = (List)columns[0]
        List rightColumns = (List)columns[1]
        assert leftColumns.size() > 0
        assert rightColumns.size() > 0
        assert leftColumns.size() == rightColumns.size()

        int columnCount = leftColumns.size() - 1
        int rowCount = leftTable.size() - 1

        for(int i = 0; i <= rowCount; i++) {
            for (int j = 0; j <= columnCount; j++) {
                def leftRow = leftTable.get(i)
                def rightRow = rightTable.get(i)
                def firstEntry = leftRow.get(leftColumns.get(j))
                def secondEntry = rightRow.get(rightColumns.get(j))
                if (firstEntry != secondEntry)
                    verdict = false
            }
        }
        return verdict
    }

    /**
     * Counts the number of records in db table
     * @param table - List of maps containing records from db
     * @return integer value of records
     */

    def getDbRowCounts(Map table){
        return table.size()
    }

    /**
     * Compares the counts of records in two db tables
     * @param table_1 list of maps containing records from first db table
     * @param table_2 list of maps containing records from second db table
     * @return true if counts of records match, aflse otherwise
     */
    boolean compareDbRowCounts(List table_1, List table_2){
        if (table_1.size() == table_2.size()) {
            return true
        }else{
            return false
        }
    }

    /** Method gets specific values from a text file
     * @param file to read, absolute path
     * @param field, position of the field in file to read; starting with 1 from left
     * @return List of values with leading zeros trimmed
     */
    def getNumberOfRecordsFromLogFile(String file, int field, String separator){
        String fileContents = fileUtil.getFileContent(file)

        def records = []
        String[] lines = fileContents.split('\n')
        lines.each { line ->
            String strNumberOfRecords = line.split(separator)[field]
            String numberOfRecords = trimLeadingZeros(strNumberOfRecords)
            records.add(numberOfRecords.toLong())
        }
        return records
    }

    /**
     * Counts the number of records/lines in a text file
     * @param file to read from, absolute path
     * @return number of records
     */
    def getCountFromLogFile(String file){
        String fileContents = fileUtil.getFileContent(file)
        return fileContents.split('\n').size()
    }

    /**
     * Checks if a job has completed successfully by search specific keyword or phrase
     * in a log file. A timestamp is taken when a job runs and the record in the file
     * will be validated against this timestamp to ensure the record is not for an old job
     * @param log file to be analysed, absolute pth
     * @param keyword or phrase to be searched for
     * @return true if found and later that than jon timestamp or false otherwise
     */
    def verifyJobSuccessInLogFile(String log, String keyword){
        String fileContents = fileUtil.getFileContent(log)
        boolean  found = false
        String[] lines = fileContents.split('\n')
        lines.each { line ->
            if(line.contains(keyword)) {
                String strTimestamp = line[-20..-1]
                long longTimestamp = convertDateToLong(strTimestamp)
                if(batchUtil.jobStart <= longTimestamp)
                    found = true
            }
        }
        return found
    }
    /**
     * static method to convert date/time string to long
     * @param dateString
     * @return date/time in long format
     */
    static convertDateToLong(String dateString){
        long milliseconds = 0
        SimpleDateFormat f = new SimpleDateFormat("MMM dd HH:mm:ss YYYY")
        try {
            Date d = f.parse(dateString)
            milliseconds = d.getTime()
        } catch (ParseException e) {
            e.printStackTrace()
        }
        return milliseconds
    }
    /**
     * trims leading zeros
     * @param source with leading zeros
     * @return number without leading zeros
     */
    static String trimLeadingZeros(String source)
    {
        int length = source.length()
        if (length < 2)
            return source
        int i
        for (i = 0; i < length-1; i++)
        {
            char c = source.charAt(i)
            if (c != '0')
                break
        }
        if (i == 0)
            return source
        return source.substring(i)
    }
}