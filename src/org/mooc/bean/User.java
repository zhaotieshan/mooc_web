package org.mooc.bean;
/**
 * @author: wuke 
 * @date  : 2016年12月4日 下午8:24:12
 * Title  : User
 * Description : 
 */
public class User {
	private String userId;
	private String nickName;
	private String email;
	private String source;
	private String sex;
	private int age;
	private String university;
	private String major;
	
	public User() {		
	}
	
	public User(String userId, String nickName, String email, String source, 
			String sex, int age, String university, String major) {
		this.userId = userId;
		this.nickName = nickName;
		this.email = email;
		this.source = source;
		this.sex = sex;
		this.age = age;
		this.university = university;
		this.major = major;
	}
	
	public String getUserID() {
		return userId;
	}
	public String getNickName() {
		return nickName;
	}
	public String getEmail() {
		return email;
	}
	public String getSource() {
		return source;
	}
	public String getSex() {
		return sex;
	}
	public int getAge() {
		return age;
	}
	public String getUniversity() {
		return university;
	}
	public String getMajor() {
		return major;
	}
}
