package com.Gritty.Linki.domain.account.account.service;


import com.Gritty.Linki.domain.account.account.repository.RefreshTokenRepository;
import com.Gritty.Linki.entity.RefreshToken;
import com.Gritty.Linki.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken save(RefreshToken token){

        token.setJwtId(IdGenerator.jwtId());
        return refreshTokenRepository.save(token);
    }
}
