package cs3114.J3.DS;

/**
 * Interface for Hash function intended to provide
 * indexing calculations for HashTable implementation.
 */
public interface Hashable<T> {
	
	// Returns an index for HashTable.
	public int Hash();
}
