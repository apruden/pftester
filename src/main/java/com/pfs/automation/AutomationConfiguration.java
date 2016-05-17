package com.pfs.automation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.io.StoryPathResolver;
import org.jbehave.core.io.UnderscoredCamelCaseResolver;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.pfs.automation.util.TestScope;

import org.jbehave.core.reporters.FilePrintStreamFactory.ResolveToPackagedName;

import static org.jbehave.core.reporters.Format.*;

@Configuration
@ComponentScan(basePackages = "com.pfs")
@EnableJpaRepositories(basePackages = "com.pfs")
public class AutomationConfiguration {

	@Autowired
	ApplicationContext applicationContext;

	@Bean
	public static TestScope testScope() {
		return new TestScope();
	}

	@Bean
	public static CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("test", testScope());
		scopeConfigurer.setScopes(scopes);
		return scopeConfigurer;
	}

	@Bean
	@Scope("test")
	public static WebDriver webDriver() {
		return new ChromeDriver();
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getLocalEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPersistenceUnitName("pfs");
		// NOTICE: alternatively create HibernateJpaVendorAdapter manually to scan package and avoid using persistence.xml
		// See: https://github.com/spring-projects/spring-data-examples/tree/master/jpa/multiple-datasources 

		return emf;
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(getLocalEntityManagerFactory().getObject());

		return txManager;
	}

	@Bean
	public Embedder getEmbedder() {
		Embedder embedder = new Embedder();
		embedder.useEmbedderControls(embedderControls());
		embedder.useMetaFilters(Arrays.asList("-skip"));
		embedder.useStepsFactory(new SpringStepsFactory(configuration(), applicationContext));
		embedder.useEmbedderControls(embedderControls());
		embedder.useConfiguration(configuration());

		return embedder;
	}

	private org.jbehave.core.configuration.Configuration configuration() {
		return new MostUsefulConfiguration().useStoryPathResolver(storyPathResolver()).useStoryLoader(storyLoader())
				.useStoryReporterBuilder(storyReporterBuilder()).useParameterControls(parameterControls());
	}

	private EmbedderControls embedderControls() {
		return new EmbedderControls().doIgnoreFailureInView(true);
	}

	private ParameterControls parameterControls() {
		return new ParameterControls().useDelimiterNamedParameters(true);
	}

	private StoryPathResolver storyPathResolver() {
		return new UnderscoredCamelCaseResolver();
	}

	private StoryLoader storyLoader() {
		return new LoadFromClasspath();
	}

	private StoryReporterBuilder storyReporterBuilder() {
		return new StoryReporterBuilder()
				.withCodeLocation(CodeLocations.codeLocationFromClass(AbstractSpringJBehaveStory.class))
				.withPathResolver(new ResolveToPackagedName()).withFailureTrace(true).withDefaultFormats()
				.withFormats(IDE_CONSOLE, TXT, HTML);
	}
}
