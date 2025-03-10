package com.exchange.discount.config;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;
import java.util.function.Supplier;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager ehcacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        CacheConfiguration<String, Double> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class,
                        Double.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .heap(100, MemoryUnit.MB)
                )
                .withExpiry(new ExpiryPolicy<>() {
                    @Override
                    public Duration getExpiryForCreation(String s, Double aDouble) {
                        return Duration.ofSeconds(60);
                    }

                    @Override
                    public Duration getExpiryForAccess(String s, Supplier<? extends Double> supplier) {
                        return null;
                    }

                    @Override
                    public Duration getExpiryForUpdate(String s, Supplier<? extends Double> supplier, Double aDouble) {
                        return null;
                    }
                })
                .build();

        javax.cache.configuration.Configuration<String, Double> configuration =
                Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration);
        cacheManager.createCache("exchangeRatesCache", configuration);

        return cacheManager;
    }
}