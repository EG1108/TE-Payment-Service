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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TenmoService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;


    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) { // variable forms request body

        HttpHeaders headers = new HttpHeaders(); // we need headers for request
        headers.setBearerAuth(this.authToken); // add auth line to header - either add provided token or null

        return new HttpEntity<>(transfer, headers);

    }

    private HttpEntity<Void> makeAuthEntity() {

        HttpHeaders headers = new HttpHeaders(); // we need headers for request
//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.authToken); // add auth line to header - either add provided token or null

        return new HttpEntity<>(headers);
    }

    public List<User> userList() {

        List<User> listUsers = null;
        try {
            listUsers = List.of(restTemplate.exchange(
                    API_BASE_URL,
                    HttpMethod.GET,
                    makeAuthEntity(),
                    User[].class
            ).getBody());



        } catch (RestClientException e) {
            System.out.println("Something went wrong.");
        }
        return listUsers;
    }


    public Account getAccount(String username) {


        return restTemplate.exchange(
                API_BASE_URL + "account/balance",
                HttpMethod.GET,
                makeAuthEntity(),
                Account.class
        ).getBody();
    }

    public List<Transfer> listOfTransfers(int code) {
        List<Transfer> transferList = new ArrayList<>();
        if (code == 2) {
            transferList = List.of(
                    restTemplate.exchange(
                            API_BASE_URL + "transfer/",
                            HttpMethod.GET,
                            makeAuthEntity(),
                            Transfer[].class
                    ).getBody());
        } else if (code == 1) {


        transferList = List.of(
                restTemplate.exchange(
                        API_BASE_URL + "pending/", // get username out of path
                        HttpMethod.GET,
                        makeAuthEntity(),
                        Transfer[].class
                ).getBody());
    }
        return transferList;

    }

    public Transfer transferBucks(Transfer transfer) {

        try {
            restTemplate.exchange(
                    API_BASE_URL + "transfer",
                    HttpMethod.PUT,
                    makeTransferEntity(transfer),
                    Transfer.class
            ).getBody();
            System.out.println("Transfer succeeded.");
        } catch (ResponseStatusException e) {
            System.out.println("Transfer failed - insufficient balance.");
        } catch (IllegalArgumentException f) {
            System.out.println("Transfer failed - account error.");
        }
        return transfer;
    }

    public Transfer requestBucks(Transfer transfer) {


        return restTemplate.exchange(
                API_BASE_URL + "request",
                HttpMethod.POST,
                makeTransferEntity(transfer),
                Transfer.class
        ).getBody();


    }

    public List<Transfer> pendingTransfers() {

        List<Transfer> transferList = List.of(
                restTemplate.exchange(
                        API_BASE_URL + "pending/", // get username out of path
                        HttpMethod.GET,
                        makeAuthEntity(),
                        Transfer[].class
                ).getBody());

        return transferList;
    }

    public Transfer approveBucks(Transfer transfer) {
        return restTemplate.exchange(
                API_BASE_URL + "approve",
                HttpMethod.PUT,
                makeTransferEntity(transfer),
                Transfer.class
        ).getBody();
    }

    public Transfer rejectTransfer(Transfer transfer) {
        return restTemplate.exchange(
                API_BASE_URL + "reject",
                HttpMethod.PUT,
                makeTransferEntity(transfer),
                Transfer.class
        ).getBody();
    }
}
