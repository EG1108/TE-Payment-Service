package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TenmoService tenmoService = new TenmoService();

    private AuthenticatedUser currentUser;


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        try {
            tenmoService.setAuthToken(currentUser.getToken());
        } catch (NullPointerException e) {
            System.out.println("Incorrect Username or Password");
        }

    }

    private void mainMenu() {
        Account acUser = tenmoService.getAccount(currentUser.getUser().getUsername());

        // move this to server side?
        if(acUser.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("There is a problem with your account. Please contact TEnmo.");
            System.exit(0);
        }

        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {

        Account acUser = tenmoService.getAccount(currentUser.getUser().getUsername());
        System.out.println("Your current account balance is: \u001B[92m$" + acUser.getBalance() + "\u001b[96m");

    }

    private void viewTransferHistory() {

        List<Transfer> transfers = tenmoService.listOfTransfers(2);
        consoleService.transferHistoryMenu(transfers, currentUser);
        int transferId = -1;
        while (transferId != 0) {
            transferId = consoleService.promptForInt("\nPlease enter transfer ID to view details (0 to cancel):\n");
            for (Transfer i : transfers) {
                if (transferId == i.getTransfer_id()) {
                    consoleService.transferHistoryMenu(i);
                    transferId = 0;
                }
            }
        }

    }

    private void viewPendingRequests() {

        List<Transfer> transfers = tenmoService.listOfTransfers(1);

        int pendingId = -1;
        while(pendingId != 0) {
            consoleService.pendingRequestsMenu(transfers);
            pendingId = consoleService.promptForInt("\nPlease enter transfer ID to approve/reject (0 to cancel):\n");
            Account acUser = tenmoService.getAccount(currentUser.getUser().getUsername());
            for (Transfer i : transfers) {
                if (pendingId == i.getTransfer_id()) {
                    int option = consoleService.promptForInt("\u001B[32m1: Approve\n\u001B[31m2: Reject\n\u001B[93m0: Don't approve or reject\n\u001b[96m----------\nPlease choose an option: ");
                    if (option == 1 && i.getAmount().compareTo(acUser.getBalance()) <= 0) {
                        tenmoService.approveBucks(i);
                        System.out.println("Transfer approved.");
                        pendingId = 0;
                    } else if (option == 1 && i.getAmount().compareTo(acUser.getBalance()) >= 0) {
                        System.out.println("You don't have enough money to approve this transfer.");
                    }else if (option == 2) {
                        tenmoService.rejectTransfer(i);
                        System.out.println("Transfer Rejected");
                        pendingId = 0;
                    } else if (option == 0) {
                        //Don't do anything
                    } else {
                        System.out.println("Invalid input.");
                    }
                }

            }
        }
    }

    private void sendBucks() {
        Account acUser = tenmoService.getAccount(currentUser.getUser().getUsername());
        List<User> listUsers = tenmoService.userList();
        Set<Long> values = new HashSet<>();
        for (User user : listUsers) {
            values.add(user.getId());
        }

        consoleService.sendBucksMenu(listUsers);
        int sendTo = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel):");


        while (Objects.equals((long) sendTo, currentUser.getUser().getId()) || (!values.contains((long) sendTo) && sendTo != 0)) {
            System.out.println("Invalid recipient.");
            sendTo = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel):");
        }

        BigDecimal amountToSend = consoleService.promptForBigDecimal("Enter amount:");



        while (amountToSend.compareTo(acUser.getBalance()) >= 0 || amountToSend.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Invalid transfer amount.");
            amountToSend = consoleService.promptForBigDecimal("Enter amount:");
        }
        if(amountToSend.compareTo(BigDecimal.ZERO) != 0) {
            Transfer transfer = new Transfer(currentUser.getUser().getId().intValue(), sendTo, amountToSend);
            tenmoService.transferBucks(transfer);
        }

    }


    private void requestBucks() {

        Account acUser = tenmoService.getAccount(currentUser.getUser().getUsername());
        List<User> listUsers = tenmoService.userList();
        Set<Long> values = new HashSet<>();
        for (User user : listUsers) {
            System.out.println("\u001B[97m" + user.getId() + " \u001b[96m:\u001b[94m " + user.getUsername() + "\u001b[96m");
            values.add(user.getId());
        }
            int requestFrom = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel):");

            if(Objects.equals((long) requestFrom, currentUser.getUser().getId()) || !values.contains((long) requestFrom)) {
                System.out.println("Invalid recipient.");
            } else {
                BigDecimal amountToRequest = consoleService.promptForBigDecimal("Enter amount:");
                if(amountToRequest.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Invalid transfer amount.");
                } else {
                    Transfer makeTransfer = new Transfer(requestFrom, currentUser.getUser().getId().intValue(), amountToRequest);
                    tenmoService.requestBucks(makeTransfer);
                }
            }


        }

    }

