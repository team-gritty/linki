//package com.linki.admin_integration_service.config;
//
//import com.Gritty.Linki.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
////매니저에서 가져다 쓸 객체 (회원객체)
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUserLoginId(username).orElseThrow(() ->
//                new UsernameNotFoundException(username+"는 없습니다."));
//
//        return new CustomUserDetails(user.getUserId(),user.getUserLoginId(),user.getUserLoginPw(),user.getUserRole());
//    }
//}
