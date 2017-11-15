package core.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;

import core.api.ICourseManager;
import core.api.impl.Admin;
import core.api.impl.CourseManager;

/**
 * Tests course manager. Since the admin implementation is known to be bugging.
 *
 * @author Vincent
 * 
 */

public class TestCourseManager {
	@Spy
	private Admin admin;
	private ICourseManager courseManager;
	
	@Before
	public void setup() {
		this.admin = Mockito.spy(new Admin());
		this.courseManager = new CourseManager(this.admin);
		setupMocking();
	}
	
	/**
	 * Shows some initial set-up for the mocking of Admin.
	 * This includes fixing a known bug (year in past is not correctly checked) in the Admin class by Mocking its behavior.
	 * Not all fixes to Admin can be made from here, so for the more complex constraints you can simply Mock the
	 * specific calls to Admin's createClass() to yield the correct behavior in the unit test itself.
	 */
	public void setupMocking() {
		// Example (year must be current year)
		Mockito.doNothing().when(this.admin).createClass(
				Mockito.anyString(), AdditionalMatchers.lt(2017), Mockito.anyString(), Mockito.anyInt());
		
		// Constraint 1 (capacity)
		Mockito.doNothing().when(this.admin).createClass(
				Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), AdditionalMatchers.gt(1000));
		
		// Constraint 2 (no more than 2 courses per instructor per year)
		// Execute this method up to two times for the same instructor,
		//		but do nothing on any subsequent calls (attempt to assign
		//		>2 classes to same professor in same year)
		Mockito.doCallRealMethod()
				.doCallRealMethod()
				.doNothing()
				.when(this.admin)
				.createClass(Mockito.anyString(),
							 Mockito.anyInt(),
							 AdditionalMatchers.cmpEq("Prem"),
							 Mockito.anyInt());
			
		// Constraint 3 (no two classes with same name/year)
		// Execute this method 1 time (at most) for same classname/year,
		//		but do nothing on subsequent calls
		Mockito.doCallRealMethod()
			   .doNothing()
			   .when(this.admin)
			   .createClass(AdditionalMatchers.cmpEq("Software Tools"), 
					   		Mockito.eq(2017),
					   		Mockito.anyString(),
					   		Mockito.anyInt());
	}
	
	@Test
	public void testCreateClassCorrect() {
		this.courseManager.createClass("ECS161", 2017, "Instructor", 1);
		assertTrue(this.courseManager.classExists("ECS161", 2017));
	}
	
	@Test
	public void testCreateClassInPast() {
		this.courseManager.createClass("ECS161", 2016, "Instructor", 1);
		assertFalse(this.courseManager.classExists("ECS161", 2016));
	}
	
	@Test
	public void testCreateClassInFuture() {
		this.courseManager.createClass("ECS161", 2018, "Instructor", 1);
		Mockito.verify(this.admin, Mockito.never())
			   .createClass(Mockito.anyString(),
					   		Mockito.anyInt(),
					   		Mockito.anyString(),
					   		Mockito.anyInt());
	}
	
	// 1) capacity: both old and new constraint
	
	// 		A) Old constraint (capacity > 0)
	@Test
	public void testCreateClassAcceptedCapacity() {
		this.courseManager.createClass("ECS161", 2017, "Instructor", 15);
		assertTrue(this.courseManager.classExists("ECS161", 2017));
	}
	
	@Test
	public void testCreateClassZeroCapacity() {
		this.courseManager.createClass("ECS161", 2017, "Instructor", 0);
		assertFalse(this.courseManager.classExists("ECS161", 2017));
	}
	
	@Test
	public void testCreateClassNegCapacity() {
		this.courseManager.createClass("ECS161", 2017, "Instructor", -1);
		assertFalse(this.courseManager.classExists("ECS161", 2017));
	}
	
	//		B) New constraint (capacity < 1000)
	
	@Test
	public void testCreateClassThousandCapacity() {
		this.courseManager.createClass("ECS161", 2017, "Instructor", 1000);
		assertFalse(this.courseManager.classExists("ECS161", 2017));
	}
	
	@Test
	public void testCreateClassOverThousandCapacity() {
		this.courseManager.createClass("ECS161", 2017, "Instructor", 1001);
		assertFalse(this.courseManager.classExists("ECS161", 2017));
	}
	
	// 2) no more than two classes per instructor per year
	@Test
	public void testCreateClassTwoClasses() {
		this.courseManager.createClass("ECS161", 2017, "Prem", 15);
		this.courseManager.createClass("ECS160", 2017, "Prem", 15);

		// Test that createClass() was only called two times (at most)
		Mockito.verify(this.admin, Mockito.atMost(2))
			   .createClass(Mockito.anyString(), 
					   		Mockito.anyInt(),
					   		AdditionalMatchers.cmpEq("Prem"),
					   		Mockito.anyInt());
	}
	
	@Test
	public void testCreateClassMoreThanTwoClasses() {
		this.courseManager.createClass("ECS161", 2017, "Prem", 15);
		this.courseManager.createClass("ECS160", 2017, "Prem", 15);
		this.courseManager.createClass("ECS60", 2017, "Prem", 15);
		
		// Test that createClass() was only called two times (at most)
		Mockito.verify(this.admin, Mockito.atMost(2))
			   .createClass(Mockito.anyString(), 
					   		Mockito.anyInt(),
					   		AdditionalMatchers.cmpEq("Prem"),
					   		Mockito.anyInt());
	}
	
	// 3) no two classes with the same name/year
	@Test
	public void testCreateClassDuplicatePairs() {
		this.courseManager.createClass("Software Tools", 2017, "Instructor", 15);
		this.courseManager.createClass("Software Tools", 2017, "Instructor_2", 15);
		
		// Test that createClass() was only called once
		Mockito.verify(this.admin, Mockito.atMost(1))
			   .createClass(Mockito.anyString(),
					   		Mockito.anyInt(),
					   		Mockito.anyString(),
					   		Mockito.anyInt());
	}
}
