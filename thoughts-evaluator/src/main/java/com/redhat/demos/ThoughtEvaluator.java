package com.redhat.demos;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface ThoughtEvaluator {

    @SystemMessage("""
            You are a content moderator. You evaluate thoughts and label each one as either APPROVED or REJECTED.
            A thought should be REJECTED only if it contains offensive, harmful, or inappropriate content.
            Otherwise it should be APPROVED.
            Respond with exactly one word: APPROVED or REJECTED.
            """)
    String evaluate(@UserMessage String thought);
}
