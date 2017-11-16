package functional.utils;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo, UIKeyboardInteractive {

    private String passwd = "Qdrt45$6";

    public String getPassword() {
        return passwd;
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
        response[0] = "Qdrt45$6";
        return response;
    }
}
