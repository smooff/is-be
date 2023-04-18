package sk.stuba.sdg.isbe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class WebConfig{

    @Value("${sdg.http.auth-token-header-name}")
    private String principalRequestHeader;

    @Value("${spring.security.user.password}")
    private String principalRequestValue;

    @Autowired
    Environment environment;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // resolves 403 forbidden - when POST curl (DB insert) triggers
        http.csrf().disable();

//        if(environment.getActiveProfiles().equals("deployment")){
//            APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);
//            filter.setAuthenticationManager(authentication -> {
//                String principal = (String) authentication.getPrincipal();
//                if (!principalRequestValue.equals(principal))
//                {
//                    throw new BadCredentialsException("The API key was not found or not the expected value.");
//                }
//                authentication.setAuthenticated(true);
//                return authentication;
//            });
//            // use the API key like authenticate method, comment all below for see swagger and connect ot endpoints
//            // without authentication
//            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
//                and().addFilter(filter).authorizeHttpRequests().
//                requestMatchers("/api/device").permitAll().
//                anyRequest().authenticated();
//        }

        // http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        return http.build();
    }
}
