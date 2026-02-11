package com.moayo.moayobackend.global.config;

import com.moayo.moayobackend.auth.service.JwtProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtProvider jwtProvider;

    public WebSocketConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 서버 → 클라이언트로 보내는 목적지 prefix
        config.enableSimpleBroker("/topic");
        // 클라이언트 → 서버로 보내는 목적지 prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("http://localhost:5173", "https://moayo-frontend-soeun0127s-projects.vercel.app")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) {
                    return message;
                }

                // CONNECT 때만 JWT 검사
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    // 헤더 없거나 형식 이상하면 => 이 세션은 거절
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return null; // preSend에서 null 리턴하면 메시지/연결이 버려짐
                    }

                    String token = authHeader.substring(7);

                    Long userId;
                    try {
                        userId = jwtProvider.extractUserId(token);
                    } catch (Exception e) {
                        // 토큰 파싱 실패해도 연결 거절
                        return null;
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    List.of() // 권한 필요하면 ROLE_USER 같은 거 넣어도 됨
                            );

                    // 이게 나중에 @MessageMapping 쪽 Authentication/Principal 로 들어감
                    accessor.setUser(authentication);
                }

                return message;
            }
        });
    }
}
