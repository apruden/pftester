package com.pfs.automation;

import javax.annotation.PostConstruct;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.pfs.automation.util.SeleniumTestExecutionListener;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AutomationConfiguration.class)
@TestExecutionListeners({ SeleniumTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public abstract class AbstractSpringJBehaveStory extends JUnitStory {

	@Autowired
	Embedder embedder;

	@PostConstruct
	protected void init() {
		useEmbedder(embedder);
	}

	@Override
	public Configuration configuration() {
		return embedder.configuration();
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		return embedder.stepsFactory();
	}
}
