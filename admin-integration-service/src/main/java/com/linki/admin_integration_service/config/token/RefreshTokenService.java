package com.linki.admin_integration_service.config.token;

import com.linki.admin_integration_service.entity.RefreshToken;
import com.linki.admin_integration_service.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Ref;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken save(RefreshToken refreshToken){
        refreshToken.setJwtId(IdGenerator.jwtId());
        return refreshTokenRepository.save(refreshToken);
    }


}
