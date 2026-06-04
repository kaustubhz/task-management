package com.management.tasks.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

@Service
public class AIChatService {
	private final ChatClient chatClient;

	public AIChatService(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder
				.defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
				.build();
	}


}
