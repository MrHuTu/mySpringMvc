package com.myspringmvc.servlet.service;

import java.io.PrintStream;

import zd.spring.com.annotation.MyService;

@MyService
public class MyPrint {
	
	public PrintStream geiPrint(){
		
		return System.out;
	}

}
