package cn.ijustone.crm.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.ijustone.crm.service.IEmployeeService;

public class AuthInterceptor implements HandlerInterceptor{

		@Autowired
		private IEmployeeService employeeService;
		/**
		 * 调用控制器前调用，返回true：放行，返回false：拦截
		 */
	/*	@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			
			if(handler instanceof HandlerMethod){
				
				Employee user = UserContext.getSessionUser();
				if(null==user){
					response.sendRedirect("/login.jsp");
					return false;//拦截
				}
//				获取控制器对象
				HandlerMethod handlerMethod = (HandlerMethod)handler;
				String clsName = handlerMethod.getBeanType().getName();
				String methodName = handlerMethod.getMethod().getName();
				String clsMethod = clsName+"."+methodName;
				String allClsMethod = clsName+".ALL";
				System.out.println("clsMethod="+clsMethod);
				System.out.println("allClsMethod="+allClsMethod);
				//获取所有需拦截的资源列表List<String>
				List<String> allResources = employeeService.getResources();
				//获取用户拥有的资源列表List<String>
				List<String> myResources = employeeService.getUserResources(UserContext.getSessionUser().getId());
				System.out.println(allResources.size());
				System.out.println(myResources.size());
				//如果当前请求资源需要进行拦截
				if(allResources.contains(clsMethod) || allResources.contains(allClsMethod)){
					System.out.println("需要拦截...");
					if(myResources.contains(clsMethod) || myResources.contains(allClsMethod)){
						System.out.println("有权限访问");
					}else{
						System.out.println("无权限访问");
						
						String requestType = request.getHeader("X-Requested-With");
						if("XMLHttpRequest".equals(requestType)){//AJAX请求
							response.setContentType("text/plain;charset=utf-8");
							response.getWriter().write("{\"success\":false,\"msg\":\"无访问权限\"}");
						}else{
							response.sendRedirect("/auth.jsp");
						}
						return false;//拦截
					}
				}else{
					System.out.println("不需要拦截");
				}
			}
			
			return true;
		}
*/
		/**
		 * 调用控制器方法后（生成视图之前）
		 */
		@Override
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
				ModelAndView modelAndView) throws Exception {
			
		}

		/**
		 * 视图生成后（后台所有逻辑都完成后）
		 */
		@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
				throws Exception {
		}

		@Override
		public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
			// TODO Auto-generated method stub
			return false;
		}



}
