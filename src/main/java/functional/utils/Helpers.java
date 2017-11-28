package functional.utils;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class Helpers {
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
        String fileContents = batchUtil.getFileContents(file);
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
        String fileContents = batchUtil.getFileContents(file);
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
    public boolean verifyJobSuccessInLogFile(String log, String keyword, long jobStart){
        String fileContents = batchUtil.getFileContents(log);

        boolean  found = false;
        String[] lines = fileContents.split("\n");
        int lineCount = lines.length;
        int startLine = 0;
        if (lineCount > 10)
            startLine = lineCount - 10;

        String strTimestamp = "";
        for (int i = startLine; i < lineCount; i++){
            String line = lines[i];
            if(line.contains(keyword)) {
                 strTimestamp = line.substring(0,15).replaceAll("  ", " 0");
            }
        }

        long longTimestamp = convertDateToLong(strTimestamp);
        if(jobStart <= longTimestamp)
            found = true;

        System.out.println("*****************************************");
        System.out.println("Job started: "+ jobStart);
        System.out.println("Log time   : "+ longTimestamp);
        System.out.println("Comparision result: "+ found);
        System.out.println("*****************************************");
        return found;
    }
    /**
     * static method to convert date/time string to long
     * @param dateString
     * @return date/time in long format
     */
    private long convertDateToLong(String dateString){
        long milliseconds = 0;
        SimpleDateFormat f = new SimpleDateFormat("MMM dd HH:mm:ss");
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

    /**
     * Extracts values of attributes from XML string
     * @param inputXml - string containing the xml
     * @param nodeName - name of the node wcontaining the attrivates
     * @param attributeString - comma separated list of attributes to be extracted
     * @return map of the attributes and their values
     */
    public List readXML(String inputXml, String nodeName, String attributeString){
        List<Object> results = new ArrayList<Object>();
        List<String> attributes = Arrays.asList(attributeString.split("\\s*,\\s*"));
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputSource inputStream = new InputSource(new StringReader(inputXml));
            Document doc = docBuilder.parse(inputStream);

            NodeList nodeList = doc.getElementsByTagName(nodeName);

            for (int count = 0; count < nodeList.getLength(); count++) {

                Node node = nodeList.item(count);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Map<String,String> map = new HashMap<String,String>();
                    Element element = (Element) node;

                    for(String attribute : attributes){
                        String value = element.getElementsByTagName(attribute).item(0).getTextContent();
                        map.put(attribute, value);
                    }
                    results.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Compares DB query result with attribute values extracted from an xml
     * @param dbTable - resultset of the db query we want to compare
     * @param xmlData - list of data columns to be compared
     * @param attributes - Map of xml attributes and their values
     * @return true if all records match (row counts and individual fields), otherwise false
     */

    public boolean compareDbTableWithXmlValues(List dbTable, String columns, List xmlData, String attributes){
        boolean verdict = true;

        List<String> dbColumns = Arrays.asList(columns.split("\\s*,\\s*"));
        List<String> xmlAttributes = Arrays.asList(attributes.split("\\s*,\\s*"));

        if (dbTable.size()  <= 0) {
            System.out.println("ERROR: DB records is empty ********");
            return false;
        }
        if (xmlData.size()  <= 0) {
            System.out.println("ERROR: XML data is empty ********");
            return false;
        }
        if (dbColumns.size()  <= 0) {
            System.out.println("ERROR: Expecting at least one DB column to compare ********");
            return false;
        }

        if (xmlAttributes.size()  <= 0) {
            System.out.println("ERROR: Expecting at least one xml attribute to compare ********");
            return false;
        }
        Assert.assertTrue(dbTable.size() == xmlData.size());

        if (dbTable.size() != xmlData.size()) {
            System.out.println("ERROR: Number of records in DB and XML do not match ********");
            return false;
        }

        int columnCount = dbColumns.size() - 1;
        int rowCount = dbTable.size() - 1;
        for(int i = 0; i <= rowCount; i++) {
            for (int j = 0; j <= columnCount; j++) {
                Map dbRow = (Map)dbTable.get(i);
                Map xmlRow = (Map)xmlData.get(i);
                String firstEntry = (String)dbRow.get(dbColumns.get(j));
                String secondEntry = (String)xmlRow.get(xmlAttributes.get(j));
                if (!firstEntry.equals(secondEntry)) {
                    verdict = false;
                    int row = i + 1;
                    System.out.println("ERROR: Comparison failed. Expecting " + firstEntry + " but found " + secondEntry + " in row " + row + " ********");
                }
            }
        }
        return verdict;
    }

    public String updateTextFileWithDbValues(List dbValues, String[] inputString,
                                             Map<String,Integer> expendibles, String separator){
        String newContent = null;
        int count = dbValues.size();
        for ( int i = 0; i < count - 1; i++){
            String line = inputString[i];
            Map row = (Map)dbValues.get(i);
            String[] fields = line.split(separator);
            for (Map.Entry<String, Integer> entry : expendibles.entrySet())
            {
                String column = entry.getKey();
                int field = entry.getValue() - 1;
                int dbValue = Integer.parseInt(row.get(column).toString());
                fields[field] = String.format("%012d", dbValue);
            }
            String newLine = StringUtils.join(fields, ',') + "\n";
            newContent += newLine;
            System.out.println("*******************************************************");
            System.out.println("Old: "+ line);
            System.out.println("New: "+ newLine);
            System.out.println("*******************************************************");
        }
        return newContent;
    }
}
