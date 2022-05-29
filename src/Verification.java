import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Verification {
    private static final HashMap<String ,DirectoryPrivilegePair> userPrivileges = new HashMap<>();
    private static IUserCredentials userCredentials;
    private static Verification instance=null;
    private Verification() {}

    public static Verification getInstance() {
        if(instance==null){
            instance=new Verification();
            return instance;
        }
        else{
            return instance;
        }
    }
    public boolean checkUser(String username, String password) {
        return userCredentials.checkUser(username, password);
    }
    public boolean login(String username, String password) {
        if(checkUser(username, password)) {
            userCredentials.setCurrentUser(username);
            return true;
        }
        else{
            return false;
        }
    }
    public void setUserCredentials(IUserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }

    public boolean setPrivilege(String username,VDirectory directory, Privileges privilege) {
          if(!userCredentials.TellUser().equals("admin")) {
              System.out.println("Only admin can set privileges!");
              return false;
          }
          else {
              userPrivileges.put(username, new DirectoryPrivilegePair(directory, privilege));
              return true;
          }

    }
    public boolean checkPrivilege(String username, VDirectory directory) {
        if(userPrivileges.containsKey(username)) {
            if(userPrivileges.get(username).getDirectory().equals(directory)&&userPrivileges.get(username).getPrivilege().equals(Privileges.CREATE_DELETE)) {
                return true;
            }
            else if(userPrivileges.get(username).getDirectory().equals(directory)&&userPrivileges.get(username).getPrivilege().equals(Privileges.CREATE_ONLY)){
                return true;
            }
            else if(userPrivileges.get(username).getDirectory().equals(directory)&&userPrivileges.get(username).getPrivilege().equals(Privileges.CREATE_ONLY)){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

}
