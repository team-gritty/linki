package com.linki.admin_integration_service.domain.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.LLMDTO;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.DashBoardSummaryMapper;
import com.linki.admin_integration_service.util.GptClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class DashBoardLLMServiceImpl implements DashBoardLLMService{


    private static String DEFAULT_MSG = "Hello World";
    private final GptClient gptClient;

    private final DashBoardSummaryMapper dashBoardSummaryMapper;

    private static String getDefaultMsg() {
        return DEFAULT_MSG;
    }

    private static void setDefaultMsg(String msg) {
        DEFAULT_MSG = msg;
    }

    @Override
    public LLMDTO getDashboardLLM() {
        LLMDTO llmDTO = new LLMDTO();
        llmDTO.setMsg(DEFAULT_MSG);
        return llmDTO;
    }

    @PostConstruct
    public void init() {
        updateDashboardLLM();
    }

    @Scheduled(fixedRate = 600000)
    public void updateDashboardLLM() {


        StringBuilder msg = new StringBuilder();

        List<CampaignDTO> campaignDTOs =
                dashBoardSummaryMapper.getCampaigns().stream()
                        .filter(dto -> dto.getApplyDeadline().isAfter(LocalDate.now()))
                        .toList();

        for(CampaignDTO campaignDTO : campaignDTOs) {
            msg.append("- ");
            msg.append(campaignDTO.getCampaignTitle());
            msg.append("\n");

            log.info("Campaign Title: " + campaignDTO.getCampaignTitle());
        }





        // GPT 붙이기
        String result = gptClient.request("GPT/DashBoard.json", String.valueOf(msg));

        setDefaultMsg(result);
    }


}
