package com.linki.admin_integration_service.domain.payment.controller;


import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeKeysetResponseDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;
import com.linki.admin_integration_service.domain.payment.service.PaymentSubscribeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class PaymentSubscribeController {


    private final PaymentSubscribeService paymentSubscribeService;
    private final ModelMapper modelMapper;


    /**
     * cursor 정규화: "null" 문자열을 실제 null로 변환
     */
    private String normalizeCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty() || "null".equalsIgnoreCase(cursor.trim())) {
            return null;
        }
        return cursor.trim();
    }

    @GetMapping("/v1/admin/api/subscriptions")
    public ResponseEntity<?> getContracts(@RequestParam(value = "cursor", required = false) String cursor,
                                          @RequestParam(value = "size", defaultValue = "10") int size){
        // cursor 정규화: "null" 문자열을 실제 null로 변환
        String normalizedCursor = normalizeCursor(cursor);

        PaymentSubscribeKeysetResponseDTO keysetResponseDTO = paymentSubscribeService.getAllPaymentSubscribesWithKeyset(normalizedCursor, size);

        return ResponseEntity.ok(keysetResponseDTO);
    }

    @PostMapping("/v1/admin/api/subscriptions/search")
    public ResponseEntity<?> searchContract(@RequestBody PaymentSubscribeSearchDTO paymentSubscribeSearchDTO,@RequestParam(value = "cursor", required = false) String queryParamCursor,
                                                                            @RequestParam(value = "size", defaultValue = "10") int queryParamSize) {
        // 요청 바디의 cursor, size가 우선. 없으면 쿼리 파라미터 사용
        String cursor = paymentSubscribeSearchDTO.getCursor() != null ?
                paymentSubscribeSearchDTO.getCursor() : queryParamCursor;
        int size = paymentSubscribeSearchDTO.getSize() != null ?
                paymentSubscribeSearchDTO.getSize() : queryParamSize;

        // cursor 정규화
        String normalizedCursor = normalizeCursor(cursor);

        // 새로운 프론트엔드에서는 항상 Keyset 페이지네이션 사용
        PaymentSubscribeKeysetResponseDTO keysetResponse =
                paymentSubscribeService.searchPaymentSubscribeWithKeyset(paymentSubscribeSearchDTO, normalizedCursor, size);

        return ResponseEntity.ok(keysetResponse);
    }

    @PostMapping("/v1/admin/api/subscriptions/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(paymentSubscribeService.exportExcel());
    }
}
