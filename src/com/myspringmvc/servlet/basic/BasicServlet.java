package com.myspringmvc.servlet.basic;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myspringmvc.servlet.bean.BeanUtils;
import com.myspringmvc.servlet.tools.Tools;

/**
 * 全部servlet的父类
 * @author 胡超
 *
 */
public class BasicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String REDIRECT="redirect:";
	
	private static final String FORWARD="forward:";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		Object url = null;
		List<Object> param = new ArrayList<Object>();
		req.setCharacterEncoding("UTF-8");  //请求页面的编码
	    resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");	
		
	
		String menthod = req.getParameter("param");		
		
		Class<? extends BasicServlet> clazz = this.getClass();		
		Method mymethods = null; 		
		Method[] methods=  clazz.getDeclaredMethods();		
		if(methods.length==0) {			
			PrintWriter weiter = resp.getWriter();			
			weiter.print("请求路径不存在");			
			weiter.close();
		}		
		for(int j=0;j<methods.length;j++){			
			if(methods[j].getName().equals(menthod)){				
				mymethods = methods[j];				
				break;
			}			
		}		
		if(mymethods==null){			
			PrintWriter  writer = resp.getWriter();			
			writer.print("请求路径不存在");			
			writer.close();
			return;
		}
		Class<?>[] clazzs =  mymethods.getParameterTypes();
		
		for(Class<?> v: clazzs){
			if(!v.isInterface()){
				param.add(BeanUtils.reqPassToBean(req, v));
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
		if(param.isEmpty()){
			try {
				 url =  mymethods.invoke(this);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				 url =  mymethods.invoke(this,param.toArray());
				
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
			
			System.out.println();
		}
		//转发页面
	
	}
     
   
}
