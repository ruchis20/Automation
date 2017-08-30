package utils

class Configuration {
    static Properties props
    static String username
    static String password
    static String db

    static {
        username = System.properties['unix_username']
        password = System.properties['unix_password']
        db = System.properties['db']
        props = new Properties()
        def filePath = "config.properties"
        InputStream input = this.getClassLoader().getResourceAsStream(filePath)
        if(input) {
            props.load(input as InputStream)
        } else {
            println "Properties file: ${filePath} cannot be loaded!!! Tests might not work!!"
        }
    }

    static String getUsername() {
        username
    }

    static String getPassword() {
        password
    }

    static String getDb() {
        db
    }

    static String getPropertyValue(String key) {
        System.getProperty(key)?:props.get(key)
    }
}
