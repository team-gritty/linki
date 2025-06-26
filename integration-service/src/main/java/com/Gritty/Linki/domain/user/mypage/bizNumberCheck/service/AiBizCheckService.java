package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.service;

import java.io.IOException;

public interface AiBizCheckService {

    String check(String openAiJson,String msg) throws IOException;
}
