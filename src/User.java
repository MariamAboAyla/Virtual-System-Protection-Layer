import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User implements IUserCredentials{

    private static final HashMap<String, String> systemUsers = new HashMap<> (  );  // stores the system's users

    private static final HashMap<String,String> UsersGrant = new HashMap<> ();

    VDirectory dir1=new VDirectory();

    private final String adminUsername, adminPassword;  // holds the username and password of the admin
    private String currentUser;  // holds the username of currently logged-in user


    /* first line contains admin's username and password, and rest of lines contains the other users */
    public final String UserFile = "user.txt";
    public final String UserGrant = "capabilities.txt";
    public final String temp = "TempCapabilities.txt";


    /* Class constructor: admin logged-in by default, and set its data */
    public User() throws IOException {
        this.adminPassword = "admin";
        this.adminUsername = "admin";
        this.currentUser = adminUsername;
        addToFile(this.adminUsername, this.adminPassword);
        addUser(adminUsername, adminPassword);
        UsersGrant.put(adminUsername, adminPassword);

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
        System.out.println ("User Added Successfully :) ");
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

    public void grantUser (String name , String Path , String what , VDirectory dir1) throws IOException {
        if(Objects.equals ( currentUser , adminUsername ) )
        {
            if(systemUsers.containsKey(name))
            {
                if(dir1.checkDirectoryPath(Path)!=null)
                {
                    String concat =Path+" "+what;
                    UsersGrant.put(name,concat);
                    addToCapabilities(name,Path,what);
                }
                else
                {
                    System.out.println("There is no folder with this path ! ");
                }
            }
            else
            {
                System.out.println("There is no user with this name !");
            }
        }
        else
        {
            System.out.println ("Only admin can give Grant to users !!" );
        }
        return;
    }

    public boolean createCheaker (String name)
    {

        String status = UsersGrant.get(name);
        if (status==null) return false;
        String arr[] = status.split(" ");
        if(arr[1].equalsIgnoreCase("01"))return false;
        if(arr[1].equalsIgnoreCase("00"))return false;

        return true;
    }
    public boolean deleteCheaker (String name)
    {
        String status = UsersGrant.get(name);
        if (status==null) return false;
        String arr[] = status.split(" ");
        if(arr[1].equalsIgnoreCase("00"))return false;
        if(arr[1].equalsIgnoreCase("10"))return false;
        return true;
    }
    public void addToCapabilities(String name , String Path , String what) throws IOException {
        new FileWriter(temp, false).close();

        String line, line2 = "";
        BufferedWriter writer = new BufferedWriter (new FileWriter(temp, true));
        try {
            BufferedReader in = new BufferedReader(new FileReader(UserGrant));
            while ((line = in.readLine()) != null)
            {
                String[] arr = line.split(",");
                line2 =arr[0];
                boolean ok = false;
                for (int i = 1; i < arr.length; i += 2) {
                    if (arr[i].equalsIgnoreCase(name) && arr[0].equalsIgnoreCase(Path)) {
                        line2 = line2 + "," + arr[i] + "," + what;
                        ok = true;
                    }
                    else {
                        line2 = line2 + "," + arr[i] + "," + arr[i + 1];
                    }
                }

                if (arr[0].equalsIgnoreCase(Path)) {
                    if (!ok) {
                        line2 = line2 + "," + name + "," + what;
                    }
                }
                line2 = line2 + "\n";
                writer.write(line2);
            }
            writer.close ();
            in.close();
        }
        catch (IOException e) {
            line2 = Path+","+name+","+what;
            line2 = line2 +"\n";
            writer.write(line2);
            writer.close ();
            //System.out.println("EXCEPTION!" + e);
        }
        new FileWriter(UserGrant, false).close();

        writer = new BufferedWriter (new FileWriter(UserGrant, true));
        BufferedReader in = new BufferedReader(new FileReader(temp));
        while((line = in.readLine()) != null) {
            line = line+"\n";
            writer.write(line);
        }
        writer.close ();
        in.close();
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
