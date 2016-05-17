package com.pfs.automation;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.runner.JUnitCore;
import org.reflections.Reflections;

public class Tester {

	public static void main(String... args) {
		addJarToClasspath(args[0]);
		List<String> stories = getStories();
		System.out.println(stories.size());
		JUnitCore.main(stories.toArray(new String[stories.size()]));
	}
	
	private static void addJarToClasspath(String path) {
		try {
			File file = new File(path);
			URL url = file.toURI().toURL();

			URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			method.setAccessible(true);
			method.invoke(classLoader, url);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	private static List<String> getStories() {
		Reflections reflections = new Reflections("com.pfs");
		return reflections.getSubTypesOf(AbstractSpringJBehaveStory.class) //
				.stream().map(Class::getName).collect(Collectors.toList());
	}
}
