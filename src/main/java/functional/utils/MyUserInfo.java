package functional.utils;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo, UIKeyboardInteractive {

    Configuration conf = new Configuration();
    private String password = conf.getPassword();

    public String getPassword() {
        return password;
    }

    public boolean promptYesNo(String str) {
        return false;
    }


    public String getPassphrase() {
        return null;
    }

    public boolean promptPassphrase(String message) {
        return true;
    }

    public boolean promptPassword(String message) {
        return false;
    }

    public void showMessage(String message) {
    }
    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
                                              boolean[] echo) {
        String[] response = new String[1];
        response[0] = password;
        return response;
    }
}
