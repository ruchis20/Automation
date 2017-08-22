package utils

class Configuration {
    static Properties props
    static String username
    static String password

    static {
        username = System.properties['unix_username']
        password = System.properties['unix_password']
        props = new Properties()
        def filePath = "config.properties"
        println "FT -> Applying properties file from: ${filePath}."
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

    static String getDestinationFolder(){
        getPropertyValue("destination_folder")
    }

    static String getPropertyValue(String key) {
        System.getProperty(key)?:props.get(key)
    }
}
