package com.doom.mvc.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.doom.mvc.annotation.Handler;
import com.doom.mvc.annotation.Response;
import com.doom.mvc.utils.ClassUtils;


/**
 * @author yuzhang <z99370324@gmail.com>
 * 
 */

public class DispatcherServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private HttpSession session;

    private Map<String, Method> requestMap = new HashMap<String, Method>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        Set<Class<?>> classSet = ClassUtils.getClasses("com");
        for (Class<?> clazz : classSet) {
            Handler handlerAnnotation = clazz.getAnnotation(Handler.class);
            if (handlerAnnotation != null) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    Response responseAnnotation = method.getAnnotation(Response.class);
                    if (responseAnnotation != null) {
                        String path = handlerAnnotation.path() + responseAnnotation.path();
                        if (requestMap.containsKey(path))
                            throw new NullPointerException("identical url detected between "
                                    + clazz.getName() + " and "
                                    + requestMap.get(path).getDeclaringClass().getName());
                        requestMap.put(path, method);
                    }
                }
            }
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
        Method realHandlerMethod = requestMap.get(request.getRequestURI());
        Class<?> realHandler = realHandlerMethod.getDeclaringClass();
        Class<?>[] paramsType = realHandlerMethod.getParameterTypes();
        // Proxy.newProxyInstance(getClass().getClassLoader(), realHandler.getInterfaces(), this);
        try {
            realHandlerMethod.invoke(realHandler);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
