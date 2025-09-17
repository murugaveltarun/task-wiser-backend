package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.exception.MissingFieldException;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.UsersRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class Oauth2Service {

    @Autowired
    private UsersRepo repo;

    @Autowired
    private JwtService service;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public RedirectView auth(OAuth2AuthenticationToken token, HttpServletResponse response) {
        System.out.println(token);
        Users oauthUser = new Users();
        oauthUser.setProviderId(token.getName());
        oauthUser.setUsername(token.getName());
        oauthUser.setAuthProvider(token.getAuthorizedClientRegistrationId());
        oauthUser.setRole("ROLE_USER");
        oauthUser.setName((String) token.getPrincipal().getAttributes().get("name"));
        oauthUser.setEmail((String) token.getPrincipal().getAttributes().get("email"));
        oauthUser.setActive(true);

        Users dbUser = repo.findByAuthProviderAndProviderId(oauthUser.getAuthProvider(), oauthUser.getProviderId());

        if (dbUser == null) {
            Users emailUser = repo.findByEmail(oauthUser.getEmail());

            if (emailUser == null) {
                // completely new user
                oauthUser.setCreatedAt(LocalDateTime.now());
                oauthUser.setLastLoginAt(LocalDateTime.now());
                dbUser = repo.save(oauthUser);
            } else {
                // existing user signing in with oauth first time
                emailUser.setAuthProvider(oauthUser.getAuthProvider());
                emailUser.setProviderId(oauthUser.getProviderId());
                emailUser.setLastLoginAt(LocalDateTime.now());
                dbUser = repo.save(emailUser);
            }
        } else {
            // old user  in with oauth
            dbUser.setLastLoginAt(LocalDateTime.now());
            dbUser = repo.save(dbUser);
        }

        // safety check
        if (dbUser == null || dbUser.getAuthProvider() == null || dbUser.getProviderId() == null) {
            throw new MissingFieldException("Login Failed");
        }


        System.out.println(dbUser);
        if(token.isAuthenticated()){
            System.out.println("below");
            System.out.println(dbUser);
            String accessToken = service.generateToken(dbUser);
            String refreshToken = service.generateRefreshToken(dbUser);
            System.out.println("Access Token generated for userId : " + dbUser.getUsername() + " access token : " + accessToken);
            System.out.println("Refresh Token generated for userId : " + dbUser.getUsername() + " refresh token : " + refreshToken);

            ResponseCookie cookie = ResponseCookie.from("refreshToken",refreshToken)
                    .httpOnly(true)
                    .maxAge(Duration.ofDays(60))
                    .path("/")
                    .secure(true)
                    .sameSite("None")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            System.out.println("cookie header" + cookie.toString());

            return new RedirectView(frontendUrl + "/oauth2/callback/" + accessToken);
        }
        throw new BadCredentialsException("Invalid username or password.");

    }
}
