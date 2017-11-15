package core.test;

import core.api.IAdmin;
import core.api.impl.Admin;
import core.api.IInstructor;
import core.api.impl.Instructor;
import core.api.IStudent;
import core.api.impl.Student;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestStudent {
	
	private IAdmin admin;
	private IInstructor instructor;
	private IStudent student;
	
	@Before
	public void setup() {
		this.admin = new Admin();
		this.instructor = new Instructor();
		this.student = new Student();
	}
	
	/**
	 * registerForClass() Tests:
	 * 1) Class must exist
	 * 2) Class must not have met enrollment capacity yet
	 * 3) Student must not already be registered for this class
	 */
	
	// 1) Class must exist
	@Test
	public void testRegisterClassExists() {
		this.admin.createClass("Test", 2017, "Instructor", 15); 	// Class exists
		this.student.registerForClass("Student", "Test", 2017);
		assertTrue(this.student.isRegisteredFor("Student", "Test", 2017));
	}
	
	@Test
	public void testRegisterClassExists2() {
		 															// Class doesn't exist
		this.student.registerForClass("Student", "Test", 2017);
		assertFalse(this.student.isRegisteredFor("Student", "Test", 2017));
	}
	
	// 2) Class must not have met enrollment capacity yet
	@Test
	public void testRegisterEnrollmentCap() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017); 	// Class hasn't met enrollment cap. yet
		assertTrue(this.student.isRegisteredFor("Student", "Test", 2017));
	}
	
	@Test
	public void testRegisterEnrollmentCap2() {
		this.admin.createClass("Test", 2017, "Instructor", 1);
		this.student.registerForClass("Student", "Test", 2017); 	// Class is at enrollment cap.
		assertTrue(this.student.isRegisteredFor("Student", "Test", 2017));
	}
	
	@Test
	public void testRegisterEnrollmentCap3() {
		this.admin.createClass("Test", 2017, "Instructor", 1);
		this.student.registerForClass("Student", "Test", 2017);
		this.student.registerForClass("Student_2", "Test", 2017); 	// Class is over enrollment cap.
		assertFalse(this.student.isRegisteredFor("Student_2", "Test", 2017));
	}
	
	// 3) Student must not already be registered for this class
	@Test
	public void testStudentRegistered() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017); 	// Student is not already registered for this class
		assertTrue(this.student.isRegisteredFor("Student", "Test", 2017));
	}
	
	@Test
	public void testStudentRegistered1() {
		this.admin.createClass("Test", 2017, "Instructor", 2);
		this.student.registerForClass("Student", "Test", 2017);
		this.student.registerForClass("Student", "Test", 2017); 	// Student is already registered for this class
		
		// The second registerForClass() call above shouldn't have done anything, since
		//		Student had already registered for that class. This means the enrollment
		//		for Test should still be at 1, which means the enrollment capacity (2)
		//		has not yet been met. Therefore, we know that the registerForClass()
		//		method worked the way it should have worked if we are able to register
		//		another student for the same class
		// NOTE: this only works if the method passes all of the enrollment capacity tests
		//			for registerForClass() [condition 2 above]
		this.student.registerForClass("Student_2", "Test", 2017);
		assertTrue(this.student.isRegisteredFor("Student_2", "Test", 2017));
	}
	
	/**
	 * dropClass() Tests:
	 * 1) Student must be registered for this class
	 */
	
	// 1) Student must be registered for this class
	@Test
	public void testDropStudentRegistered() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017); 	// Student registered
		
		// registered = was student registered before call to dropClass() ?
		boolean registered = this.student.isRegisteredFor("Student", "Test", 2017);
		this.student.dropClass("Student", "Test", 2017);
		
		// if isRegisteredFor() returns the different value than it did before dropClass(),
		// then call to dropClass() successfully dropped student form class
		assertFalse(this.student.isRegisteredFor("Student", "Test", 2017) == registered);
	}
	
	@Test
	public void testDropStudentRegistered2() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
																	// Student hasn't registered
		
		// registered = was student registered before call to dropClass() ? [should be false]
		boolean registered = this.student.isRegisteredFor("Student", "Test", 2017);
		this.student.dropClass("Student", "Test", 2017);
		
		// if isRegisteredFor() returns the same value as it did before dropClass(),
		// then call to dropClass() didn't drop student from class (what we want in this case)
		assertTrue(this.student.isRegisteredFor("Student", "Test", 2017) == registered);
	}
	
	/**
	 * submitHomework() Tests:
	 * 1) HW must exist
	 * 2) Student must be registered for this class
	 * 3) Class must be taught in the current year (not future)
	 */
	
	// 1) HW must exist
	@Test
	public void testSubmitHWExists() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");		// HW exists
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		
		assertTrue(this.student.hasSubmitted("Student", "HW", "Test", 2017));
	}
	
	@Test
	public void testSubmitHWExists2() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
																			// HW doesn't exist
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		
		assertFalse(this.student.hasSubmitted("Student", "HW", "Test", 2017));
	}
	
	// 2) Student must be registered for this class
	@Test
	public void testSubmitRegistered() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017); 			// Student is registered
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		
		assertTrue(this.student.hasSubmitted("Student", "HW", "Test", 2017));
	}
	
	@Test
	public void testSubmitRegistered2() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
																			// Student isn't registered
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		
		assertFalse(this.student.hasSubmitted("Student", "HW", "Test", 2017));
	}
	
	// 3) Class must be taught in current year (not future)
	@Test
	public void testSubmitYear() {
		this.admin.createClass("Test", 2017, "Instructor", 15);			// Class is taught in current year
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		
		assertTrue(this.student.hasSubmitted("Student", "HW", "Test", 2017));
	}
	
	@Test
	public void testSubmitYear2() {
		this.admin.createClass("Test", 2018, "Instructor", 15);			// Class is taught in future year
		this.student.registerForClass("Student", "Test", 2018);
		this.instructor.addHomework("Instructor", "Test", 2018, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2018);
		
		assertFalse(this.student.hasSubmitted("Student", "HW", "Test", 2018));
	}
}
