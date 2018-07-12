package zd.spring.com.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
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
	 public static Object convertHttpServletRequestToSystemClassBean(HttpServletRequest request, Class<?> clazz)  {
	        // 获取request对象中所有表单元素名称
	        Enumeration<String> parameterNames = request.getParameterNames();
	        // 定义存储字段需要的值
	        Object fieldValue = null;

	        while (parameterNames.hasMoreElements()) {
	            String fieldName = parameterNames.nextElement();
	            // 定义获取到表单元素名称对应值
	            String value = request.getParameter(fieldName);

	            if (clazz == Integer.class) {
	                fieldValue = Integer.valueOf(value);
	            } else if (clazz == int.class) {
	                fieldValue = Integer.parseInt(value);
	            } else if (clazz == Double.class) {
	                fieldValue = Double.valueOf(value);
	            } else if (clazz == double.class) {
	                fieldValue = Double.parseDouble(value);
	            } else if (clazz == BigDecimal.class) {
	                fieldValue = new BigDecimal(value);
	            } else if (clazz == Date.class) {
	                try {
						fieldValue = new SimpleDateFormat("yyyy-MM-dd").parse(value);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }else if(clazz==Boolean.class){
	            	fieldValue = Boolean.parseBoolean(value);
				} else if (clazz == String.class) {
	                fieldValue = value;
	            }
	        }
	        return fieldValue;
	    }
	
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
			try {
				String value = req.getParameter(name);			
				Field field =null;
				Field[] fields =  clazz.getDeclaredFields();
				for(Field f: fields){
					if(f.getName().equals(name)) {
						field =f;
					}
				}
				//Field field= 	clazz.getDeclaredField(name);
				//判断请求参数类型
				if(field==null) continue;
				Class<?> clazzTyp = field.getType();
				Object fileValue = null;
				if(clazzTyp==int.class){
					fileValue =  Integer.parseInt(value);
				}else if(clazzTyp==Integer.class){
					fileValue = Integer.valueOf(value);
				}else if(clazzTyp==String.class){
					fileValue = value;
				}else if(clazzTyp==Double.class){
					fileValue = Double.valueOf(value);
				}else if(clazzTyp==double.class){
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
				e.printStackTrace();
			}
		}
		
		return  t;
		
	}

}
