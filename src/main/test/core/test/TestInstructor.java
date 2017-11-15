package core.test;

import core.api.IAdmin;
import core.api.impl.Admin;
import core.api.IInstructor;
import core.api.impl.Instructor;
import core.api.IStudent;
import core.api.impl.Student;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestInstructor {
	
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
	 * addHomework() tests:
	 * 1) Instructor has been assigned to this class
	 */
	
	// 1) Instructor has been assigned to this class
	@Test
	public void testAddHomeworkInstructor() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW"); // Instructor has been assigned to this class
		assertTrue(this.instructor.homeworkExists("Test", 2017, "HW"));
	}
	
	@Test
	public void testAddHomeworkInstructor2() {
		this.admin.createClass("Test", 2017, "Instructor", 2017);
		this.instructor.addHomework("Instructor_1", "Test", 2017, "HW"); // Instructor hasn't been assigned to this class
		assertFalse(this.instructor.homeworkExists("Test", 2017, "HW"));
	}
	
	/**
	 * assignGrade() tests:
	 * 1) Instructor has been assigned to this class
	 * 2) Homework has been assigned
	 * 3) Student has submitted the homework
	 * 4) Grade is a valid percentage (0 <= grade <= 100)
	 * 
	 * NOTE: submitHomework() checks that student is registered for this class, so we don't need to check that here
	 * 
	 */
	
	// 1) Instructor has been assigned to this class
	@Test
	public void testAssignGradeInstructor() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 90); // Instructor has been assigned to class
		
		assertNotNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	@Test
	public void testAssignGradeInstructor2() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor_1", "Test", 2017, "HW", "Student", 90); // Instructor hasn't been assigned to class
		
		assertNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	// 2) Homework has been assigned
	@Test
	public void testAssignGradeHomework() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");		// Assign HW
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 90);
		
		assertNotNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	@Test
	public void testAssignGradeHomework2() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
																			// Don't assign HW
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 90);
		
		assertNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	// 3) Student has submitted the homework
	@Test
	public void testAssignGradeSubmit() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);		// Submit HW
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 90);
		
		assertNotNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));;
	}
	
	@Test
	public void testAssignGradeSubmit2() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
																					// Don't submit HW
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 90);
		
		assertNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	// 4) Grade is a valid percentage (0 <= grade <= 100)
	@Test
	public void testAssignGradePercentage() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 90); // 0 < Grade < 100
		
		assertNotNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	@Test
	public void testAssignGradePercentage2() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 0); // Grade == 0
		
		assertNotNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	@Test
	public void testAssignGradePercentage3() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 100); // Grade == 100
		
		assertNotNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	@Test
	public void testAssignGradePercentage4() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", -1); // Grade < 0
		
		assertNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
	
	@Test
	public void testAssignGradePercentage5() {
		this.admin.createClass("Test", 2017, "Instructor", 15);
		this.student.registerForClass("Student", "Test", 2017);
		this.instructor.addHomework("Instructor", "Test", 2017, "HW");
		this.student.submitHomework("Student", "HW", "Solution", "Test", 2017);
		this.instructor.assignGrade("Instructor", "Test", 2017, "HW", "Student", 101); // Grade > 100
		
		assertNull(this.instructor.getGrade("Test", 2017, "HW", "Student"));
	}
}
