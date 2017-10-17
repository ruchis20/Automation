package functional.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Unix {
    Configuration conf = new Configuration();
    private String username = conf.getUsername();
    private String password = conf.getPassword();
    private Map<String, String> properties = conf.getProperties();
    private String server = properties.get("unix_server");
    private int port = Integer.parseInt(properties.get("ssh_port"));

    public String runJob(String command) {
        String output = "";
        try
        {
            Connection conn = new Connection(server, port);
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);

            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");

            Session sess = conn.openSession();

            sess.execCommand(command);

            InputStream stdout = new StreamGobbler(sess.getStdout());

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            while (true)
            {
                String line = br.readLine();
                if (line == null)
                    break;
                output += line + "\n";
            }

            br.close();
            sess.close();
            conn.close();

        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(2);
        }
        return output;
    }
}