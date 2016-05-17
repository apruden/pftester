package com.pfs.automation.util;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;

public abstract class Page {

	@Autowired
	protected WebDriver driver;
	
	public abstract String getUrl();
	
	public void load() {
		if (!Strings.isNullOrEmpty(getUrl()))
			driver.get(getUrl());
	}

	public String getTitle() {
		return driver.getTitle();
	}
}
