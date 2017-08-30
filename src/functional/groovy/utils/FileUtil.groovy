package utils

import static utils.Configuration.*

/**
 * Utility for managing remote files on unix server
 */
class FileUtil {
    def username = getUsername()
    def password = getPassword()
    def server = getPropertyValue("unix_server")
    def port = getPropertyValue("ssh_port")
    def destination = getPropertyValue("destination_folder")

    FileUtil(){}

    /**
     * Method gets file from remote unix sevrver and stores in a local folder specified in the config file
     * Unix credentials are supplied on the command line
     * AntBuilder prints  outputs on the screen, this can be disabled if required
     * @param file name to be retrieved
     * @return true or false
     */
    def getFile(String file){
        def ant = new AntBuilder()
        ant.project.buildListeners[0].messageOutputLevel = 0
        new File(file).delete()
        ant.scp(
                trust:"true",
                port:port,
                file:"${username}@${server}:${file}",
                todir:"${destination}",
                password:"${password}",
                verbose:false
        )
    }

    /**
     * Reads the contents of a remote file
     * @param file
     * @return file content as a string
     */
    def getFileContent(String file){
        def ant = new AntBuilder()
        ant.project.buildListeners[0].messageOutputLevel = 0
        new File(file).delete()
        ant.sshexec(
                host: server,
                port: port,
                trust: true,
                username: username,
                password: password,
                command: 'cat ' + file,
                outputproperty: 'result',
                verbose: false
        )
        return ant.project.properties.'result'.toString()
    }

    /**
     * Cheks if file exists on remote server
     * @param file name to check, absolute path
     * @return true if found, false if not
     */
    boolean checkFileExists(String file){
        def ant = new AntBuilder()
        ant.project.buildListeners[0].messageOutputLevel = 0
        ant.sshexec(
                host: server,
                port: port,
                trust: true,
                username: username,
                password: password,
                command: 'ls -la ' + file,
                outputproperty: 'result',
                verbose: false
        )
        def files = ant.project.properties.'result'.toString()
        return files.contains(file)
    }

    /**
     * Checks if a file has been moved after batch run
     * @param file to be checked
     * @param source - path of original original folder
     * @param destination - path of final folder
     * @return true if file is not present in source folder and present in destination folder; false in any other case
     */
    boolean checkFileMoved(String file, String source, String destination){
        def ant = new AntBuilder()
        ant.project.buildListeners[0].messageOutputLevel = 0
        ant.sshexec(
                host: server,
                port: port,
                trust: true,
                username: username,
                password: password,
                command: 'ls -la ' + source,
                outputproperty: 'result',
                verbose: false
        )
        def sourceFiles = ant.project.properties.'result'.toString()
        def ant2 = new AntBuilder()
        ant2.project.buildListeners[0].messageOutputLevel = 0
        ant2.sshexec(
                host: server,
                port: port,
                trust: true,
                username: username,
                password: password,
                command: 'ls -la ' + destination,
                outputproperty: 'result',
                verbose: false
        )
        def destinationFiles = ant2.project.properties.'result'.toString()
        if (!sourceFiles.contains(file) && destinationFiles.contains(file)){
            return true
        }else {
            return false
        }
    }
}
