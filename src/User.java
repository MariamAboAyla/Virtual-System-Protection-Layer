import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User implements IUserCredentials{

    private static final HashMap<String, String> systemUsers = new HashMap<> (  );  // stores the system's users

    private final String adminUsername, adminPassword;  // holds the username and password of the admin
    private String currentUser;  // holds the username of currently logged-in user


    /* first line contains admin's username and password, and rest of lines contains the other users */
    public final String UserFile = "user.txt";


    /* Class constructor: admin logged-in by default, and set its data */
    public User() throws IOException {
        this.adminPassword = "admin";
        this.adminUsername = "admin";
        this.currentUser = adminUsername;
        addToFile(this.adminUsername, this.adminPassword);

    }

    /*  Function that adds new user to the system */
    public boolean addUser (String username, String password) throws IOException {
        if( ! Objects.equals ( currentUser , adminUsername ) )
        {
            System.out.println ("Only admin can create new user!!" );
            return false;
        }

        if(systemUsers.containsKey ( username ))
        {
            System.out.println ("Can't be added! Already have user with the same name!!" );
            return false;
        }

        systemUsers.put ( username, password );
        addToFile(username, password);

        return true;

    }

    /* function that adds the user's data to the file */
    public void addToFile(String username, String password) throws IOException {

        BufferedWriter writer = new BufferedWriter (new FileWriter(UserFile, true));

        String data  = username + " , "+password + "\n";

        writer.write ( data );

        writer.close ();

    }


    /* function that sets the currently logged-in user */
    @Override
    public void setCurrentUser (String username)
    {
        currentUser = username;
    }

    /* function that tells "returns" the currently logged-in user */
    @Override
    public String TellUser ()
    {
        return currentUser;
    }

    /* function that checks if it is an admin, as only admin can create users */
    @Override
    public boolean checkUser ( String username , String password ) {

        for (Map.Entry<String, String> entry: systemUsers.entrySet ())
        {
            if( Objects.equals ( entry.getKey ( ) , username )  && Objects.equals ( entry.getValue ( ) , password ) )
            {
                return true;
            }
        }

        return false;
    }

}
