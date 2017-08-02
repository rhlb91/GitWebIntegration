package com.teammerge.cronjob;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {
 
    // Maintain reference to Spring's Application Context
    private static ApplicationContext context;
 
    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        this.context = context;
    }
 
    // Make constructor private, so that the class can not be instantiated
    private ApplicationContextUtils() {
    }
 
    /**
     * Get Spring Managed bean from Non Spring Managed (outside of spring) classes
     * 
     * @param type, Class of Bean whose instance is required
     * @return Spring managed bean
     */
    public static <T> T getBean(Class<T> type) {
        return (T) context.getBean(type);
    }
}