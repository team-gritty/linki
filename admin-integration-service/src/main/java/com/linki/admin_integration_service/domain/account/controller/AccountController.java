//package com.linki.admin_integration_service.domain.account.controller;
//
//import com.linki.admin_integration_service.domain.account.dto.JoinDTO;
//import com.linki.admin_integration_service.domain.account.dto.RequestJoinDTO;
//import com.linki.admin_integration_service.domain.account.service.AccountService;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("v1/api")
//
//public class AccountController {
//    private final AccountService accountService;
//
//    @PostMapping("signup")
//    public ResponseEntity<?> signUp(@RequestBody RequestJoinDTO requestJoinDTO){
//
//        JoinDTO joinDTo = new ModelMapper().map(requestJoinDTO, JoinDTO.class);
//
//        if(!StringUtils.hasLength(joinDTo.getAdminLoginId()) || !StringUtils.hasLength(joinDTo.getAdminLoginPw()) || !StringUtils.hasLength(joinDTo.getAdminName())
//        || !StringUtils.hasLength(joinDTo.get))
//    }
//
//}
