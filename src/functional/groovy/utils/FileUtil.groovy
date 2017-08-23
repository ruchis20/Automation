package utils

import static utils.Configuration.*

/**
 * Utility for managing remote files on unix server
 */
class FileUtil {
    def username = getUsername()
    def password = getPassword()
    def server = getPropertyValue("unix_server")
    def destination = getPropertyValue("destination_folder")

    FileUtil(){}

    /**
     * Method gets file from remove unix sevrver and stores in a local folder specified in the config file
     * Unix credentials are supplied on the command line
     * AntBuilder prints  outputs on the screen, this can be disabled if required
     * @param file name to be retrieved
     * @return
     */
    def getFile(String file){
        def ant = new AntBuilder()
        ant.project.buildListeners[0].messageOutputLevel = 0
        new File(file).delete()
        ant.scp(
                trust:"true",
                file:"${username}@${server}:${file}",
                todir:"${destination}",
                password:"${password}",
                verbose:false
        )
    }

    boolean checkFileExists(String file){
        def ant = new AntBuilder()
        ant.project.buildListeners[0].messageOutputLevel = 0
        ant.sshexec(host: server,
                trust: true,
                username: username,
                password: password,
                command: 'ls -la ' + file,
                outputproperty: 'result'
        )
        def files = ant.project.properties.'result'.toString()
        return files.contains(file)
    }
}
