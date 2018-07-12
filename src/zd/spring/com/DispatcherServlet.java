package zd.spring.com;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import zd.spring.com.annotation.MyAutowired;
import zd.spring.com.annotation.MyController;
import zd.spring.com.annotation.MyRepository;
import zd.spring.com.annotation.MyRequestMapping;
import zd.spring.com.annotation.MyService;
import zd.spring.com.util.BeanUtils;
import zd.spring.com.util.ClassUtils;
import zd.spring.com.util.ReflectUtil;
import zd.spring.com.util.Tools;
import zd.spring.com.xml.XMlConfigurationParser;


/**
 * Servlet implementation class DispatcherServlet
 */

public class DispatcherServlet extends HttpServlet {
	private static final String REDIRECT="redirect:";
	
	private static final String FORWARD="forward:";
	private String contextConfigLocation;
	private static final long serialVersionUID = 1L;
	private List<Class<?>> classList =   Collections.synchronizedList(new ArrayList<Class<?>>());
	private Map<String,Object> contextContainer = Collections.synchronizedMap(new HashMap<String, Object>());
	private Map<String,Method> urlMaping = Collections.synchronizedMap(new HashMap<String, Method>());
   public DispatcherServlet() {
	   System.out.println(DispatcherServlet.class+"实例化");
   }
   /**
    * 初始化MySpring容器
    */
	@Override
	public void init() throws ServletException {
		this.contextConfigLocation =this.getInitParameter("contextConfigLocation").replace("classpath:", "");
		String basePackage = XMlConfigurationParser.readXMLBasePackage(contextConfigLocation);
		scanBasePackfeAnnotationClass(basePackage);
		doIoc();
		doDi();
		urlMapingToMethod();
		
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		Object url = null;
		List<Object> param = new ArrayList<Object>();
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=UTF-8");
		String reqMaping= req.getRequestURI().replace(req.getContextPath(), "");
		
		Method  method= urlMaping.get(reqMaping);
		
		if(method!=null){
			Class<?>[] clazzs =  method.getParameterTypes();
			
			for(Class<?> v: clazzs){
				if(!v.isInterface()){
					if(ClassUtils.isSystemClass(v)){
						param.add(BeanUtils.convertHttpServletRequestToSystemClassBean(req, v));					
					}else{
						param.add(BeanUtils.reqPassToBean(req, v));
					}
					
				}else{
					if(v==HttpServletRequest.class){
						param.add(req);
					}else if(v==HttpServletResponse.class){
						param.add(resp);
					}else if(v==HttpSession.class){
						param.add(req.getSession());
					}else if(v== ServletContext.class){
						param.add(req.getServletContext());
					}
				}
				
				
			}
			Class<?>  clazz= method.getDeclaringClass();
			
			Object  tageObje= contextContainer.get(ReflectUtil.getAnnotationAlias(clazz));
			if(param.isEmpty()){
				try {
					 url =  method.invoke(tageObje);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					 url =  method.invoke(tageObje,param.toArray());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(url!=null){
			String urlString = 	url.toString();		
				if(urlString.startsWith(REDIRECT)){
				resp.sendRedirect(req.getContextPath()+"/"+Tools.GetUrl(REDIRECT, urlString));				
				}else if(urlString.equals(FORWARD)){				
					req.getRequestDispatcher(Tools.GetUrl(FORWARD, urlString)).forward(req, resp);
				}else{
					req.getRequestDispatcher(urlString).forward(req, resp);
				}
				
			
			}
			//转发页面
		
		}else{
			req.getRequestDispatcher("/WEB-INF/html/404.jsp").forward(req, resp);
			/*PrintWriter writer= 	resp.getWriter();
			writer.print("请求不存在");
			writer.close();*/
		}
	
	}
	
	private void urlMapingToMethod() {
		if(classList.size()==0){
			return;
		}else{
			Iterator<Class<?>> iter = classList.iterator();
			while(iter.hasNext()){
				Class<?> clazz = iter.next();
				if(clazz.isAnnotationPresent(MyController.class)){
					String baseMapingUrl = null;
					MyRequestMapping  myRequestMapping = clazz.getAnnotation(MyRequestMapping.class);
					if(myRequestMapping!=null){
						baseMapingUrl = myRequestMapping.value();
						//System.out.println("baseMapingUrl"+baseMapingUrl);
					}
					Method[] method = clazz.getMethods();
					String url = null;
					for(Method m: method){
						if(m.getModifiers()==Modifier.PUBLIC && m.isAnnotationPresent(MyRequestMapping.class)){
							MyRequestMapping myMethodtMapping = m.getAnnotation(MyRequestMapping.class);
							url = handldUrl(baseMapingUrl)+handldUrl(myMethodtMapping.value());
							
							urlMaping.put(url, m);
						}
						
					}
				}
			}
		}
		
	}
	private String handldUrl(String baseMapingUrl) {
		String  newUrl = baseMapingUrl.replaceAll("/", "");
		return "/"+newUrl;
	}
	/**
	 * 依赖注入
	 */
	private void doDi() {
		if (!contextContainer.isEmpty()) {

			Iterator<String> keySet = contextContainer.keySet().iterator();
			while (keySet.hasNext()) {
				String key = keySet.next();
				Object obj = contextContainer.get(key);
				Class<?> clazz = obj.getClass();
				Field[] fieldClazz = clazz.getDeclaredFields();
				Object value = null;
				for (Field f : fieldClazz) {
					MyAutowired myAutowired = f
							.getAnnotation(MyAutowired.class);
					if (myAutowired != null) {
						if (!myAutowired.value().equals("")) {
							// 按bean名称装配
							value = contextContainer.get(myAutowired.value());
						} else {
							// 按类型装配
							Class<?> fClazz = f.getType();
							Collection<Object> collection = contextContainer
									.values();
							Iterator<Object> iter = collection.iterator();
							while (iter.hasNext()) {
								Object obj1 = iter.next();

								if (fClazz.isAssignableFrom(obj1.getClass())) {
									value = obj1;
									break;
								}
							}
							// value = contextContainer.get(f.getName());

						}
						f.setAccessible(true);
						try {
							if (value != null) {
								f.set(obj, value);
							} else {
								System.out.println(obj + "下的" + f.getName()
										+ "注入失败");
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}
			}
		} else {
			return;
		}
	}
	/**
	 * 控制反转
	 */
	private void doIoc() {
		if(classList.size()==0){
			return;
			
		}
		for(int i=0;i<classList.size();i++){
			
			Class<?> clazz = classList.get(i);
			
			String beanName = ReflectUtil.getAnnotationAlias(clazz);
			
			
			try {							
				contextContainer.put(beanName, clazz.newInstance());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 扫描
	 * @param basePackage
	 */
	private void scanBasePackfeAnnotationClass(String basePackage) {
		// TODO Auto-generated method stub
		URL url = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
		File file = null;
		try {
			 file = new File(url.toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory()){
					//System.out.println(basePackage+"."+pathname.getName());
					scanBasePackfeAnnotationClass(basePackage+"."+pathname.getName());
				}else{
					
					if(pathname.getName().endsWith(".class")){
						String className = pathname.getName().replace(".class", "");
						try {
							Class<?> clazz= Class.forName(basePackage+"."+className);
							if( clazz.isAnnotationPresent(MyController.class)
									|| clazz.isAnnotationPresent(MyService.class)
									||clazz.isAnnotationPresent(MyRepository.class)){
								
								classList.add(clazz);
							}
						} catch (ClassNotFoundException  e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
				}
				return false;
			}
		});
		//System.out.println(url);
	}
   public  Object getBean(String beanName){
	return contextContainer.get(beanName);
	   
   }
	
}
