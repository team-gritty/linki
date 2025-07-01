package com.linki.admin_integration_service.domain.contract.service;

import com.linki.admin_integration_service.domain.contract.dto.*;
import com.linki.admin_integration_service.domain.contract.repository.myBatis.ContractMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<ContractDTO> getContracts() {
        List<ContractDTO> contractDTOList = contractMapper.getContracts();
        if(contractDTOList.isEmpty()){
            return Collections.emptyList();
        }
        return contractDTOList;
    }

    @Override
    public List<ContractDTO> searchContract(ContractSearchDTO contractSearchDTO) {
        String keyword = contractSearchDTO.getKeyword();
        String searchType = contractSearchDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return contractMapper.getContracts();
        }

        // 2. keyword는 있는데 searchType이 없으면 → 의미 없으므로 빈 리스트
        if ((searchType == null || searchType.isBlank()) && keyword != null && !keyword.isBlank()) {
            return Collections.emptyList();
        }

        // 3. keyword 없는데 searchType만 있으면 → 의미 없으므로 빈 리스트
        if ((keyword == null || keyword.isBlank())) {
            return Collections.emptyList();
        }

        // 4. 정상 검색
        contractSearchDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        contractSearchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<ContractDTO> searchResult = contractMapper.searchContract(contractSearchDTO);
        List<ContractDTO> result = new ArrayList<>();

        if(!searchResult.isEmpty() && contractSearchDTO.getSearchType().equals("influencername")){
            String findFirst = searchResult.get(0).getInfluencerName();
            result = searchResult.stream()
                    .filter(dto -> dto.getInfluencerName().equals(findFirst))
                    .toList();
        }

        if(!searchResult.isEmpty() && contractSearchDTO.getSearchType().equals("advertisername")){
            String findFirst = searchResult.get(0).getAdvertiserName();
            result = searchResult.stream()
                    .filter(dto -> dto.getAdvertiserName().equals(findFirst))
                    .toList();
        }

        if(!searchResult.isEmpty() && contractSearchDTO.getSearchType().equals("contractid")){
            String findFirst = searchResult.get(0).getContractId();
            result = searchResult.stream()
                    .filter(dto -> dto.getContractId().equals(findFirst))
                    .toList();
        }
        log.info("서비스 최종 검색어 searchType : {} 검색어 : {}",contractSearchDTO.getSearchType(),contractSearchDTO.getKeyword());
        log.info("result:{}", result);

        return result.isEmpty() ? Collections.emptyList() : result;
    }



    @Override
    public String exportExcel(){
        List<ContractDTO> contractDTOList = contractMapper.getContracts();
        return excelUtil.exportExcel(contractDTOList, ContractDTO.class, "contractList",null);
    }


    // ==============================================
    // Keyset 페이지네이션 구현
    // ==============================================


    @Override
    public ContractKeysetResponseDTO getContractsWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 계약 목록 조회 - cursor: {}, size: {}", cursor, size);
        
        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<ContractDTO> contractDTOList = contractMapper.getContractsWithKeyset(cursor, size + 1);
        return buildContractKeysetResponseDTO(contractDTOList, cursor, size);
    }

    @Override
    public ContractKeysetResponseDTO searchContractsWithKeyset(ContractSearchDTO contractSearchDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                contractSearchDTO.getSearchType(), contractSearchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = contractSearchDTO.getKeyword();
        String searchType = contractSearchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getContractsWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return ContractKeysetResponseDTO.builder()
                    .list(Collections.emptyList())
                    .hasNext(false)
                    .size(0)
                    .nextCursor(null)
                    .currentCursor(cursor)
                    .requestedSize(size)
                    .build();
        }

        // 검색 조건 정규화
        contractSearchDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        contractSearchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<ContractDTO> searchResult = contractMapper.searchContractWithKeyset(contractSearchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<ContractDTO> filteredResult = filterSearchResult(searchResult, contractSearchDTO);

        return buildContractKeysetResponseDTO(filteredResult, cursor, size);
    }


    private List<ContractDTO> filterSearchResult(List<ContractDTO> searchResult, ContractSearchDTO contractSearchDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = contractSearchDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "contractid" -> searchResult.get(0).getContractId();
            case "advertisername" -> searchResult.get(0).getAdvertiserName();
            case "influencername" -> searchResult.get(0).getInfluencerName();
            default -> null;
        };

        if (findFirstValue == null) {
            return searchResult;
        }

        return searchResult.stream()
                .filter(dto -> {
                    return switch (searchType) {
                        case "contractid" -> dto.getContractId().equals(findFirstValue);
                        case "advertisername" -> dto.getAdvertiserName().equals(findFirstValue);
                        case "influencername" -> dto.getInfluencerName().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    /**
     * Keyset 응답 객체를 구성합니다.
     * - 요청된 size + 1만큼의 데이터를 받아와 다음 페이지 존재 여부를 판단
     * - 실제 반환할 데이터는 size 개수로 제한
     * - contractLink 필드 추가 및 DTO 매핑 처리
     * @param contractDTOList 조회된 계약 데이터
     * @param cursor 요청 시 전달된 현재 cursor
     * @param size 요청된 데이터 수
     * @return Keyset 기반 응답 객체
     */
    private ContractKeysetResponseDTO buildContractKeysetResponseDTO(List<ContractDTO> contractDTOList, String cursor, int size) {
        boolean hasNext = contractDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<ContractDTO> actualData = hasNext ?
                contractDTOList.subList(0, size) : contractDTOList;


        // 다음 cursor 설정 (마지막 데이터의 contractId)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getContractId();
        }

        // DTO 변환
        List<ContractResponseDTO> responseList = actualData.stream()
                .map(dto -> modelMapper.map(dto, ContractResponseDTO.class))
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return ContractKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }








}
