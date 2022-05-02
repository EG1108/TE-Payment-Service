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
        System.out.println("\u001B[96m" + "*********************");
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
            System.out.println("\u001b[97m" + i.getTransfer_id() + "\t\t\u001b[94m" + toFrom + "\t\t" + "\u001B[92m$" + i.getAmount() + "\t\t" + statusColor(i.getTransfer_status_desc()) + "\u001b[96m");
        }
    }

    public void transferHistoryMenu(Transfer i) {
        System.out.println("\u001b[96m-----------------------------------------\n\t\t\tTransfer Details\t\t\t" +
                "\n-----------------------------------------");
        System.out.println("\u001b[96mId: \u001B[97m" + i.getTransfer_id());
        System.out.println("\u001b[96mFrom: \u001B[95m" + i.getUsernameFrom());
        System.out.println("\u001b[96mTo: \u001B[34m" + i.getUsernameTo());
        System.out.println("\u001b[96mType: \u001b[0m" + i.getTransfer_type_desc());
        System.out.println("\u001b[96mStatus: " + statusColor(i.getTransfer_status_desc()));
        System.out.println("\u001b[96mAmount: \u001B[92m$" + i.getAmount());
        System.out.println("\u001b[96m");
    }

    public void sendBucksMenu(List<User> listUsers) {
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID\t\t\t\tName");
        System.out.println("-------------------------------------------");
        for (User user : listUsers) {
            System.out.println("\u001B[97m" + user.getId() + "\t\t\t\u001b[94m" + user.getUsername() + "\u001b[96m");
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
                    "\u001b[97m" + i.getTransfer_id(), "\u001b[94m" + toFrom, "\u001B[92m$ " + i.getAmount() + "\u001b[96m"));
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
            return "\u001B[93m" + status;
        }
    }

}
