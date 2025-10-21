package com.example.air_resume_mate.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatLanguageModel chatModel;

    @Autowired
    public AIService(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getSimpleResponse(String prompt) {
        return chatModel.generate(prompt);
    }
}