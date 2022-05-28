import java.util.ArrayList;

public class VDirectory {
    private String directoryName;
    private ArrayList<VFile> files = new ArrayList<>();
    private ArrayList<VDirectory> subDirectories = new ArrayList<>();
    private boolean deleted;

    public VDirectory() {
    }
    public VDirectory(String name) {
        directoryName = name;
    }
    public void set_DirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
    public String get_DirectoryName() {
        return directoryName;
    }

    public void set_Files(ArrayList<VFile> files) {
        this.files = files;
    }
    public ArrayList<VFile>  get_Files() {
        return files;
    }

    public void set_SubDirectories(ArrayList<VDirectory> subDirectories) {
        this.subDirectories = subDirectories;
    }
    public ArrayList<VDirectory>  get_SubDirectories() {
        return subDirectories;
    }

    public void set_Deleted(boolean deleted) {
        this.deleted = deleted;
    }
    public boolean isDeleted() {
        return deleted;
    }


    public boolean addNewFile(String path , int size ){
        // get the last appear of '/' because the string after it is the file name.
        int target = path.lastIndexOf("/");
        String file = path.substring(target+1,path.length());

        // the remaining must be directories
        String Dpath = path.substring(0,target);

        // check if the path is valid
        VDirectory directory = checkDirectoryPath(Dpath);
        if(directory == null)return false;

        VFile newFile = new VFile(file,size);
//        newFile.set_AllocatedBlocks(allocatedBlocks);
        newFile.set_Deleted(false);

        // return true if the file added , false otherwise
        return directory.addFile(newFile);
    }

    public boolean addNewDirectory(String path){
        // get the last appear of '/' because the string after it is the directory name we want to add.
        int target = path.lastIndexOf("/");
        String directory = path.substring(target+1);

        // the remaining must be directories
        String Dpath = path.substring(0,target);

        // check if the path is valid
        VDirectory tempDirectory = checkDirectoryPath(Dpath);
        if(tempDirectory == null)return false;

        VDirectory newDirectory = new VDirectory();
        newDirectory.set_DirectoryName(directory);
        newDirectory.set_Deleted(false);

        // return true if the Directory added , false otherwise
        return tempDirectory.addSubDirectory(newDirectory);
    }


    public VFile checkFilePath(String path) { // root/folder1/file.txt ??
        String[] pathes = path.split("/"); // ( [root][folder1][file.txt] )
        VFile file = null;
        if (!isDeleted() && pathes[0].equalsIgnoreCase(directoryName)) {
            // search if second is file
            for (VFile tempFile : files) {
                if (tempFile.get_FileName().equalsIgnoreCase(pathes[1]) && !tempFile.isDeleted()) {
                    return tempFile;
                }
            }
            // if reach here then must be sub-directory not folder
            for (VDirectory tempDirectory : subDirectories) {
                // recursion call if the sub-directory not deleted
                // to make the sub-directory be the main directory
                if (!tempDirectory.isDeleted()) {
                    String temp="";
                    for (int i = 1; i < pathes.length-1; i++) {
                        temp += pathes[i] + "/";
                    }
                    temp+=pathes[pathes.length-1];
                    file = tempDirectory.checkFilePath(temp);
                    if(file!=null)break;//here we get a file
                }
            }
        }
        return file;
    }

    public VDirectory checkDirectoryPath(String path) { // root/folder1 ??
        String[] pathes = path.split("/"); // ( [root][folder1] )
        VDirectory directory = null;
        if (!isDeleted() && pathes[0].equalsIgnoreCase(directoryName)) {
            // if the path is the main directory
            if(pathes.length==1){
                return this;
            }
            // if reach here then must be sub-directory again
            for (VDirectory tempDirectory : subDirectories) {
                // recursion call if the sub-directory not deleted
                // to make the sub-directory be the main directory
                if (!tempDirectory.isDeleted()) {
                    String temp="";
                    for (int i = 1; i < pathes.length-1; i++) {
                        temp += pathes[i] + "/";
                    }
                    temp+=pathes[pathes.length-1];
                    directory = tempDirectory.checkDirectoryPath(temp);
                    if(directory!=null)break;// here we get a directory
                }
            }
        }
        return directory;
    }


    public boolean addFile(VFile newFile) {
        for(VFile file : files) {
            if(file.get_FileName().equalsIgnoreCase(newFile.get_FileName())) {
                if(file.isDeleted()) {
                    file.set_Deleted(false);
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        files.add(newFile);
        return true;
    }
    public boolean addSubDirectory(VDirectory newSubDirectory) {
        for(VDirectory subDirectory : subDirectories) {
            if(subDirectory.get_DirectoryName().equalsIgnoreCase(newSubDirectory.get_DirectoryName())){
                if(subDirectory.isDeleted()) {
                    subDirectory.set_Deleted(false);
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        subDirectories.add(newSubDirectory);
        return true;
    }
    public VDirectory getSubDirectory(String DirectoryName) {
        for (VDirectory subDirectory : subDirectories) {
            if (subDirectory.directoryName.equals(DirectoryName))
                return subDirectory;
        }
        System.out.println("No such Directory");
        return null;
    }


    public void printDiskStructure(String tab)
    {
        if(!isDeleted()) {
            System.out.println(tab+directoryName);
            tab+="        ";
            for(VFile file : files){
                if(!file.isDeleted()){
                    System.out.println(tab+file.get_FileName());
                }
            }
            for(VDirectory directory : subDirectories){
                if(!directory.isDeleted()){
                    directory.printDiskStructure(tab);
                }
            }
        }
    }
}
