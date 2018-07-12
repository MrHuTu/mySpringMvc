package zd.spring.com.util;

import zd.spring.com.annotation.MyController;
import zd.spring.com.annotation.MyRepository;
import zd.spring.com.annotation.MyService;

public class ReflectUtil {
	
	private static String getLowerAlias(Class<?> clazz){
		String className= clazz.getSimpleName();
		String start =className.substring(0, 1).toLowerCase();
		String end = className.substring(1);
		return start+end;
		
	}
	public static String  getAnnotationAlias(Class<?> clazz){
		String aliasName = getLowerAlias(clazz);
		MyRepository myRepository = clazz.getAnnotation(MyRepository.class);
		MyController  myController=clazz.getAnnotation(MyController.class);
		MyService  mkyService=clazz.getAnnotation(MyService.class);
		if(myRepository!=null && !myRepository.value().trim().equals("")){
			
			aliasName =  myRepository.value();
		}else if(myController!=null && !myController.value().trim().equals("") ){
					
			aliasName =  myController.value();
		}else if(mkyService!=null && !mkyService.value().trim().equals("")){
			
			aliasName =  mkyService.value();
		}
		return aliasName;
	}

}
