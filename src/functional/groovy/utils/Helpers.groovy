package utils

import static org.junit.Assert.assertTrue
class Helpers {
    /**
    This helper method compares the contents of log file and table from database
     * @param log file - full path
     * @param Resultset from database (list of Maps)
     * @return true if totals match and each count also math
     */
    def compareDbTableAndLog(def dbData, String logFile){
        List logData = getNumberOfRecordsFromLogFile(logFile)
        def verdict = true
        int logCount = logData.size()
        int dbCount = dbData.size()
        if(logCount != dbCount)
            verdict = false
        if(logCount > 0 && dbCount > 0) {
            for (int i = 0; i < logCount - 1; i++) {
                Map dbEntry = dbData.get(i)
                def value = dbEntry.get("count")
                if (value != logData[i])
                    verdict = false
                println value + ":" + logData[i]
            }
        }
        return verdict
    }

    /**
     This helper method compares two tables from database
     * @param Map containing first and second tables as well as columns to compare
     * @return true if totals match and each count also math
     */

    def compareTwoDbTables(Map data){
        def verdict = true
        def firstTable = data.get("firstTable")
        def secondTable = data.get("secondTable")
        def firstColumn = data.get("firstColumn")
        def secondColumn = data.get("secondColumn")

        int firstCount = firstTable.size()
        int secondCount = secondTable.size()
        if(firstCount != secondCount)
            verdict = false
        if(firstCount > 0 && secondCount > 0) {
            for (int i = 0; i < firstCount - 1; i++) {
                def firstEntry = firstTable.get(i).get(firstColumn)
                def secondEntry = secondTable.get(i).get(secondColumn)
                if (firstEntry != secondEntry)
                    verdict = false
             }
        }
        return verdict
    }

    def compareTwoDbTables(def tables, def leftColumns, def rightColumns){
        def verdict = true
        def leftTable
        def rightTable
        assert tables.size() == 2
        assert leftColumns.size() > 0
        assert rightColumns.size() > 0
        assert leftColumns.size() == rightColumns.size()

        int columns = leftColumns.size() - 1
        leftTable = tables[0]
        rightTable = tables[1]
        int rows = leftTable.size() - 1

        for(int i = 0; i <= rows; i++) {
            for (int j = 0; j <= columns; j++) {
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

    def compareDbTableBeforeAndAfter(List beforeTable, List afterTable){
        def verdict = true

        def firstRow = [:]
        if (afterTable.size() > 0){
            firstRow = afterTable.first()
        }
        int columnCount = columns.size() - 1
        beforeTable = tables[0]
        afterTable = tables[1]
        int rows = leftTable.size() - 1

        for(int i = 0; i <= rows; i++) {
            for (int j = 0; j <= columns; j++) {
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

    def getDbRowCounts(Map table_1){
        return table_1.size()
    }

    boolean compareDbRowCounts(List table_1, List table_2){
        if (table_1.size() == table_2.size()) {
            return true
        }else{
            return false
        }
    }

    /** Method gets second column from a log file
     * @param file
     * @return List of counts with leading zeros trimmed
     */
    static getNumberOfRecordsFromLogFile(String file){
        def fileHandler = new File(file)
        String fileContents = fileHandler.text

        def records = []
        String[] lines = fileContents.split('\n')
        lines.each { line ->
            String strNumberOfRecords = line.split(" ").last()
            String numberOfRecords = trimLeadingZeros(strNumberOfRecords)
            records.add(numberOfRecords.toLong())
        }
        return records
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