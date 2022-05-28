
public interface AllocationManager {
	void deAllocate(VFile file);
	int allocate(VFile file);

	void displayDiskStatus();

}
