package com.iyd.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iyd.config.AiApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiApiClient {
    private final AiApiProperties properties;
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();
    private boolean available = false;

    @PostConstruct
    public void init() {
        String key = properties.getLlm().getKey();
        available = key != null && !key.isEmpty() && !"demo-key".equals(key) && !key.startsWith("your-api");
        log.info("AI API ready: {} | model: {}", available, properties.getLlm().getModel());
    }

    public boolean isAvailable() { return available; }

    public String chat(String systemPrompt, String userMessage) {
        if (!available) return null;
        try {
            ObjectNode body = mapper.createObjectNode();
            body.put("model", properties.getLlm().getModel());
            body.put("temperature", 0.8); body.put("max_tokens", 2048);
            ArrayNode msgs = mapper.createArrayNode();
            ObjectNode s = mapper.createObjectNode(); s.put("role","system"); s.put("content",systemPrompt); msgs.add(s);
            ObjectNode u = mapper.createObjectNode(); u.put("role","user"); u.put("content",userMessage); msgs.add(u);
            body.set("messages", msgs);
            HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON); h.setBearerAuth(properties.getLlm().getKey());
            ResponseEntity<String> r = restTemplate.postForEntity(properties.getLlm().getUrl(), new HttpEntity<>(mapper.writeValueAsString(body), h), String.class);
            if (r.getStatusCode()==HttpStatus.OK && r.getBody()!=null) {
                JsonNode root = mapper.readTree(r.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size()>0) {
                    String c = choices.get(0).path("message").path("content").asText();
                    if (c!=null && !c.isEmpty()) { log.info("AI generated {} chars", c.length()); return c; }
                }
            }
        } catch(Exception e) { log.warn("AI API error: {}", e.getMessage()); }
        return null;
    }
}
