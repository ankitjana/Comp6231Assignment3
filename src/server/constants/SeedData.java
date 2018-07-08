package server.constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import server.models.CourseType;
import server.models.StudentRecord;
import server.models.StudentStatus;
import server.models.TeacherRecord;

/**
 * Database seed data 
 *
 */
public class SeedData {
	
	public static List<TeacherRecord> getTeacherSeeds(String serverId){
		return Arrays.asList(
				new TeacherRecord("Alex", "Bishop", "5115 Beacon Street, H3O2A6", "514-514-5145", CourseType.Biology, serverId),
				new TeacherRecord("Michalina", "Kelly", "4590 Atwater Avenue, H6W7K2", "514-514-6501", CourseType.English, serverId),
				new TeacherRecord("Corinne", "Espinosa", "1190 Berri Street, H3T2L4", "514-212-3498", CourseType.Math, serverId),
				new TeacherRecord("Lisa", "Lyon", "7400 Champlain Street, H9X231", "514-322-1190", CourseType.Physics, serverId),
				new TeacherRecord("Artur", "Neville", "1010 Saint-Laurent Street, H4A4I1", "514-451-9955", CourseType.ComputerScience, serverId)
			);
	}
		
	public static List<StudentRecord> getStudentSeeds(String serverId){
		return Arrays.asList(
				new StudentRecord("Stefan", "Wapnick", Arrays.asList(CourseType.Physics, CourseType.Biology), StudentStatus.Active, LocalDateTime.now()),
				new StudentRecord("Ankit", "Jana", Arrays.asList(CourseType.ComputerScience), StudentStatus.Inactive, LocalDateTime.now()),
				new StudentRecord("Goutham", "Gopal Raje Urs", Arrays.asList(CourseType.English), StudentStatus.Inactive, LocalDateTime.now()),
				new StudentRecord("Sriprna", "Chakraborty", Arrays.asList(CourseType.Math), StudentStatus.Active, LocalDateTime.now())
				);
	}
	
}
