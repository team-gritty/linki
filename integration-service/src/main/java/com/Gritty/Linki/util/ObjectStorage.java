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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ObjectStorage {
    private String endPoint = "https://kr.object.ncloudstorage.com";
    private String regionName = "kr-standard";
    @Value("${secret-accessKey}")
    private String accessKey;
    @Value("${secret-secretKey}")
    private String secretKey;
    private String bucketName = "team-gritty-linki";

    public String uploadFile(MultipartFile multipartFile) throws IOException {


        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        String originalFilename = multipartFile.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // 파일 업로드
        s3.putObject(bucketName, originalFilename, multipartFile.getInputStream(), metadata);

        // 접근 권한을 공개로 설정 (필수)
        setObjectACL(bucketName, originalFilename);

        // 업로드된 파일의 URL 반환
        return s3.getUrl(bucketName, originalFilename).toString();
    }

    // 접근 권한을 PublicRead로 변경
    public void setObjectACL(String bucketName, String objectName) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        AccessControlList acl = s3.getObjectAcl(bucketName, objectName);
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3.setObjectAcl(bucketName, objectName, acl);
    }

}
