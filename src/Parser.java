import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    public String commandName="";
    public ArrayList<String> args= new ArrayList<>(); //This method will divide the input into commandName and args
    public static String[] cmds = {"CreateFile","CreateFolder","DeleteFile","DeleteFolder","DisplayDiskStatues","DisplayDiskStructure"};
    //where "input" is the string command entered by the user

    public boolean parse(String input) {
        String[] substrings = input.split(" ");
        commandName = substrings[0];
        if(!Arrays.asList(cmds).contains(commandName)){
            return false;
        }
        for(int i=1;i<substrings.length;i++){
            args.add(substrings[i]);
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public ArrayList<String> getArgs() {
        return args;
    }


}