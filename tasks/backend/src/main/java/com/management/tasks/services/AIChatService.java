package com.management.tasks.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.stereotype.Service;

@Service
public class AIChatService {
	private final ChatClient chatClient;

	public AIChatService(ChatClient.Builder chatClientBuilder) {
		SessionService sessionService = DefaultSessionService.builder()
				.sessionRepository(InMemorySessionRepository.builder().build())
				.build();
		this.chatClient = chatClientBuilder
				.defaultAdvisors(SessionAdvisors.builder(sessionService).build())
				.build();
	}

}
