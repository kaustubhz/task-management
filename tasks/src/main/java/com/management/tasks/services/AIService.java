package com.management.tasks.services;

import com.management.tasks.dto.request.TaskRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@ConditionalOnProperty(name = "features.ai-enabled", havingValue = "true")
public class AIService {

	private final ChatClient chatClient;

	public AIService(ChatClient.Builder chatClientBuilder) {
		String currentDateTimeWithZone = ZonedDateTime.now().toString();
		this.chatClient = chatClientBuilder.defaultSystem(String.format("""
						You are a productivity assistant.
						Your job is to extract task details from the user's natural language text.
						CRITICAL INSTRUCTIONS:
						1. Analyze the user text and extract the title, description, priority, and due date.
						2. Fix ALL typos, spelling mistakes, and grammatical errors in the extracted title and description.
						3. Improve the vocabulary so the task sounds professional and clear.
						4. ALL relative dates MUST be calculated based on the current date and time.
						The current date, time, and timezone is exactly: %s
						5. Output the dueDate strictly in ISO-8601 format WITH timezone offset (e.g. "2026-04-19T17:00:00+05:30").
						 Never use Z or UTC unless the user specifically says UTC.
						6. Return a valid JSON object matching the requested schema. ONLY output valid JSON.
						""", currentDateTimeWithZone))
				.build();
	}

	public TaskRequest extractTaskFromText(String chatText) {
		// Spring AI makes structured extraction incredibly easy!
		return chatClient.prompt()
				.user(chatText)
				.call()
				.entity(TaskRequest.class);// This tells Spring AI to parse the LLM output directly into your TaskRequest DTO!
	}

}
