package org.mooc.bean;

import java.util.TreeSet;

/**
 * @author: wuke 
 * @date  : 2016年12月12日 下午9:14:18
 * Title  : UserLearnedCourses
 * Description : User's id and the set of his/her learned courses.
 */
public class UserLearnedCourses {
	String userId;
	TreeSet<String> coursesSet; // the elements are distinct and sorted
	
	public UserLearnedCourses() {
	}
	
	public UserLearnedCourses(String userId, TreeSet<String> coursesSet) {
		this.userId = userId;
		this.coursesSet = coursesSet;
	}
	
	public String getUserId() {
		return userId;
	}
	public TreeSet<String> getCoursesSet() {
		return coursesSet;
	}
}