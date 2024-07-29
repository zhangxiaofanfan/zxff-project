package com.zhangxiaofanfan.cloud.framework.mybatis.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.DmKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.KingbaseKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zhangxiaofanfan.cloud.framework.mybatis.core.handler.DefaultDBFieldHandler;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * MyBaits 配置类
 *
 * @author zhangxiaofanfan
 */
@AutoConfiguration
@MapperScan(
        value = "${zxff.info.base-package}",
        annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}" // Mapper 懒加载，目前仅用于单元测试
)
public class ZxffMybatisAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
        return mybatisPlusInterceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler(){
        return new DefaultDBFieldHandler(); // 自动填充参数类
    }


    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
    public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
        DbType dbType = IdTypeEnvironmentPostProcessor.getDbType(environment);
        if (dbType == null) {
            throw new IllegalArgumentException("DbType为 null, 无法设置主键生成类型");
        }
        return switch (dbType) {
            case POSTGRE_SQL -> new PostgreKeyGenerator();
            case ORACLE, ORACLE_12C -> new OracleKeyGenerator();
            case H2 -> new H2KeyGenerator();
            case KINGBASE_ES -> new KingbaseKeyGenerator();
            case DM -> new DmKeyGenerator();
            default ->
                    // 找不到合适的 IKeyGenerator 实现类
                    throw new IllegalArgumentException(StrUtil.format("DbType{}找不到合适的IKeyGenerator实现类", dbType));
        };

    }
}
