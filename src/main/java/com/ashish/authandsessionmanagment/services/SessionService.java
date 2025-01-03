package com.ashish.authandsessionmanagment.services;

import com.ashish.authandsessionmanagment.entities.SessionEntity;
import com.ashish.authandsessionmanagment.entities.UserEntity;
import com.ashish.authandsessionmanagment.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    @Value("${session.total.limit}")
    private String TOTAL_SESSION_LIMIT;    // we can change this on subscription basis as we need
    private final SessionRepository sessionRepository;

  public boolean generateNewSession(UserEntity userEntity, String refreshToken) {
        List<SessionEntity> sessions = sessionRepository.findByUser(userEntity);

        if (sessions.size() >= Integer.parseInt(TOTAL_SESSION_LIMIT)) {
            // Find the oldest session and delete it if it exists
            sessions.stream()
                    .min(Comparator.comparing(SessionEntity::getLastUsedAt))
                    .ifPresent(sessionRepository::delete);
        }

        // Create and save the new session
        SessionEntity sessionEntity = SessionEntity.builder()
                .user(userEntity)
                .refreshToken(refreshToken)
                .build();
        sessionRepository.save(sessionEntity);

        return true;
    }



    public boolean validateSession(String refreshToken) {
        SessionEntity session = sessionRepository.findByRefreshToken(refreshToken).orElse(null);
        if (session == null) return false;
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
        return true;
    }

    public boolean invalidateSession(String refreshToken) {
        SessionEntity session = sessionRepository.findByRefreshToken(refreshToken).orElse(null);
        if (session == null) return false;
        sessionRepository.delete(session);
        return true;
    }
}
