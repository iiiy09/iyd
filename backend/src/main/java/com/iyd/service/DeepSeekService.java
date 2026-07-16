package com.iyd.service;

import java.util.List;
import java.util.Map;

public interface DeepSeekService {
    String chat(String systemPrompt, String userMessage);
    String chatWithHistory(List<Map<String, String>> messages);
}
