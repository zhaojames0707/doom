package com.doom.mvc.annotation;
/**
 * @author yuzhang <z99370324@gmail.com>
 * 
 */

public @interface Response {
    
    String path();
    
    Method method() default Method.GET;
    
    
}
