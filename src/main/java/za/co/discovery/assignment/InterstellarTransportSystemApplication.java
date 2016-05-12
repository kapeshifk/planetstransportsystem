package za.co.discovery.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.web.client.RestTemplate;
import za.co.discovery.assignment.formatter.EdgeFormatter;
import za.co.discovery.assignment.formatter.TrafficFormatter;
import za.co.discovery.assignment.formatter.VertexFormatter;

import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class InterstellarTransportSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterstellarTransportSystemApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "conversionService")
    public FormattingConversionService conversionService() {
        FormattingConversionServiceFactoryBean bean = new FormattingConversionServiceFactoryBean();
        bean.setRegisterDefaultFormatters(false);
        bean.setFormatters(getFormatters());
        return bean.getObject();
    }

    private Set<Formatter> getFormatters() {
        Set<Formatter> converters = new HashSet<>();
        converters.add(new VertexFormatter());
        converters.add(new EdgeFormatter());
        converters.add(new TrafficFormatter());
        return converters;
    }

}
