package client.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

import client.eventbus.ApplicationEvent;
import client.eventbus.ApplicationEventBus;
import client.models.AuthenticationContext;
import client.views.utilities.RemoteSupplier;
import server.DcmsServer;
import server.models.BaseRecord;
import server.models.CourseType;
import server.models.RecordCount;
import server.models.StudentRecord;
import server.models.StudentStatus;
import server.models.TeacherRecord;
import server.validation.ValidationException;

public class ServerProxy {

	private ApplicationEventBus applicationEventBus;
	private AuthenticationService authenticationService;
	private String clientId;
	private DcmsServer server;
	
	public ServerProxy(ApplicationEventBus applicationEventBus, AuthenticationService authenticationService) {
		this.applicationEventBus = applicationEventBus;
		this.authenticationService = authenticationService;
		authenticationService.addListener(this::onAuthenticationContextChanged);
	}
	
	public void onAuthenticationContextChanged(AuthenticationContext authenticationContext){
		this.server = authenticationContext.getConnectedServer();
		this.clientId = authenticationContext.getClientId();
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public CompletionStage<TeacherRecord> createTeacherRecord(String firstName, String lastName, String address, String phone, CourseType specialization, String location){
		
		String logMessage = "Creating new teacher: firstName=" + firstName + " lastName=" + lastName + 
				" address=" + address + " phone=" + phone + " specialization=" + specialization + " location=" + location;
		
		return executeInternal(() -> server.createTeacherRecord(firstName, lastName, address, 
					phone, specialization, 
					location, clientId), 
				logMessage,
				r -> String.format("Create teacher: %s created", r.getId()));
	}
	
	public CompletionStage<StudentRecord> createStudentRecord(String firstName, String lastName, List<CourseType> courses, StudentStatus status, LocalDateTime statusDate){
		
		String logMessage = "Creating new student: firstName=" + firstName + " lastName=" + lastName
				+ " coursesRegistered=" + courses.stream().map(CourseType::toString).collect(Collectors.joining(", "))
				+ " status=" + status + " statusDate=" + statusDate;
		
		return executeInternal(() -> server.createStudentRecord(firstName, lastName, 
					courses, status, 
					statusDate.toString(), clientId), 
				logMessage,
				r -> String.format("Create student: %s created"));
	}
	
	public CompletionStage<BaseRecord> editRecord(String id, String fieldName, String newValue) {
		return executeInternal(() -> server.editRecord(id, fieldName, newValue, clientId), 
				"Editing record id=" + id + " fieldName=" + fieldName + " newValue=" + newValue, 
				r -> id + " updated");
	}

	public CompletionStage<Boolean> transferRecord(String recordId, String recipientServer){
		return executeInternal(() -> server.transferRecord(recordId, recipientServer, clientId), 
				String.format("Transfering record %s to server %s", recordId, recipientServer), 
				r -> String.format("%s transfered to %s", recordId, recipientServer));
	}
	
	public CompletionStage<List<RecordCount>> getRecordCounts(){
		return executeInternal(() -> server.getRecordCounts(clientId), 
				"Retrieving record counts.",
				r -> String.format("Record counts: %s", r.toString()));
	}
	
	public CompletionStage<List<TeacherRecord>> getTeacherRecords(char lastNameFiler){
		
		return executeInternal(() -> server.getTeachersWithNameFilter(lastNameFiler, clientId), 
				"Retrieving teacher records with '" + lastNameFiler + "' filter",
				r -> String.format("Teacher list with '%s' filter: %s records retrieved", lastNameFiler, r.size()));
	}
	
	public CompletionStage<List<StudentRecord>> getStudentRecords(char lastNameFiler){
		return executeInternal(() -> server.getStudentsWithNameFilter(lastNameFiler, clientId), 
				"Retrieving student records with '" + lastNameFiler + "' filter", 
				r -> String.format("Student list with '%s' filter: %s records retrieved", lastNameFiler, r.size()));
	}
	
	public CompletionStage<List<TeacherRecord>> getTeacherRecords(){
		return executeInternal(() -> server.getTeachers(clientId), 
				"Retrieving teacher records.",
				r -> String.format("Teacher list: %s records retrieved", r.size()));
	}
	
	public CompletionStage<List<StudentRecord>> getStudentRecords(){
		return executeInternal(() -> server.getStudents(clientId), 
				"Retrieving student records.", 
				r -> String.format("Student list: %s records retrieved", r.size()));
	}
	
	private <T> CompletionStage<T> executeInternal(RemoteSupplier<T> supplier, String infoMessage, 
			Function<T, String> successLogger){
		
		applicationEventBus.raiseEvent(ApplicationEvent.loading(infoMessage));
		
		return CompletableFuture.supplyAsync(() -> {
			try {
				T result = supplier.get();
				applicationEventBus.raiseEvent(ApplicationEvent.success(successLogger.apply(result)));
				return result;
			}catch (ValidationException e){
				applicationEventBus.raiseEvent(ApplicationEvent.error(e.getMessage()));
				throw new CompletionException(e);
			} catch (Exception e) {
				String errorMessage = e.getCause() == null ? e.getMessage() : e.getCause().getMessage(); 
				applicationEventBus.raiseEvent(ApplicationEvent.error(errorMessage));
				throw new CompletionException(e);
			}
		});
	}
}
