package com.myspringmvc.servlet.tools;

public class Tools {
	private static final String REDIRECT="redirect:";
	
	private static final String FORWARD="forward:";
	public static String GetUrl(String prefix,String strValue){
		if(prefix.equals(REDIRECT)){
			String newString  = strValue.replace(REDIRECT, "");
			
			String star = newString.substring(0, newString.lastIndexOf("/"));
			
			String end = newString.substring(newString.lastIndexOf("/")+1, newString.length());
			
			return star+"?param="+end;
		}else if(prefix.equals(FORWARD)){
			String newString  = strValue.replace(REDIRECT, "");
			
			String star = newString.substring(0, newString.lastIndexOf("/"));
			
			String end = newString.substring(newString.lastIndexOf("/")+1, newString.length());
			
			return star+"?param="+end;
		}else{
			 String newString = strValue.replace(prefix, "");
	         return newString;
		}
		
		
		
		
							
	}
	

}
