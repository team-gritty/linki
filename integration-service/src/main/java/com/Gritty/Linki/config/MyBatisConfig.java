package com.Gritty.Linki.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {
        "com.Gritty.Linki.domain.user.influencer.campaign.repository.myBatis",
        "com.Gritty.Linki.domain.user.influencer.contract.repository.myBatis",
        "com.Gritty.Linki.domain.user.influencer.proposal.repository.myBatis",
        "com.Gritty.Linki.domain.user.influencer.review.repository.myBatis",
//        "com.Gritty.Linki.domain.user.advertiser.repository.myBatis"
})
public class MyBatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // // resources/mapper 경로에 있는 XML 파일 인식하도록 설정
        // Resource[] resources = new PathMatchingResourcePatternResolver()
        // .getResources("classpath:/mapper/**/*.xml");
        // sessionFactory.setMapperLocations(resources);

        return sessionFactory.getObject();

    }
}
