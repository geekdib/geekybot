package com.ittu;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ittu.ai.Chatbot;
import com.ittu.voice.TextSpeech;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

@SpringBootApplication(exclude = {GroovyTemplateAutoConfiguration.class})
@EnableWebMvc
@EnableAsync
public class IttuAiApplication implements WebMvcConfigurer {

	@Autowired
	Chatbot chatbot;
	
	@Autowired
	TextSpeech textSpeech;

	public static void main(String[] args) {
		SpringApplication.run(IttuAiApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
	@Bean
	public void initBean() {
		CompletableFuture.runAsync(() -> {
		init();
		});
	}
	
	public void init() {
		Configuration config = new Configuration();
		config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		config.setDictionaryPath("src/main/resources/1107.dic");
		config.setLanguageModelPath("src/main/resources/1107.lm");
		try {
			LiveSpeechRecognizer speech = new LiveSpeechRecognizer(config);
			speech.startRecognition(true);

			SpeechResult speechResult = null;

			while ((speechResult = speech.getResult()) != null) {
				String voiceCommand = speechResult.getHypothesis();
				System.err.println("Voice Command is " + voiceCommand);
					try {
						String response = chatbot.askIttu(voiceCommand);
							if(!response.equalsIgnoreCase("")) {
								textSpeech.speak(response);
							}else {
								textSpeech.speak("Hmm");
							}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} 
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
