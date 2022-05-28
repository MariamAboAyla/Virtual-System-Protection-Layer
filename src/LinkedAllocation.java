import java.io.*;
import java.util.*;


public class LinkedAllocation implements AllocationManager {

    private ArrayList<Integer> blocks = new ArrayList<>(); // of blocks size
    private final HashMap<VFile, LinkedList<Integer> > files = new HashMap<> (); // file -> linked-list of assigned blocks

    public final String VFS = "VFLinked.vfs";

    LinkedAllocation(int size)
    {
        for(int i = 0 ;i < size ; i++)
        {
            blocks.add(0);
        }
    }

    public void setBlocks (ArrayList<Integer> blocks)
    {
        this.blocks = blocks;
    }

    public ArrayList<Integer> getBlocks ( )
    {
        return this.blocks;
    }


    public int allocate (VFile file)
    {

        // check if have enough space
        int emptySpaceSize = getSpaceManager();
        if((file.get_Size () > blocks.size ())  ||  emptySpaceSize<file.get_Size () )
        {
            System.out.println ("Unable to allocate file! No Enough Space !" );
            return -1;
        }


        int nextBlockIndex = blocks.indexOf ( 0 ); // first available block
        int startIndex = nextBlockIndex;
        blocks.set ( nextBlockIndex , 1 );
        int fileSize = file.get_Size ();
        LinkedList<Integer> fileBlocks = new LinkedList<> ( ) ;
        fileBlocks.add ( nextBlockIndex );

        for (int i=0; i<fileSize-1; i++)
        {
            nextBlockIndex = blocks.indexOf ( 0 );// first available block
            blocks.set ( nextBlockIndex , 1 );
            fileBlocks.add ( nextBlockIndex );
        }

        files.put ( file, fileBlocks); // add the file and its assigned blocks to list

        return startIndex;

    }

    @Override
    public void deAllocate( VFile vFile)
    {
        //if delete it -> will delete from: file list - deallocate blocks
        for (Map.Entry<VFile, LinkedList<Integer> > iterator: files.entrySet ())
        {
            if( Objects.equals ( iterator.getKey ( ).get_FileName ()  ,  vFile.get_FileName ()) )
            {

                //deallocates the blocks
                for (Integer allocatedBlocks: iterator.getValue () )
                {
                    blocks.set ( allocatedBlocks, 0 ); // deallocate blocks - mark free
                }

                // empties the blocks linked list of the file
                iterator.getValue ().clear ();


                // empties the file from the file list
                files.remove ( iterator.getKey (), iterator.getValue () ); // delete it from files list

                break;

            }
        }


    }


    public void displayDiskStatus()
    {
        int sizeReamining = getSpaceManager ();
        StringBuilder systemBlocks = getFreeSpaceManager ();
        int systemBlocksSize = systemBlocks.length();

        System.out.println ("Size Remaining: "+ sizeReamining + " KB" );
        System.out.println ("Allocated Space: "+ (blocks.size () - sizeReamining)+" KB" );
        System.out.println ("Blocks (1s means allocated and 0s means free):  "+ systemBlocks );
        // could print each of them alone -> by for loop
        /*
        for(int i=0; i<systemBlocksSize; i++)
        {
            System.out.print ("("+(i+1)+", "+systemBlocks.charAt(i)+")  " );
            if(i+1 != systemBlocksSize){
                System.out.print (", " );
            }
        }
        System.out.println ();
        */

    }


    public LinkedList<Integer> getListOfBlocks(String name)
    {
        for (Map.Entry<VFile, LinkedList<Integer>> block: files.entrySet ())
        {
            if(block.getKey ().get_FileName ().equals ( name ))
            {
                return block.getValue ();
            }
        }

        return null;
    }

    public void saveStatues(String path) {
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter (VFS, true)); // clear file
            out.write(path); // write to file
            String [] arr= path.split("/");
            String name = arr[arr.length-1];

            LinkedList<Integer> listBlocks = getListOfBlocks(name);
            int startIndex=0, endIndex = 0, size=listBlocks.size ();

            if(listBlocks == null)
            {
                System.out.println ("Empty File!" );
                return;
            }

            int cnt=1; //to know if start index or end
            for(Integer blockIndex: listBlocks)
            {
                if(cnt==1) startIndex = blockIndex;
                else if (cnt==size) endIndex = blockIndex;
                cnt++;
            }

            out.write (" "+(startIndex)+"  "+(endIndex) +"\n" );
            int prev =startIndex;
            for(Integer blockIndex: listBlocks)
            {
                if(blockIndex==startIndex){
                    prev = blockIndex;
                }
                out.write ( prev + " "+blockIndex+"\n" );
                prev = blockIndex;
            }
            out.write ( prev + " nil"+"\n" );

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
            BufferedReader in = new BufferedReader(new FileReader (VFS)); // clear file
            String line,line2;
//            int cnt=0; // cnt=0-> path, start, end; cnt=1-> array of indices
            while( (line = in.readLine()) != null) {
                int cnt=0;
                while( (line2=in.readLine ()) != null)
                {
                    cnt++;
                    String []data = line2.split ( " " );
                    if(data[data.length-1].equalsIgnoreCase ( "nil" ))
                    {
                        break;
                    }

                }

                String [] arr= line.split(" ");
                String [] name= arr[0].split("/");
                String path=name[0];
                for(int i=1;i<name.length-1;i++)
                {
                    path=path+"/"+name[i];
                    root.addNewDirectory(path);
                }
                path=path+"/"+name[name.length-1];

                root.addNewFile(path,cnt);

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


    /*
    public void displayAllocatedBlocks(String filePath)
    {
        VFile vFile = null;
        LinkedList<Integer> list = new LinkedList<> ();
        int startIndex = 0, endIndex = 0;
        for (Map.Entry<VFile, LinkedList<Integer> > iterator: files.entrySet ())
        {
            if( Objects.equals ( iterator.getKey ( ).get_FilePath ( )   ,    filePath ) )
            {
                vFile = iterator.getKey ();
                list = iterator.getValue ( );

            }
        }
        if(vFile == null)
        {
            System.out.println ("No such a file !" );
            return;
        }

        System.out.println ( filePath + "   "+startIndex+"  "+endIndex);
        int prev = startIndex;
        for (Integer iterator: list)
        {
            if(iterator==startIndex) continue;
            System.out.println ( prev + "   " + iterator);
            prev = iterator;
        }
        System.out.println (prev + "   nil" );


    }
    */



    public int getSpaceManager()
    {
        // it returns the number of empty blocks
        int emptySize = 0;// get the size of the empty blocks remaining
        StringBuilder freeSpace = getFreeSpaceManager();
        for (int i=0; i<freeSpace.length (); i++)
        {
            if (freeSpace.charAt ( i ) == '0')
            {
                emptySize++;
            }
        }

        return emptySize;
    }

    public StringBuilder getFreeSpaceManager()
    {
        // get the blocks in  a string of 0's and 1's ->
        // if 0 means block at ith item is empty, while if 1 means block at ith item is accompained
        StringBuilder result = new StringBuilder ( );
        for (Integer block : blocks) {
            result.append ( Integer.toString ( block ) );
        }

        return result;
    }



}
