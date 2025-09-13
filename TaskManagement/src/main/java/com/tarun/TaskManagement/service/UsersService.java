package com.tarun.TaskManagement.service;

import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.model.ForgotPassword;
import com.tarun.TaskManagement.model.ResetPassword;
import com.tarun.TaskManagement.model.UserPrincipal;
import com.tarun.TaskManagement.model.Users;
import com.tarun.TaskManagement.repository.ForgotPasswordRepo;
import com.tarun.TaskManagement.repository.UsersRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsersService {

    @Autowired
    private JwtService service;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UsersRepo repo;

    @Autowired
    private ForgotPasswordRepo forgotRepo;

    @Autowired
    private JavaMailSender mailSender;


    public ApiResponseModel<Void> register(Users user){
        if(user.getUsername() == null || user.getPassword() == null || user.getRole() == null || user.getName() == null || user.getEmail() == null){
            return new ApiResponseModel<>(false,"Missing field",HttpStatus.PARTIAL_CONTENT.value(),null);
        }
        if(repo.findByUsername(user.getUsername()) != null){
            return new ApiResponseModel<>(false,"Username already exists",HttpStatus.CONFLICT.value(),null);
        }
        if(repo.findByEmail(user.getEmail()) != null){
            return new ApiResponseModel<>(false,"Email already exists",HttpStatus.CONFLICT.value(), null);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        System.out.println("User Created successfully with username : " + user.getUsername()  + " and password : " + user.getPassword());
        return new ApiResponseModel<>(true,"User Created Successfully", HttpStatus.CREATED.value(),null);
    }

    public String verify(Users user, HttpServletResponse response){
        System.out.println(user);
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authentication.isAuthenticated()){
            Users dbUser = repo.findByUsername(user.getUsername());
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
            return accessToken;
        }
        throw new BadCredentialsException("Invalid username or password.");
    }

    public String generateAccessToken(String refreshToken) throws IllegalAccessException {
        if(refreshToken == null){
            throw new IllegalAccessException("Refresh Token not found. Please login.");
        }
        String username = service.extractUserName(refreshToken);
        Users user = repo.findByUsername(username);
        if(user == null){
            throw new IllegalAccessException("User Not found.");
        }
        UserPrincipal userDetails = new UserPrincipal(user);
        if(!service.validateToken(refreshToken,userDetails)) {
            throw new IllegalAccessException("Invalid Token or Expired Token");
        }
        String accessToken = service.generateToken(user);
        System.out.println("Access token generated for user '" + username + "' : " + accessToken);
        return accessToken;
    }

    public void forgotPassword(Users user) {
        Users dbUser = repo.findByEmail(user.getEmail());
        if(dbUser != null){
            String token = UUID.randomUUID().toString();
            ForgotPassword forgotPassword = new ForgotPassword(dbUser.getId(),token, LocalDateTime.now().plusHours(1));
            forgotRepo.save(forgotPassword);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(dbUser.getEmail());
            message.setSubject("Password reset request from Taskwiser for " + dbUser.getUsername() );
            message.setText("Username : " + dbUser.getUsername() + "\nClick the given link to reset your password. \nhttp://localhost:5173/reset-password/" + token +
                    "\nThe reset link expires in one hour.");
            mailSender.send(message);

            System.out.println("Forgot password initiated and mail sent for user : " + forgotPassword);
        }
    }

    public void resetPassword(ResetPassword resetPassword) throws IllegalAccessException {
        ForgotPassword forgotPassword = forgotRepo.findByToken(resetPassword.getToken());
        if(forgotPassword == null){
            throw new BadCredentialsException("Token not found");
        }
        if(forgotPassword.getExpiry().isBefore(LocalDateTime.now())){
            throw new AccessDeniedException("Token expired");
        }
        Users dbUser = repo.findById(forgotPassword.getUserId()).orElse(null);
        if(dbUser == null){
            throw new IllegalAccessException("User not found");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        System.out.println("Password changed for '" + dbUser.getUsername() +"' sucessfully.");
        dbUser.setPassword(encoder.encode(resetPassword.getPassword()));
        repo.save(dbUser);
    }
}
