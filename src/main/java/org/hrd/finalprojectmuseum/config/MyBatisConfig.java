package org.hrd.finalprojectmuseum.config;

import org.apache.ibatis.session.Configuration;
import org.hrd.finalprojectmuseum.model.enums.DayOfWeek;
import org.hrd.finalprojectmuseum.typehandler.DayOfWeekTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class MyBatisConfig {

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry()
                    .register(DayOfWeek.class, DayOfWeekTypeHandler.class);
        };
    }
}