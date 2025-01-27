package com.example.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.scm.auth.authServices.MyUserDetailsService;

@Configuration
public class SecurityConfig {


    // @Autowired
    // private AuthSuccessHandler authSuccessHandler;

    @Lazy
    private final AuthSuccessHandler authSuccessHandler;

    public SecurityConfig(@Lazy AuthSuccessHandler authSuccessHandler) {
        this.authSuccessHandler = authSuccessHandler;
    }

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/home", "/register", "/do-register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/user/**").authenticated()
                        .anyRequest().authenticated())
                        .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        // .successHandler(authSuccessHandler)
                        .loginProcessingUrl("/authenticate") // Ensure this matches the form action URL
                        .defaultSuccessUrl("/user/profile")  // successforwardurl is causing error of Post method not supported
                        .failureUrl("/login?error=true") 
                        .usernameParameter("email") 
                        .passwordParameter("password")) 
                
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler(authSuccessHandler)
                        .failureUrl("/login?error=true")
                    )      
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL to trigger logout
                        .logoutSuccessUrl("/login?logout=true") // Redirect after successful logout
                        .invalidateHttpSession(true) // Invalidate session
                        .clearAuthentication(true)) // Clear authentication
                .build();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder());
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }
}






















// package com.example.scm.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.web.SecurityFilterChain;

// import com.example.scm.auth.authServices.MyUserDetailsService;

// @Configuration
// public class SecurityConfig {

//     @Autowired
//     private MyUserDetailsService myUserDetailsService;

//     @Bean
//     public BCryptPasswordEncoder encoder() {
//         return new BCryptPasswordEncoder(12);
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         return http
//                 .authorizeHttpRequests(
//                         request -> request
//                                 .requestMatchers("/home", "/register", "do-register", "/login", "/css/**", "/js/**",
//                                         "/images/**")
//                                 .permitAll()
//                                 .requestMatchers("/user/**").authenticated()
//                                 .anyRequest().authenticated())
//                 .formLogin(formLogin -> {
//                 formLogin
//                 .loginPage("/login")
//                 .loginProcessingUrl("/authenticate")
//                 .successForwardUrl("/user/dashboard")
//                 .defaultSuccessUrl("/home", true)
//                 .failureUrl("/home")
//                 .usernameParameter("email")
//                 .passwordParameter("password");
//                 })
//                 // .logout(logout -> {
//                 // logout
//                 // .logoutUrl("/do-logout")
//                 // .logoutSuccessUrl("/login?logout=true")
//                 // .invalidateHttpSession(true)
//                 // .clearAuthentication(true);
//                 // })
//                 .build();
//     }

//     @Bean
//     public AuthenticationProvider authProvider() {
//         DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//         provider.setUserDetailsService(myUserDetailsService);
//         provider.setPasswordEncoder(encoder());
//         return provider;
//     }

//     @Bean
//     public UserDetailsService userDetailsService() {
//         UserDetails user = User
//                 .withDefaultPasswordEncoder()
//                 .username("user")
//                 .password("u@123")
//                 .roles("USER")
//                 .build();

//         UserDetails admin = User
//                 .withDefaultPasswordEncoder()
//                 .username("admin")
//                 .password("a@123")
//                 .roles("ADMIN")
//                 .build();
//         return new InMemoryUserDetailsManager(user, admin);
//     }

// }
