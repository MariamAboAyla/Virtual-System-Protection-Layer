import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class IndexedAllocation implements AllocationManager{
    private final ArrayList<Integer> blocks = new ArrayList<>(); // the blocks that are allocated to this file
    private final HashMap<Integer, ArrayList<Integer>> IndexBlocks = new HashMap<>(); // <block, [indexes]>
    private final ArrayList<BlockDetails> StoredFiles  = new ArrayList<>(); // this is the list of files that are stored in the disk
    private final HashMap<String , Integer> StoredFilesToIndexFiles = new HashMap<>(); // <fileName, indexFile>

    public final String VFS = "VFIndexed.vfs";

    IndexedAllocation(int size){
        for(int i = 0 ;i < size ; i++) {
            blocks.add(-1); // -1 means empty block
        }
    }
    @Override
    public int allocate(VFile file) {

        int startIndex = blocks.indexOf(-1); // find first free block
        ArrayList<Integer> blocksForFile = new ArrayList<>(); // create array of blocks for file

        if(startIndex != -1) { // if there is free block
            blocks.set(startIndex, 1); // set block to used

            for(int i = 0 ;i < file.get_Size() ; i++) { // allocate blocks for file
                int freeSpace = blocks.indexOf(-1); // find first free block
                if(freeSpace > -1) { // if there is free block
                    blocksForFile.add(freeSpace); // add block to array of blocks for file
                    blocks.set(freeSpace, 1); // set block to used
                }
                else {
                    System.out.println("No enough space for file, please delete some files");
                    blocks.set(startIndex, -1); // set block to free
                    for(Integer e :  blocksForFile) // deallocate
                        blocks.set(e, -1); // set block to free
                    return -1;
                }
            }
            file.set_StartIndex(startIndex); // set start index of file
            StoredFilesToIndexFiles.put(file.get_FileName() ,startIndex ); // add file to list of files
            file.set_AllocatedBlocks(blocksForFile); // set blocks for file
            IndexBlocks.put(startIndex, blocksForFile); // add blocks for file to index

        }
        else {

            System.out.println("No enough space for file, please delete some files");
            return -1; // no free block
        }
        return startIndex; // return start index
    }
    @Override
    public void deAllocate(VFile file) {
        int startIndex = file.get_StartIndex(); // get start index of file
        ArrayList<Integer> blocksForFile = file.get_AllocatedBlocks(); // get blocks for file
        for(Integer e : blocksForFile) { // deallocate blocks for file
            blocks.set(e, -1); // set block to free
        }
        blocks.set(startIndex, -1); // set block to free
        StoredFilesToIndexFiles.remove(file.get_FileName()); // remove file from list of files
        IndexBlocks.remove(startIndex); // remove blocks for file from index
    }


    @Override
    public void displayDiskStatus() {
        int allocated = 0 , freeSpace = 0;
        StringBuilder bitVector = new StringBuilder();
        for(int i = 0 ; i < blocks.size() ; i++)
        {
            if(blocks.get(i)!=-1){
                // if block is allocated
                allocated++; // increase allocated blocks
                bitVector.append(" 1"); // add 1 to bit vector
            }
            else{
                freeSpace++; // increase free space
                bitVector.append(" 0"); // add 0 to bit vector
            }

        }
        System.out.println("Disk Space:"+bitVector + " ");
        System.out.println("Number of allocated BLocks = " + allocated);
        System.out.println("Number of Free BLocks = " + freeSpace);
        System.out.println("Total number of blocks = " + blocks.size()  + "\n");
    }

    public void saveStatues(String path) {
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(VFS, true)); // clear file
            out.write(path); // write to file
            String [] arr= path.split("/");
            String name = arr[arr.length-1];
            int ind=StoredFilesToIndexFiles.get(name);

            out.write(" "+(ind+1)+"\n");
            out.write((ind+1)+" ");
            ArrayList<Integer> blocksForFile= IndexBlocks.get(ind);
            for(int i = 0 ; i < blocksForFile.size() ; i++)
            {
                out.write(blocksForFile.get(i)+" ");
            }
            out.write("\n");
            out.close(); // close file
        }
        catch (IOException e) {
            System.out.println("EXCEPTION!" + e);
        }


    }
    public VDirectory loadStatues(){
        VDirectory root = new VDirectory("root");
        VFile file;
        try{
            BufferedReader in = new BufferedReader(new FileReader(VFS)); // clear file
            String line,line2;
            while((line = in.readLine()) != null) {
               line2=in.readLine();
               String [] arr= line.split(" ");
                String [] name= arr[0].split("/");
                String [] blocks= line2.split(" ");
                String path=name[0];
                for(int i=1;i<name.length-1;i++)
                {
                    path=path+"/"+name[i];
                    root.addNewDirectory(path);
                }
                path=path+"/"+name[name.length-1];
                int counter=blocks.length-1;
                root.addNewFile(path,counter);

                file=root.checkFilePath(path);
                allocate(file);


            }
            in.close(); // close file
        }
        catch (IOException e) {
            System.out.println("EXCEPTION!" + e);
        }
        return root;
    }


}
