package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("\u001B[36m" + "*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void transferHistoryMenu(List<Transfer> transfers, AuthenticatedUser user) {
        System.out.println("-----------------------------------------\n\t\t\tTransfers\n\nID\t\t\tFrom/To\t\t\tAmount" +
                "\n\t\t\t Type\n-----------------------------------------");
        for (Transfer i : transfers) {
            String toFrom = "";
            if (!i.getUsernameFrom().equals(user.getUser().getUsername())) {
                toFrom = "From: " + i.getUsernameFrom();
            } else {
                toFrom = "To: " + i.getUsernameTo();
            }
            System.out.println(i.getTransfer_id() + "\t\t" + toFrom + "\t\t" + "$" + i.getAmount() + "\t\t" + statusColor(i.getTransfer_status_desc()));
        }
    }

    public void transferHistoryMenu(Transfer i) {
        System.out.println("-----------------------------------------\n\t\t\tTransfer Details\t\t\t" +
                "\n-----------------------------------------");
        System.out.println("\u001B[37m Id: " + i.getTransfer_id());
        System.out.println("\u001B[35m From: " + i.getUsernameFrom());
        System.out.println("\u001B[34m To: " + i.getUsernameTo());
        System.out.println("Type: " + i.getTransfer_type_desc());
        System.out.println("Status: " + statusColor(i.getTransfer_status_desc()));
        System.out.println("Amount: $" + i.getAmount());
        System.out.println("");
    }

    public void sendBucksMenu(List<User> listUsers) {
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID\t\t\t\tName");
        System.out.println("-------------------------------------------");
        for (User user : listUsers) {
            System.out.println(user.getId() + "\t\t\t" + user.getUsername());
        }
        System.out.println("-------------------------------------------\n");
    }

    public void pendingRequestsMenu(List<Transfer> transfers) {
        String formatStr = "%-10s %-24s %-10s";
        System.out.println("\n-------------------------------------------");
        System.out.println("Pending Transfers");
        System.out.println(String.format(formatStr, "ID", "From", "Amount"));
        System.out.println("-------------------------------------------");

        for (Transfer i : transfers) {
            String toFrom = "";
            toFrom = i.getUsernameTo();
            System.out.println(String.format(formatStr,
                    i.getTransfer_id(), toFrom, "$ " + i.getAmount()));
        }
        System.out.println("-------------------------------------------");
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public String statusColor(String status) {
        if(status.equals("Approved")) {
            return "\u001B[32m" + status;
        } else if(status.equals("Rejected")) {
            return "\u001B[31m" + status;
        } else {
            return "\u001B[33m" + status;
        }
    }

}
