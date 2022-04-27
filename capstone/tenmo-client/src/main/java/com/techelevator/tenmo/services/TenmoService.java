package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TenmoService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;



    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private HttpEntity<Void> makeAuthEntity() {

        HttpHeaders headers = new HttpHeaders(); // we need headers for request
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.authToken); // add auth line to header - either add provided token or null
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return entity;
    }

    public boolean userList() {

        List<User> listUsers = List.of(restTemplate.exchange(
                API_BASE_URL,
                HttpMethod.GET,
                makeAuthEntity(),
                User[].class
        ).getBody());

        for(User user : listUsers) {
            System.out.println(user.getId() + " : " + user.getUsername());
        }

        return true;
    }


    public Account getAccount(String username) {


        return restTemplate.exchange(
        API_BASE_URL + "account/" + username,
        HttpMethod.GET,
        makeAuthEntity(),
        Account.class
            ).getBody();
    }

    public Transfer transferBucks(Transfer transfer) {


        return restTemplate.exchange(
                API_BASE_URL + "transfer/",
                HttpMethod.POST,
                makeAuthEntity(),
                Transfer.class
        ).getBody();






    }

}