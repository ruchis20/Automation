package functional.utils;

import com.jcraft.jsch.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BatchUtil {
    Configuration conf = new Configuration();
    private String username = conf.getUsername();
    private Map<String, String> properties = conf.getProperties();
    private String server = properties.get("unix_server");
    private int port = Integer.parseInt(properties.get("ssh_port"));

    private long jobStart;

    public long getJobStart() {
        return this.jobStart;
    }

    public String runCommand(String command) {
        String response = "";
        final DateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        this.jobStart = new Date().getTime();
        System.out.println("Starting job: "+ command);
        System.out.println("Timestamp: "+ dateFormat.format(this.jobStart));
        try{

            JSch jsch = new JSch();

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            Session session=jsch.getSession(username, server, port);

            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);
            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("shell");
            OutputStream ops = channel.getOutputStream();
            PrintStream ps = new PrintStream(ops);
            channel.connect();
            InputStream input = channel.getInputStream();

            ps.println("sesudo mcom");
            ps.println(command);
            ps.println("exit");
            ps.flush();
            ps.close();

            response = printResult(input, channel);

            channel.disconnect();
            session.disconnect();

        }catch(Exception e){
            e.printStackTrace();

        }
        return response;
    }

    private String printResult(InputStream input,
                                    Channel channel) throws Exception
    {
        int SIZE = 1024;
        byte[] tmp = new byte[SIZE];
        String out = "";
        while (true)
        {
            while (input.available() > 0)
            {
                int i = input.read(tmp, 0, SIZE);
                if(i < 0)
                    break;
                out += new String(tmp, 0, i);
                System.out.print(new String(tmp, 0, i));
            }
            if(channel.isClosed())
            {
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try{Thread.sleep(300);}
            catch (Exception ee){ee.printStackTrace();}
        }
        return out;
    }

    public String getFileContents(String file) {

        String command = "cat " + file;
        return runCommand(command);
    }

    public boolean checkFileExists(String file, String path) {
        String command = "ls -la " + path;
        String stdout = runCommand(command);
        if (stdout.contains(file)) return true;
        else return false;
    }

    public boolean checkFileMoved(String file, String source, String destination) {
        String command = "ls -la " + source;
        String stdout1 = runCommand(command);
        command = "ls -la " + destination;
        String stdout2 = runCommand(command);

        if (!stdout1.contains(file) && stdout2.contains(file)) return true;
        else return false;
    }

    public String[] readLocalFile(String file) {
        String contents = "";
        try {
            contents = new String(Files.readAllBytes(Paths.get(file)));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return contents.split("\n");
    }

    public boolean writeToRemoteFile(String file, String destination, String contents) {
        String fullPath = destination + "/" + file;
        String write = "echo " + contents + " > " + fullPath;
        runCommand(write);
        return checkFileExists(file, destination);
    }
}
