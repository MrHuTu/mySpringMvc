package com.myspringmvc.servlet.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import zd.spring.com.annotation.MyAutowired;
import zd.spring.com.annotation.MyController;
import zd.spring.com.annotation.MyRequestMapping;

import com.myspringmvc.servlet.entity.Cat;
import com.myspringmvc.servlet.service.MyPrint;

@MyController
@MyRequestMapping("/my")
public class IndexServlet/* extends BasicServlet*/{
	
	@MyAutowired
	public MyPrint myPrint;
	
	/**
	 * 
	 */
	/*private static final long serialVersionUID = 1L;*/
	@MyRequestMapping("/hello")
	public void hello(HttpServletRequest req, HttpServletResponse resp,Cat cat,String h){
		

		PrintWriter writer = null;
		try {
			 writer = 	resp.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>登入</title></head><body>");
		writer.print("姓名:"+cat.getName()+"</br>");
		writer.print("性别:"+cat.getSex()+"</br>");
		writer.print("年龄:"+cat.getAge()+"</br>");
		writer.print("h:"+h+"</br>");
		writer.print("</body></html>");
		
		writer.close();
	}
	@MyRequestMapping("/word")
	public String word(){
		
		return "hello.jsp";
		
	}
	
	
}
