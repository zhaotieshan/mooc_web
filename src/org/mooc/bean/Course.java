package org.mooc.bean;

import java.util.ArrayList;

/**
 * @author: wuke 
 * @date  : 2016年11月24日 下午5:29:46
 * Title  : Course
 * Description : 
 */
public class Course {
	private String courseId;
	private String couresName;
	private ArrayList<String> categoryList;
	
	public Course() {
	}
	
	public Course(String courseId, String couresName, ArrayList<String> categoryList) {
		this.courseId = courseId;
		this.couresName = couresName;
		this.categoryList = categoryList;
	}
	
	public String getCourseId() {
		return this.courseId;
	}
	public String getCouresName() {
		return this.couresName;
	}
	public ArrayList<String> getCategoryList() {
		return this.categoryList;
	}
}