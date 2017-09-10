package functional.utils;

import com.jcabi.ssh.Shell;
import com.jcabi.ssh.SshByPassword;

import java.util.Map;

public class FileUtil {
    private Configuration conf = new Configuration();
    private String username = conf.getUsername();
    private String password = conf.getPassword();
    Map<String,String> properties = conf.getProperties();
    private String server = properties.get("unix_server");
    private int port = Integer.parseInt(properties.get("ssh_port"));

    public String getFileContents(String file){
        String stdout = "";
        String command = "cat " + file;
        try {
            Shell shell = new SshByPassword(server, port, username, password);
            stdout = new Shell.Plain(shell).exec(command);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return stdout;
    }

    public boolean checkFileExists(String file, String path){
        String stdout = "";
        String command = "ls -la" + path;
        try {
            Shell shell = new SshByPassword(server, port, username, password);
            stdout = new Shell.Plain(shell).exec(command);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        if (stdout.contains(file)) return  true;
        else return false;
    }

    public boolean checkFileMoved(String file, String source, String destination){
        String stdout1 = "";
        String stdout2 = "";
        String command1 = "ls -la" + source;
        String command2 = "ls -la" + file;
        try {
            Shell shell = new SshByPassword(server, port, username, password);
            stdout1 = new Shell.Plain(shell).exec(command1);
            stdout2 = new Shell.Plain(shell).exec(command2);
        }catch (Exception e){
            System.out.println(e.toString());
        }
        if (!stdout1.contains(file) && stdout2.contains(file)) return  true;
        else return false;
    }
}
