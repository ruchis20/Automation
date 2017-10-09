package functional.utils;

import com.jcabi.ssh.*;
import com.jcraft.jsch.*;
import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class BatchUtil {
    Configuration conf = new Configuration();
    private String username = conf.getUsername();
    private String password = conf.getPassword();
    private Map<String,String> properties = conf.getProperties();
    private String server = properties.get("unix_server");
    private int port = Integer.parseInt(properties.get("ssh_port"));
     /**
     * Executes batch job on remote match using Ant. Can be the result is not utilised at the moment but may be
     * modified to to validate for example if you completes successfully
     * @param command to be execute, fully qualified path
     */
    public String runBatch(String command){
        String stdout = "";
        try {

            Shell shell = new SshByPassword(server, port, username, password);
            stdout = new Shell.Plain(shell).exec(command);
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return stdout;
    }
    public long getJobStart(){
        return new Date().getTime();
    }

    public String runJob(String command) {
        JSch js = new JSch();
        Properties config = new Properties();
        String stdout = "";
        try {
            Session s = js.getSession(this.username, this.server, this.port);
            s.setPassword(this.password);
            config.put("StrictHostKeyChecking", "no");
            s.setConfig(config);
            s.connect();

            Channel c = s.openChannel("exec");
            ChannelExec ce = (ChannelExec) c;

            ce.setCommand(command);
            ce.setErrStream(System.err);

            ce.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stdout = stdout + "\n"+ line;
            }

            ce.disconnect();
            s.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }
        return stdout;
    }
}
