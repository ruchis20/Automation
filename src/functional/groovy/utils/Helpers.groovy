package utils


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
                println firstEntry + ":" + secondEntry
            }
        }
        return verdict
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