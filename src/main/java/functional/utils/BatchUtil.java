package functional.utils;

import com.jcraft.jsch.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public long getJobStart(){
        return new Date().getTime();
    }

    public String runJob(String command) {
        String stdout = "";
        try {
            Session session = connectSession();
            ChannelExec ce = connectChannel(session);
            ce.setCommand(command);
            ce.setErrStream(System.err);
            ce.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stdout = stdout + "\n"+ line;
            }
            ce.disconnect();
            disconnectSession(session);
        }catch(Exception e){
            e.printStackTrace();
        }
        return stdout;
    }

    private ChannelExec connectChannel(Session s) {
        ChannelExec ce = new ChannelExec();
        try {
            Channel c = s.openChannel("exec");
            ce = (ChannelExec) c;
        }catch(Exception e){
            e.printStackTrace();
        }
        return ce;
    }

    private void disconnectSession(Session session) {

        try {
            session.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Session connectSession(){
        JSch js = new JSch();
        Properties config = new Properties();
        try {
            Session s = js.getSession(this.username, this.server, this.port);
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password");
            s.setConfig(config);
            s.setPassword(this.password);
            s.connect();
            return s;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getFileContents(String file){

        String command = "cat " + file;
        return runJob(command);
    }

    public boolean checkFileExists(String file, String path){
        String command = "ls -la " + path;
        String stdout = runJob(command);
        if (stdout.contains(file)) return  true;
        else return false;
    }

    public boolean checkFileMoved(String file, String source, String destination){
        String command = "ls -la " + source;
        String stdout1 = runJob(command);
        command = "ls -la " + destination;
        String stdout2 = runJob(command);

        if (!stdout1.contains(file) && stdout2.contains(file)) return  true;
        else return false;
    }

    public String[] readLocalFile(String file){
        String contents = "";
        try {
            contents = new String(Files.readAllBytes(Paths.get(file)));
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return contents.split("\n");
    }

    public boolean writeToRemoteFile(String file, String destination, String contents){
        String fullPath = destination + "/" + file;
        String write = "echo " + contents + " > " + fullPath;
        runJob(write);
        return checkFileExists(file,destination);
    }
}
