package com.myspringmvc.servlet.bean;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * 将请求参数封装成对应bean
 * @author huchao
 *
 */
public class BeanUtils {
	
	public static <T> T reqPassToBean(HttpServletRequest req,Class<T> clazz){
		T t = null;
		try {
			t = clazz.newInstance();
		} catch (Exception e1) {		
			e1.printStackTrace();
		} 
		
		Enumeration<String> enumeration = req.getParameterNames();
		while(enumeration.hasMoreElements()){
			String name = enumeration.nextElement();			
			if(name.equals("param")){
				continue;
			}
			try {
				String value = req.getParameter(name);				
				Field field= 	clazz.getDeclaredField(name);
				//判断请求参数类型
				Class<?> clazzTyp = field.getType();
				Object fileValue = null;
				if(clazzTyp==int.class){
					fileValue =  Integer.parseInt(value);
				}else if(clazzTyp==Integer.class){
					fileValue = Integer.valueOf(value);
				}else if(clazzTyp==String.class){
					fileValue = value;
				}else if(clazzTyp==Double.class){
					fileValue = Double.parseDouble(value);
				}else if(clazzTyp==Float.class){
					fileValue = Float.parseFloat(value);
				}else if(clazzTyp==Boolean.class){
					fileValue = Boolean.parseBoolean(value);
				}else if(clazzTyp==Date.class){
					fileValue = new SimpleDateFormat("yyyy-MM-dd").parse(value);
					
				}
				field.setAccessible(true);
				field.set(t, fileValue);
				
			} catch (Exception e) {
				System.out.print("请求参数封装错误");
			}
		}
		
		return  t;
		
	}

}
