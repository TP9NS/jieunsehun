package com.example.ourapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class MessagingConfig {

    @Bean
    @Primary
    public SimpMessagingTemplate simpMessagingTemplate(SubscribableChannel clientOutboundChannel) {
        return new SimpMessagingTemplate(clientOutboundChannel);
    }
}
