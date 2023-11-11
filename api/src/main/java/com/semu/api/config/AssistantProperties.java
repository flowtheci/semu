package com.semu.api.config;

import com.semu.api.model.Assistants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class AssistantProperties {

    @Value("${chatgpt.assistant.math}")
    private String math;

    @Value("${chatgpt.assistant.math.vision}")
    private String mathVision;

    @Value("${chatgpt.assistant.title}")
    private String title;

    @Value("${chatgpt.assistant.estonian}")
    private String estonian;

    // standard getters and setters

    public String math() {
        return math;
    }

    public String mathVision() {
        return mathVision;
    }

    public String title() {
        return title;
    }

    public String estonian() {
        return estonian;
    }

    public String get(Assistants prompt) {
        return switch (prompt) {
            case MathAssistant -> math;
            case MathVisionAssistant -> mathVision;
            case TitleAssistant -> title;
            case EstonianAssistant -> estonian;
        };
    }
}
