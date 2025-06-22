package com.ssg.chatservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
/**
 * MongoDB 관련 설정 클래스
 * - MongoTemplate 또는 Repository를 사용할 때 Mongo 문서에 자동 추가되는 '_class' 필드를 제거하기 위한 설정
 */
@Configuration
public class MongoConfig {
    //MongoDB와 java 객체 연결
    @Autowired
    private MongoMappingContext mongoMappingContext;

    /**
     * MongoDB 데이터를 자바 객체로 변환해주는 변환기(Converter)를 만들어 Bean으로 등록함
     * 이 Converter는 "_class"라는 불필요한 정보가 MongoDB에 저장되지 않도록 설정함
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory, MongoMappingContext mongoMappingContext){
        // DB 참조(@DBRef 등)를 처리해주는 도구 생성
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        // MongoDB ↔ 자바 객체 변환을 처리하는 도구 생성
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        // 변환기 설정: _class 필드(MongoDB에 저장되는 클래스 이름)를 저장하지 않게 설정함
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        // 설정된 converter를 Spring이 사용할 수 있도록 반환
        return  converter;
    }

}
