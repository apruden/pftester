package com.pfs.automation.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class PageBeanPostProcessor implements BeanPostProcessor {
	private static final Logger log = LoggerFactory.getLogger(PageBeanPostProcessor.class.getName());

	@Autowired
	private WebDriver driver;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean.getClass().isAnnotationPresent(PageObject.class)) {
			log.trace("post processing page bean after init: applying PageFactory.initElements");
			((Page)bean).load();
			PageFactory.initElements(driver, bean);
		}
		
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
}
