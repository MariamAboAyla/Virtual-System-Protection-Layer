import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class ContiguousAllocation implements AllocationManager  {

	private ArrayList<Integer> blocks = new ArrayList<Integer>();
	private int fileStartIndex = blocks.indexOf(-1);
	public final String VFS = "VFContiguous.vfs";
	private final ArrayList<VFile> StoredFiles  = new ArrayList<>();

	//=====================================constructor======================================//
	public ContiguousAllocation(int size)
	{
		for(int i = 0 ; i < size ; i++)
		{
			blocks.add(-1);
		}
	}

	//======================================Allocate blocks for file======================================//
	@Override
	public int allocate(VFile file) {

		int start = fileStartIndex;

		int smallestSize=0;
		int size = file.get_Size();
		boolean validBlocks = false; //initialize
		for(int i = 0 ; i < blocks.size() ; i++)
		{
			if (smallestSize==size)
			{
				validBlocks = true;
				break;
			}
			if(blocks.get(i)!= -1) //allocated
			{
				smallestSize=0;
			}
			else
			{
				if(smallestSize==0){
					start = i;
				}
				smallestSize++;

			}
		}
		if(smallestSize==size)
		{
			validBlocks = true;
		}
		if (validBlocks) { //free
			for(int i = start ; i < size+start ; i++)
			{
				blocks.set( i, 1); //allocate

			}
			file.set_StartIndex(start);
			StoredFiles.add(file);
			return start;
		}
		else
		{
			System.out.println("Couldn't allocate block.");
			return -1;
		}

	}

	//=============================Free Space Manager=========================================//
	@Override
	public void displayDiskStatus()
	{

		int allocatedSpace = 0 , emptySpace = 0;
		String bitVector="";
		for(int i = 0 ; i < blocks.size() ; i++)
		{


			if(blocks.get(i)!=-1)
			{
				allocatedSpace++;
				bitVector += "1";
			}
			else
			{
				emptySpace++;
				bitVector += "0";
			}
		}

		System.out.println("Empty space: " + emptySpace);
		System.out.println("Allocated space: " + allocatedSpace);

		System.out.print("Disk space: ");
		System.out.println(bitVector + " ");

	}


	//====================deallocate file blocks ============================//
	@Override
	public void deAllocate(VFile file) {

		for(int i = file.get_StartIndex() ; i < file.get_Size()+file.get_StartIndex() ; i++)
		{
			blocks.set(i, -1);
		}
		StoredFiles.remove(file);

	}
	//=============================================================================//
	public void saveStatues(String path) {
		VFile file = null;
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(VFS, true)); // clear file
			out.write(path); // write to file
			String [] arr= path.split("/");
			String name = arr[arr.length-1];
			for (VFile storedFile : StoredFiles) {
				file = storedFile;
				if (file.get_FileName().equals(name)) {
					out.write(" " + file.get_StartIndex() + " " + file.get_Size() + "\n");
				}
			}


			out.close(); // close file
		}
		catch (IOException e) {
			System.out.println("EXCEPTION!" + e);
		}


	}
	//////////////////////
	public VDirectory loadStatues(){
		VDirectory root = new VDirectory("root");
		VFile file;
		try{
			BufferedReader in = new BufferedReader(new FileReader(VFS)); // clear file
			String line;
			while((line = in.readLine()) != null) {
				String [] arr= line.split(" ");
				String [] name= arr[0].split("/");
				String path=name[0];
				for(int i=1;i<name.length-1;i++)
				{
					path=path+"/"+name[i];
					root.addNewDirectory(path);
				}
				int size = Integer.parseInt(arr[2]);
				path=path+"/"+name[name.length-1];
				root.addNewFile(path,size);


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


