package utils

import static Configuration.*

/**
 * Utility to execute batch jobs on remote matches
 */
class BatchUtil {
    def username = getUsername()
    def password = getPassword()
    def server = getPropertyValue("unix_server")
     BatchUtil(){}

    /**
     * Executes batch job on remote match using Ant. Can be the result is not utilised at the moment but may be
     * modified to to validate for example if you completes successfully
     * @param script to be execute, fully qualified path
     */
    def runBatch(String script){
        def ant = new AntBuilder()
        println "\n============ Running job: "+ script + "=============="
        ant.sshexec(host: server,
                trust: true,
                username: username,
                password: password,
                command: 'ls',
                outputproperty: 'result'
        )
        return ant.project.properties.'result'
    }
}
