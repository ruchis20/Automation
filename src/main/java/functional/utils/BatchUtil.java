package functional.utils;

import com.jcabi.ssh.*;
import java.util.Date;
import java.util.Map;

public class BatchUtil {
    Configuration conf = new Configuration();
     /**
     * Executes batch job on remote match using Ant. Can be the result is not utilised at the moment but may be
     * modified to to validate for example if you completes successfully
     * @param command to be execute, fully qualified path
     */
    public String runBatch(String command){
        String stdout = "";
        try {
            String username = conf.getUsername();
            String password = conf.getPassword();
            Map<String,String> properties = conf.getProperties();
            String server = properties.get("unix_server");
            int port = Integer.parseInt(properties.get("ssh_port"));
            Shell shell = new SshByPassword(server, port, username, password);
            stdout = new Shell.Plain(shell).exec(command);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return stdout;
    }
    public long getJobStart(){
        return new Date().getTime();
    }
}
