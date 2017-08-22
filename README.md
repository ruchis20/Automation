# Documentation
## Installation

1. Download groovy from here http://groovy-lang.org/download.html and follow
  the instructions documented here to install it: http://groovy-lang.org/install.html
  
2. Download and install gradle as documented here: https://gradle.org/install/
  
3. Make sure you have Java 1.8
  
4. Make sure you have properly setup environment variables for groovy, gradle and java
  as documented on the respective installation pages
  
5. Unzip the project in a designated folder
  
## Running tests
To execute test, from the main project folder, type:

    gradle runTest -Dusername=your_unix_username -Dpassword=your_unix_password

The credentials you provide are for the test to login and download log files
from the unix machine where batch jobs are executed.

## Configuration
The framework comes with a configuration file located at:

    src/functionality/resources/config.properties

This file is self explanatory and it is here that you defined oracle access
credentials, unix server and the destination folder, where the test will download
log files for processing. This folder must exist and write access.

## About the framework
The framework is based on Cucumber JVM. 

### Feature files
You will need to write your feature files
and place them in the folder src/functional/resources/features. Subfolders are permitted but
if you place any file outside this folder, you will need to update build.gradle otherwise
the feature file will not be detected.
You will need to put the right parameters like log file names or some values in the feature
files.

### Step definitions
This si where you write the logic of your test. Each statement you have in a feature file
must have one and only one step definition. Step definitions are grouped into
logical files, but there locations are irrelevant as Cucumber will scan all feature files
and match them to their corresponding step definitions, a process called gluing.

### Configuration & sharedworld
The configuration class reads config files and extracts the username and password, making
it available for the rest of the application.

The sharedworld is groovy cucumber specific and helps share variabes and other objects across
the entire project. The most important thing to note in the shared world is the definition
of both MySql and Oracle data sources.
You will need to comment out the line:

    def dbUtil = new DbUtil(mysqlDataSource())

### DbUtil
This is utility class for interacting with database.
First thing you need to delete the lines:

    DbUtil(){}

    DbUtil(MysqlDataSource dataSource) {
        this.sql = new Sql(dataSource: dataSource)
    }

And uncomment the lines in order to work with Oracle:

    //    DbUtil() {
    //        this.sql = Sql.newInstance( dbUrl, dbUser, dbPassword, dbDriver )
    //    }

This class has the methods to select, insert and delete. It also
has method for reading queries from files.
You should place all your queries in the folder:

    src/functional/resources/sql
    
And call them in your step definitions.

### FileUtil & BatchUtil
These classes are self explanatory. They provide methods for reading
log files and executing batch jobs on unix machine. The names
of files and jobs will come from feature files.

### Helpers
This class contains few methods that help with comparing logs and database 
values. There are other methods as well and these are all well documented.


### Templating queries
To make queries re-usable, we use groovy templates. For example:

    SELECT * FROM BILLING WHERE BILLING_AMOUNT >=${amount} AND BILLING_CODE=${code} AND BILLING_DTTM=${date};
    
Here, the variables amount, code and date will be substituted with 
values defined in feature file.
For this to happen, in step definition, you need to define a binding like:

    def binding = ["amount":20, "code":3501, "date":"2017-08-21"]
    def query = dbUtil.getQuery("billing_01.sql", binding)
    List data = dbUtil.executeSelect(query)

When the getQuery method is called, the query is read from the file billing_01.sql
and the variables substituted.    