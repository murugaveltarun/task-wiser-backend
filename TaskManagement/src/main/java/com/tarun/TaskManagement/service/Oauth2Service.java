package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.exception.MissingFieldException;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.UsersRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Duration;

@Service
public class Oauth2Service {

    @Autowired
    private UsersRepo repo;

    @Autowired
    private JwtService service;


    public RedirectView auth(OAuth2AuthenticationToken token, HttpServletResponse response) {

        Users oauthUser = new Users();
        oauthUser.setProviderId(token.getName());
        oauthUser.setUsername(token.getName());
        oauthUser.setAuthProvider(token.getAuthorizedClientRegistrationId());
        oauthUser.setRole("ROLE_USER");
        oauthUser.setName((String) token.getPrincipal().getAttributes().get("name"));
        oauthUser.setEmail((String) token.getPrincipal().getAttributes().get("email"));

        Users dbUser = repo.findByAuthProviderAndProviderId(oauthUser.getAuthProvider(), oauthUser.getProviderId());

        if(dbUser == null){

            Users emailUser = repo.findByEmail(oauthUser.getEmail());

            if(emailUser == null){
                //register fully new
                dbUser = repo.save(oauthUser);
            }else{
                //user already exist. so just update authProvider and providerId
                emailUser.setAuthProvider(oauthUser.getAuthProvider());
                emailUser.setProviderId(oauthUser.getProviderId());
                dbUser = repo.save(emailUser);
            }

        }
        System.out.println(oauthUser);
        // old user logging in

        if(dbUser.getAuthProvider() == null || dbUser.getProviderId() == null){
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

            String frontendUrl = "http://localhost:5173";
            return new RedirectView(frontendUrl + "/oauth2/callback/" + accessToken);
        }
        throw new BadCredentialsException("Invalid username or password.");

    }
}
