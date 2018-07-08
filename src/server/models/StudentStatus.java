package server.models;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StudentStatus {
	Active, 
	Inactive;
	
	public static List<String> getList(){
		return Arrays.stream(StudentStatus.values()).map(StudentStatus::toString).collect(Collectors.toList());
	}
	
}
