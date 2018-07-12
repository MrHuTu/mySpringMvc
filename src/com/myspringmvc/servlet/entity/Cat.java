package com.myspringmvc.servlet.entity;

import zd.spring.com.annotation.MyRepository;

/**
 * 实体类
 * @author 胡超
 *
 */
@MyRepository
public class Cat {
	String name;
	String sex;
	int age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	

}
