package com.Gritty.Linki.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ObjectStorage {
    private String endPoint = "https://kr.object.ncloudstorage.com";
    private String regionName = "kr-standard";
    @Value("${secret-accessKey}")
    private String accessKey;
    @Value("${secret-secretKey}")
    private String secretKey;
    private String bucketName = "team-gritty-linki";

    // 허용된 이미지 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 파일을 Object Storage에 업로드하고 URL을 반환합니다.
     * UUID를 사용하여 파일명 중복을 방지하고 보안을 강화합니다.
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        // 파일 유효성 검사
        validateFile(multipartFile);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        String originalFilename = multipartFile.getOriginalFilename();

        // UUID + 타임스탬프를 사용한 고유 파일명 생성
        // 이유: 1) 중복 방지, 2) 보안 강화, 3) 추적 용이성, 4) 캐시 문제 해결
        String uniqueFilename = generateUniqueFilename(originalFilename);

        log.info("파일 업로드 시작: 원본명={}, 저장명={}, 크기={}bytes",
                originalFilename, uniqueFilename, multipartFile.getSize());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        // 캐시 제어 헤더 추가
        metadata.setCacheControl("max-age=31536000"); // 1년 캐시

        try {
            // 파일 업로드
            s3.putObject(bucketName, uniqueFilename, multipartFile.getInputStream(), metadata);

            // 접근 권한을 공개로 설정 (필수)
            setObjectACL(bucketName, uniqueFilename);

            // 업로드된 파일의 URL 반환
            String fileUrl = s3.getUrl(bucketName, uniqueFilename).toString();
            log.info("파일 업로드 성공: URL={}", fileUrl);

            return fileUrl;
        } catch (Exception e) {
            log.error("파일 업로드 실패: 파일명={}, 오류={}", uniqueFilename, e.getMessage());
            throw new IOException("파일 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일 유효성 검사
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("업로드할 파일이 없습니다.");
        }

        // 파일 크기 검사
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException(String.format("파일 크기가 너무 큽니다. 최대 %dMB까지 허용됩니다.",
                    MAX_FILE_SIZE / (1024 * 1024)));
        }

        // 파일 확장자 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new IOException("지원하지 않는 파일 형식입니다. 허용된 형식: " + ALLOWED_EXTENSIONS);
        }

        // MIME 타입 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("이미지 파일만 업로드 가능합니다.");
        }
    }

    /**
     * 유효한 이미지 파일인지 확인
     */
    private boolean isValidImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 고유한 파일명 생성
     * UUID + 타임스탬프 조합으로 파일명 충돌 가능성 제거
     */
    private String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        // 형식: campaign/{uuid}_{timestamp}.{extension}
        return String.format("campaign/%s_%d.%s", uuid, timestamp, extension);
    }

    /**
     * 접근 권한을 PublicRead로 변경
     */
    public void setObjectACL(String bucketName, String objectName) {
        try {
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();

            AccessControlList acl = s3.getObjectAcl(bucketName, objectName);
            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            s3.setObjectAcl(bucketName, objectName, acl);

            log.info("파일 권한 설정 완료: {}", objectName);
        } catch (Exception e) {
            log.error("파일 권한 설정 실패: {}, 오류: {}", objectName, e.getMessage());
        }
    }
}
