package com.management.tasks.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class AIChatService {
	private final ChatClient chatClient;

	public AIChatService(ChatClient.Builder chatClientBuilder) {
		ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
		this.chatClient = chatClientBuilder
				.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
				.build();
	}

}
