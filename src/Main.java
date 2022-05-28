import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	 public static void main(String[] args)
    {


		AllocationManager manager = null;
		VFile file1;
		VDirectory dir1;
		System.out.println("Enter the size of the disk");
		Scanner scannerSize = new Scanner(System.in);
		int size=scannerSize.nextInt();
		boolean exit = false;

		while(!exit ) {
			boolean exit2=false;
			System.out.println("Input the method you want to use: ");
			System.out.println("1-Contiguous\n2-Indexed\n3-Linked\n4-Exit");
			Scanner scanner = new Scanner(System.in);
			int choice = scanner.nextInt();
			VDirectory root = null;
			switch (choice) {
				case 1:
					manager = new ContiguousAllocation(size);
					root= ((ContiguousAllocation)manager).loadStatues();
					if(root==null)
					{
						root = new VDirectory("root");
					}
					break;
				case 2:
					manager = new IndexedAllocation(size);
					root= ((IndexedAllocation)manager).loadStatues();
					if(root==null)
					{
						root = new VDirectory("root");
					}

					break;
				case 3:
					manager = new LinkedAllocation(size);
					root= ((LinkedAllocation)manager).loadStatues();
					if(root==null)
					{
						root = new VDirectory("root");
					}
					break;
				case 4:
					exit = true;
					exit2 = true;
					break;
				default:
					System.out.println("Invalid input");
					break;
			}
			while (!exit2) {
				Parser parser = new Parser();
				System.out.println("Input the command you want to use: ");
				Scanner scanner2 = new Scanner(System.in);
				String choice2 = scanner2.nextLine();
				parser.parse(choice2);
				String commandName = parser.getCommandName();
				ArrayList<String> args1 = parser.getArgs();
				switch (commandName) {
					case "CreateFile":
						boolean flag = root.addNewFile(args1.get(0), Integer.parseInt(args1.get(1)));
						if (flag) {
							file1 = root.checkFilePath(args1.get(0));
							assert manager != null;
							int val=manager.allocate(file1);
							if(val==-1) {
								System.out.println("File creation failed");
								file1.set_Deleted(true);
							}
							else {
								System.out.println("File created successfully");
								if(manager instanceof LinkedAllocation){
									((LinkedAllocation) manager).saveStatues(args1.get(0));
								} else if (manager instanceof ContiguousAllocation) {
									((ContiguousAllocation) manager).saveStatues(args1.get(0));
								} else if (manager instanceof IndexedAllocation) {
									((IndexedAllocation) manager).saveStatues(args1.get(0));
								}
							}
						} else {
							System.out.println("File creation failed");
						}
						break;
					case "CreateFolder":
						boolean flag2 = root.addNewDirectory(args1.get(0));
						if (flag2) {
							System.out.println("Folder created successfully");
						} else {
							System.out.println("Folder creation failed");
						}
						break;
					case "DeleteFile":
						file1 = root.checkFilePath(args1.get(0));
						if (file1 != null) {
							System.out.println("File deleted successfully");
							file1.set_Deleted(true);
							manager.deAllocate(file1);
						} else {
							System.out.println("File deletion failed");
						}
						break;
					case "DeleteFolder":
						dir1 = root.checkDirectoryPath(args1.get(0));
						if (dir1 != null) {
							System.out.println("Folder deleted successfully");
							dir1.set_Deleted(true);
						} else {
							System.out.println("Folder deletion failed");
						}
						break;
					case "DisplayDiskStatues":
						System.out.println("Disk status: ");
						assert manager != null;
						manager.displayDiskStatus();
						break;
					case "DisplayDiskStructure":
						System.out.println("Disk structure: ");
						root.printDiskStructure("");
						break;
					case "Exit":
						exit2 = true;

						break;
					default:
						System.out.println("Invalid input");
						break;
				}

			}
		}



	}
}
