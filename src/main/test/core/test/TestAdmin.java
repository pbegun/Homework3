package core.test;

import core.api.IAdmin;
import core.api.impl.Admin;
import core.api.IStudent;
import core.api.impl.Student;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestAdmin {
	
	private IAdmin admin;
	private IStudent student;

    @Before
    public void setup() {
        this.admin = new Admin();
        this.student = new Student();
    }
    
    /**
     * createClass() Tests:
     * 1) Year is not in past
     * 2) Capacity > 0
     * 3) Instructor is not assigned to more than 2 courses per year
     * 4) className/year pair is unique
     */

    // 1) Year is not in past
    @Test
    public void testMakeClassYear() {
        this.admin.createClass("Test", 2017, "Instructor", 15);	// Class is in current year
        assertTrue(this.admin.classExists("Test", 2017));
    }

    @Test
    public void testMakeClassYear2() {
        this.admin.createClass("Test", 2016, "Instructor", 15); // Class is in past year
        assertFalse(this.admin.classExists("Test", 2016));
    }
    
    @Test
    public void testMakeClassYear3() {
    	this.admin.createClass("Test", 2018, "Instructor", 15); // Class is in future year
    	assertTrue(this.admin.classExists("Test", 2018));
    }
    
    // 2) Capacity > 0
    @Test
    public void testMakeClassCap() {
    	this.admin.createClass("Test", 2017, "Instructor", 1); // Capacity > 0
    	assertTrue(this.admin.classExists("Test", 2017));
    }
    
    @Test
    public void testMakeClassCap2() {
    	this.admin.createClass("Test", 2017, "Instructor", -1); // Capacity < 0
    	assertFalse(this.admin.classExists("Test", 2017));
    }
    
    @Test
    public void testMakeClassCap3() {
    	this.admin.createClass("Test", 2017, "Instructor", 0); // Capacity == 0
    	assertFalse(this.admin.classExists("Test", 2017));
    }
    
    // 3) Instructor is not assigned to more than 2 classes per year 
    @Test
    public void testMakeClassInstructor() {
    	this.admin.createClass("Test", 2017, "Instructor", 15);
    	this.admin.createClass("Test_1", 2017, "Instructor", 15);	// Instructor assigned to 2 classes in same year
    	assertTrue(this.admin.classExists("Test", 2017) && this.admin.classExists("Test_1", 2017));
    }
    
    @Test
    public void testMakeClassInstructor2() {
    	this.admin.createClass("Test", 2017, "Instructor", 15);
    	this.admin.createClass("Test_1", 2017, "Instructor", 15);
    	this.admin.createClass("Test_2", 2018, "Instructor", 15); // Instructor assigned to 3 courses, not in same year
    	assertTrue(this.admin.classExists("Test", 2017) && 
    			   this.admin.classExists("Test_1", 2017) && 
    			   this.admin.classExists("Test_2", 2018));
    }
    
    @Test
    public void testMakeClassInstructor3() {
    	this.admin.createClass("Test", 2017, "Instructor", 15);
    	this.admin.createClass("Test_1", 2017, "Instructor", 15);
    	this.admin.createClass("Test_2", 2017, "Instructor", 15);	// Instructor assigned to 3 classes in same year
    	assertFalse(this.admin.classExists("Test_2", 2017));
    }
    
    // 4) className/year pair is unique
    @Test
    public void testMakeClassPair() {
    	this.admin.createClass("Test", 2017, "Instructor", 15);
    	this.admin.createClass("Test", 2017, "Instructor_1", 15);	// Duplicate className/year pair
    	assertTrue(this.admin.getClassInstructor("Test", 2017).equals("Instructor"));
    }
    
    /**
     * changeCapacity() tests:
     * 1) New capacity >= number of enrollees
     */
    
    // 1) New capacity >= number of enrollees
    @Test
    public void testChangeCapSize() {
    	this.admin.createClass("Test", 2017, "Instructor", 15);
    	this.student.registerForClass("Student", "Test", 2017);
    	int numEnrollees = 1; // only Student is enrolled in class
    	
    	this.admin.changeCapacity("Test", 2017, 2);	// New capacity > numEnrollees
    	assertTrue(this.admin.getClassCapacity("Test", 2017) >= numEnrollees);
    }
    
    @Test
    public void testChangeCapSize2() {
    	this.admin.createClass("Test", 2017, "Instructor", 15);
    	this.student.registerForClass("Student", "Test", 2017);
    	int numEnrollees = 1; // only Student is enrolled in class
    	
    	this.admin.changeCapacity("Test", 2017, 0);		// New capacity < numEnrollees
    	
    	// changeCapacity() should have failed --> capacity should still be 15
    	assertTrue(this.admin.getClassCapacity("Test", 2017) >= numEnrollees);
    }
    
    @Test
    public void testChangeCapSize3() {
    	this.admin.createClass("Test", 2017, "Instructor", 15);
    	this.student.registerForClass("Student", "Test", 2017);
    	int numEnrollees = 1; // only Student is enrolled in class
    	
    	this.admin.changeCapacity("Test", 2017, 1);		// New capacity == numEnrollees
    	assertTrue(this.admin.getClassCapacity("Test", 2017) >= numEnrollees);
    }
 
}
