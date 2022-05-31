import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
	boolean exit = false;

	public static void main(String[] args) throws IOException {
		Verification verification = Verification.getInstance();
		// VDirectory root = new VDirectory("root");
		// root.addNewDirectory("root/sayed#1");
		// root.addNewDirectory("root/sayed#1/sayedinside#2");
		User user = new User();

		/*try {
			while (true) {

				Scanner scanner = new Scanner(System.in);
						System.out.println("Input the command you want to use: ");

						String choice2 = scanner.nextLine();
						Parser parser = new Parser();
						parser.parse(choice2);
						String commandName = parser.getCommandName();
						ArrayList<String> args1 = parser.getArgs();
						switch (commandName)
						{

							case "TellUser":
								String currentUser = user.TellUser();
								System.out.println(currentUser);
								break;
							case "CUser":
								user.addUser(args1.get(0), args1.get(1));
								break;
							case "Grant":
								user.grantUser(args1.get(0), args1.get(1),args1.get(2), root);
								break;
							case "Login":
								boolean flag = user.checkUser(args1.get(0), args1.get(1));
								if(flag)
								{
									user.setCurrentUser(args1.get(0));
								} else {
									System.out.println("Cannot find user.");
								}
								break;

							default:
								System.out.println("Invalid input");
								break;


						}


			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}*/
		/*Verification verification = Verification.getInstance();
		VDirectory root = new VDirectory("root");
		root.addNewDirectory("root/sayed#1");
		root.addNewDirectory("root/sayed#1/sayedinside#2");

		User u = new User();
		u.addUser ( "mariam", "admin" );

		u.addUser ( "newUser", "xxx" );

		verification.setUserCredentials(u);

		//verification.login("mariam", "admin");


		 u.grantUser("newUser" , "root/sayed#1/sayedinside#2" , "11" , root);
		 u.grantUser("mariam" , "root/sayed#1" , "01" , root);
		 //verification.login("newUser", "xxx");
		//u.createCheaker("newUser");*/
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
						if(user.TellUser().equals("admin"))
						{
							boolean flag2 = root.addNewDirectory(args1.get(0));
							if (flag2) {
								System.out.println("Folder created successfully");
							} else {
								System.out.println("Folder creation failed");
							}

						} else{
							boolean flag3 = user.createCheaker(user.TellUser());
							if(flag3)
							{
								boolean flag2 = root.addNewDirectory(args1.get(0));
								if (flag2) {
									System.out.println("Folder created successfully");
								} else {
									System.out.println("Folder creation failed");
								}
							} else {
								System.out.println("This user can't create folders here.");
							}
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
						if(user.TellUser().equals("admin"))
						{
							dir1 = root.checkDirectoryPath(args1.get(0));
							if (dir1 != null) {
								System.out.println("Folder deleted successfully");
								dir1.set_Deleted(true);
							} else {
								System.out.println("Folder deletion failed");
							}

						} else {
							boolean flag4 = user.deleteCheaker(user.TellUser());
							if (flag4) {
								dir1 = root.checkDirectoryPath(args1.get(0));
								if (dir1 != null) {
									System.out.println("Folder deleted successfully");
									dir1.set_Deleted(true);
								} else {
									System.out.println("Folder deletion failed");
								}
							} else {
								System.out.println("This user can't delete folders here.");
							}
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
					case "TellUser":
						String currentUser = user.TellUser();
						System.out.println(currentUser);
						break;
					case "CUser":
						user.addUser(args1.get(0), args1.get(1));
						break;
					case "Grant":
						user.grantUser(args1.get(0), args1.get(1),args1.get(2), root);
						break;
					case "Login":
						boolean flag1 = user.checkUser(args1.get(0), args1.get(1));
						if(flag1)
						{
							user.setCurrentUser(args1.get(0));
						} else {
							System.out.println("Cannot find user.");
						}
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
