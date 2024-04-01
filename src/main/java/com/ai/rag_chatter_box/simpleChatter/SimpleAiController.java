package com.ai.rag_chatter_box.simpleChatter;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/simple-chat")
public class SimpleAiController {

    private final ChatClient chatClient;

    public SimpleAiController(ChatClient chatClient){
        this.chatClient= chatClient;
    }

    @GetMapping("/tell-joke")
    public String getBadJoke(@RequestParam(value = "topic", defaultValue = "dad")String topic){

        PromptTemplate promptTemplate = new PromptTemplate("Tell me a bad joke on {topic}, if it sensitive just say shut up!");
        Prompt prompt = promptTemplate.create(Map.of("topic",topic));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
