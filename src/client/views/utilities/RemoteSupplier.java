package client.views.utilities;

@FunctionalInterface
public interface RemoteSupplier<T> {

	public T get() throws Exception;
	
}
