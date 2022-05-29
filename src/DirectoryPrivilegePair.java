public class DirectoryPrivilegePair {
    private VDirectory directory;
    private Privileges privilege;

    public DirectoryPrivilegePair(VDirectory directory, Privileges privilege) {
        this.directory = directory;
        this.privilege = privilege;
    }

    public VDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(VDirectory directory) {
        this.directory = directory;
    }

    public Privileges getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privileges privilege) {
        this.privilege = privilege;
    }
}
