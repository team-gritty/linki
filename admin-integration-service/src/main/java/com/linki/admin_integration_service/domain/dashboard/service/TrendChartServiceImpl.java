package com.linki.admin_integration_service.domain.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.TrendChartDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.TrendMapperDTO;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.DashBoardSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrendChartServiceImpl implements TrendChartService {
    private final DashBoardSummaryMapper dashBoardSummaryMapper;

    @Override
    @Scheduled(cron = "0 0 * * * *")
    @CachePut(value = "dashboard", key = "'trendChart'")
    public TrendChartDTO getDashboardTrendChart() {
        TrendChartDTO trendChartDTO = new TrendChartDTO();
        trendChartDTO.setNewAdvertisers(getNewAdvertisers());
        trendChartDTO.setNewInfluencers(getNewInfluencers());
        trendChartDTO.setNewMembers(getNewMembers());
        return trendChartDTO;
    }




    private Map<String, Map<String, Integer>> getNewMembers() {
        List<TrendMapperDTO> trendMapperDTOList =
                dashBoardSummaryMapper.getEnterDay().stream()
                        .filter(dto -> dto.getRole().equals("ROLE_MEMBER"))
                        .toList();
        Map<String, Map<String, Integer>> newMembers = new HashMap<>();

        for (TrendMapperDTO dto : trendMapperDTOList) {
            if (dto.getEnterDay() == null) continue;

            // 날짜에서 년-월 뽑기
            YearMonth yearMonth = YearMonth.from(dto.getEnterDay());
            String year = String.valueOf(yearMonth.getYear());
            String month = String.format("%02d", yearMonth.getMonthValue());

            // 년도 맵 없으면 생성
            newMembers.putIfAbsent(year, new HashMap<>());
            Map<String, Integer> monthMap = newMembers.get(year);

            // 월 카운트 증가
            monthMap.put(month, monthMap.getOrDefault(month, 0) + 1);
        }
        return newMembers;
    }

    private Map<String, Map<String, Integer>> getNewAdvertisers() {

        List<TrendMapperDTO> trendMapperDTOList =
                dashBoardSummaryMapper.getEnterDay().stream()
                        .filter(dto -> dto.getRole().equals("ROLE_ADVERTISER"))
                        .toList();
        Map<String, Map<String, Integer>> newAdvertisers = new HashMap<>();

        for (TrendMapperDTO dto : trendMapperDTOList) {
            if (dto.getEnterDay() == null) continue;

            // 날짜에서 년-월 뽑기
            YearMonth yearMonth = YearMonth.from(dto.getEnterDay());
            String year = String.valueOf(yearMonth.getYear());
            String month = String.format("%02d", yearMonth.getMonthValue());

            // 년도 맵 없으면 생성
            newAdvertisers.putIfAbsent(year, new HashMap<>());
            Map<String, Integer> monthMap = newAdvertisers.get(year);

            // 월 카운트 증가
            monthMap.put(month, monthMap.getOrDefault(month, 0) + 1);
        }


        return newAdvertisers;
    }

    private  Map<String, Map<String, Integer>> getNewInfluencers(){


        List<TrendMapperDTO> trendMapperDTOList =
                dashBoardSummaryMapper.getEnterDay().stream()
                        .filter(dto -> dto.getRole().equals("ROLE_INFLUENCER"))
                        .toList();
        Map<String, Map<String, Integer>> newInfluencers = new HashMap<>();

        for (TrendMapperDTO dto : trendMapperDTOList) {
            if (dto.getEnterDay() == null) continue;

            // 날짜에서 년-월 뽑기
            YearMonth yearMonth = YearMonth.from(dto.getEnterDay());
            String year = String.valueOf(yearMonth.getYear());
            String month = String.format("%02d", yearMonth.getMonthValue());

            // 년도 맵 없으면 생성
            newInfluencers.putIfAbsent(year, new HashMap<>());
            Map<String, Integer> monthMap = newInfluencers.get(year);

            // 월 카운트 증가
            monthMap.put(month, monthMap.getOrDefault(month, 0) + 1);
        }

        return newInfluencers;
    }
}
