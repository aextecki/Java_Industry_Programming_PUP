// name: Alex Mortel
// Course: MSIT - PUP OU - Java Programming MIT-592 
// Date: 8/25/2025
// Description: A console-based Microfinance Management System in Java with MariaDB backend.
// Features: Client Management, Loan Management, Payment Processing, Reporting
// Note: Ensure MariaDB JDBC Driver is in the classpath.    

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

class Client {
    private final int id;
    private final String name;
    private final String phone;
    private final String address;
    private final double creditScore; // 0.0 to 100.0

    Client(int id, String name, String phone, String address, double creditScore) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.creditScore = creditScore;
    }

    // public Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public double getCreditScore() { return creditScore; }
}

class Loan {
    private final int loanId;
    private final int clientId;
    private final double amount;
    private final double interestRate;
    private final int durationMonths;
    private String status; // "PENDING", "APPROVED", "REJECT", "PAID"
    private final Date issueDate;
    private double paidAmount;
    private final String clientName; // Added to store client name for display

    Loan(int loanId, int clientId, double amount, double interestRate, int durationMonths, String status, Date issueDate, double paidAmount, String clientName) {
        this.loanId = loanId;
        this.clientId = clientId;
        this.amount = amount;
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.status = status;
        this.issueDate = issueDate;
        this.paidAmount = paidAmount;
        this.clientName = clientName;
    }

    // Getters
    public int getLoanId() { return loanId; }
    public int getClientId() { return clientId; }
    public double getAmount() { return amount; }
    public double getInterestRate() { return interestRate; }
    public int getDurationMonths() { return durationMonths; }
    public String getStatus() { return status; }
    public Date getIssueDate() { return issueDate; }
    public double getPaidAmount() { return paidAmount; }
    public String getClientName() { return clientName; }
    public void setStatus(String status) { this.status = status; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    // Loan Calculations
    public double calculateMonthlyPayment() {
        double monthlyRate = interestRate / 100 / 12;
        if (monthlyRate == 0) {
            return amount / durationMonths; // No interest case
        }
        return amount * (monthlyRate * Math.pow(1 + monthlyRate, durationMonths)) /
                (Math.pow(1 + monthlyRate, durationMonths) - 1);
    }

    public double calculateTotalInterest() {
        double monthlyPayment = calculateMonthlyPayment();
        return (monthlyPayment * durationMonths) - amount;
    }

    public double calculateOutstandingBalance() {
        double monthlyPayment = calculateMonthlyPayment();
        double monthlyRate = interestRate / 100 / 12;
        if (monthlyRate == 0) {
            return amount - paidAmount;
        }
        double paymentsMade = paidAmount / monthlyPayment;
        int k = (int) Math.floor(paymentsMade);
        return amount * ((Math.pow(1 + monthlyRate, durationMonths) - Math.pow(1 + monthlyRate, k)) /
                (Math.pow(1 + monthlyRate, durationMonths) - 1));
    }

    public double calculateTotalPayable() {
        return amount + calculateTotalInterest();
    }
}

class Payment {
    private int paymentId;
    private int loanId;
    private double amount;
    private Date paymentDate;

    Payment(int paymentId, int loanId, double amount, Date paymentDate) {
        this.paymentId = paymentId;
        this.loanId = loanId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public int getLoanId() { return loanId; }
    public double getAmount() { return amount; }
    public Date getPaymentDate() { return paymentDate; }
}

public class MicrofinanceManagement {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/YourDBName";
    private static final String DB_USER = "YourUsername";
    private static final String DB_PASSWORD = "YourPassword";
    private static final double MIN_CREDIT_SCORE_FOR_LOAN = 50.0;
    private static final double MIN_CREDIT_SCORE_FOR_APPROVAL = 70.0;

    public static void main(String[] args) {
        // Initialize database and create tables
        initializeDatabase();

        // Add sample data (only if tables are empty)
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (isClientTableEmpty(conn)) {
                addSampleClients(conn);
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            return;
        }

        System.out.println("**** MICROFINANCE MANAGEMENT SYSTEM ****");

        while (true) {
            showMainMenu();
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1: manageClients(); break;
                case 2: manageLoans(); break;
                case 3: managePayments(); break;
                case 4: generateReports(); break;
                case 5:
                    System.out.println("Thank you for using the system!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void initializeDatabase() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS clients (" +
                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "phone VARCHAR(20) NOT NULL, " +
                        "address VARCHAR(255) NOT NULL, " +
                        "credit_score DOUBLE DEFAULT 75.0)");
                stmt.execute("CREATE TABLE IF NOT EXISTS loans (" +
                        "loan_id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        "client_id INTEGER NOT NULL, " +
                        "amount DOUBLE NOT NULL, " +
                        "interest_rate DOUBLE NOT NULL, " +
                        "duration_months INTEGER NOT NULL, " +
                        "status VARCHAR(20) NOT NULL, " +
                        "issue_date DATE NOT NULL, " +
                        "paid_amount DOUBLE DEFAULT 0.0, " +
                        "FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS payments (" +
                        "payment_id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        "loan_id INTEGER NOT NULL, " +
                        "amount DOUBLE NOT NULL, " +
                        "payment_date DATE NOT NULL, " +
                        "FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE RESTRICT)");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database Initialization Error: " + e.getMessage());
        }
    }

    private static boolean isClientTableEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM clients")) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        }
    }

    private static void addSampleClients(Connection conn) throws SQLException {
        String sql = "INSERT INTO clients (name, phone, address, credit_score) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String[][] sampleClients = {
                    {"Juan Dela Cruz", "09123456789", "New Manila", "75.0"},
                    {"Maria Santos", "09234567890", "Quezon City", "65.0"},
                    {"Sarah Marvy", "092356645458", "Alabang Muntinlupa", "80.0"}
            };
            for (String[] client : sampleClients) {
                pstmt.setString(1, client[0]);
                pstmt.setString(2, client[1]);
                pstmt.setString(3, client[2]);
                pstmt.setDouble(4, Double.parseDouble(client[3]));
                pstmt.executeUpdate();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n***** MAIN MENU *****");
        System.out.println("1. Client Management");
        System.out.println("2. Loan Management");
        System.out.println("3. Payment Processing");
        System.out.println("4. Generate Reports");
        System.out.println("5. Exit the System");
    }

    private static void manageClients() {
        while (true) {
            System.out.println("\n***** CLIENT MANAGEMENT *****");
            System.out.println("1. Add New Client");
            System.out.println("2. View All Clients");
            System.out.println("3. Search Client");
            System.out.println("4. Edit Client");
            System.out.println("5. Delete Client");
            System.out.println("6. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1: addClient(); break;
                case 2: viewAllClients(); break;
                case 3: searchClient(); break;
                case 4: editClient(); break;
                case 5: deleteClient(); break;
                case 6: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void addClient() {
        System.out.println("\n--- Add New Client ---");
        String name = getStringInput("Enter client name: ");
        String phone = getStringInput("Enter phone number: ");
        String address = getStringInput("Enter address: ");
        double creditScore = getDoubleInput("Enter credit score (0-100): ");
        if (creditScore < 0 || creditScore > 100) {
            System.out.println("Invalid credit score! Must be between 0 and 100.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO clients (name, phone, address, credit_score) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setDouble(4, creditScore);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Client added successfully! Client ID: " + rs.getInt(1));
                Client newClient = new Client(rs.getInt(1), name, phone, address, creditScore);
                System.out.println("\nNew Client Details:");
                printClientTable(Collections.singletonList(newClient));
            }
        } catch (SQLException e) {
            System.err.println("Error adding client: " + e.getMessage());
        }
    }

    private static void deleteClient() {
        System.out.println("\n--- Delete Client ---");
        int id = getIntInput("Enter client ID to delete: ");

        Client client = findClient(id);
        if (client == null) {
            System.out.println("Client not found!");
            return;
        }

        System.out.println("\nClient Details:");
        printClientTable(Collections.singletonList(client));

        // Check for associated loans
        if (hasAssociatedLoans(id)) {
            System.out.println("Cannot delete client: Client has associated loans.");
            return;
        }

        // Confirm deletion
        System.out.print("Are you sure you want to delete this client? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Client deleted successfully!");
                System.out.println("\nUpdated Client List:");
                viewAllClients();
            } else {
                System.out.println("Failed to delete client.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting client: " + e.getMessage());
        }
    }

    private static boolean hasAssociatedLoans(int clientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM loans WHERE client_id = ?")) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking associated loans: " + e.getMessage());
            return true; // Assume loans exist to prevent accidental deletion
        }
    }

    private static void printClientTable(List<Client> clients) {
        if (clients.isEmpty()) {
            System.out.println("No clients found.");
            return;
        }

        int idWidth = 5;
        int nameWidth = 20;
        int phoneWidth = 15;
        int addressWidth = 20;
        int creditScoreWidth = 12;

        System.out.print("+");
        System.out.print("-".repeat(idWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(nameWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(phoneWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(addressWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(creditScoreWidth + 2));
        System.out.println("+");

        System.out.printf("| %-" + idWidth + "s | %-" + nameWidth + "s | %-" + phoneWidth + "s | %-" + addressWidth + "s | %-" + creditScoreWidth + "s |%n",
                "id", "name", "phone", "address", "credit_score");
        // priting the output like the Mysql Format
        System.out.print("+");
        System.out.print("-".repeat(idWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(nameWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(phoneWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(addressWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(creditScoreWidth + 2));
        System.out.println("+");

        for (Client client : clients) {
            System.out.printf("| %-" + idWidth + "d | %-" + nameWidth + "s | %-" + phoneWidth + "s | %-" + addressWidth + "s | %-" + creditScoreWidth + ".1f |%n",
                    client.getId(), client.getName(), client.getPhone(), client.getAddress(), client.getCreditScore());
        }
        // printing out like Mysql Database format
        System.out.print("+");
        System.out.print("-".repeat(idWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(nameWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(phoneWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(addressWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(creditScoreWidth + 2));
        System.out.println("+");
    }

    private static void printLoanTable(List<Loan> loans) {
        if (loans.isEmpty()) {
            System.out.println("No loans found.");
            return;
        }

        int loanIdWidth = 8;
        int clientNameWidth = 20;
        int amountWidth = 10;
        int rateWidth = 8;
        int monthsWidth = 8;
        int statusWidth = 12;
        int issueDateWidth = 12;
        int paidWidth = 10;
        int monthlyWidth = 12;
        int balanceWidth = 12;
        int totalPayWidth = 12;

        System.out.print("+");
        System.out.print("-".repeat(loanIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(clientNameWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(amountWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(rateWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(monthsWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(statusWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(issueDateWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(paidWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(monthlyWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(balanceWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(totalPayWidth + 2));
        System.out.println("+");

        System.out.printf("| %-" + loanIdWidth + "s | %-" + clientNameWidth + "s | %-" + amountWidth + "s | %-" + rateWidth + "s | %-" + monthsWidth + "s | %-" + statusWidth + "s | %-" + issueDateWidth + "s | %-" + paidWidth + "s | %-" + monthlyWidth + "s | %-" + balanceWidth + "s | %-" + totalPayWidth + "s |%n",
                "loan_id", "client_name", "amount", "rate", "months", "status", "issue_date", "paid", "monthly", "balance", "total_pay");

        System.out.print("+");
        System.out.print("-".repeat(loanIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(clientNameWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(amountWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(rateWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(monthsWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(statusWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(issueDateWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(paidWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(monthlyWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(balanceWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(totalPayWidth + 2));
        System.out.println("+");

        for (Loan loan : loans) {
            System.out.printf("| %-" + loanIdWidth + "d | %-" + clientNameWidth + "s | %-" + amountWidth + ".2f | %-" + rateWidth + ".1f | %-" + monthsWidth + "d | %-" + statusWidth + "s | %-" + issueDateWidth + "s | %-" + paidWidth + ".2f | %-" + monthlyWidth + ".2f | %-" + balanceWidth + ".2f | %-" + totalPayWidth + ".2f |%n",
                    loan.getLoanId(), loan.getClientName(), loan.getAmount(), loan.getInterestRate(),
                    loan.getDurationMonths(), loan.getStatus(), dateFormat.format(loan.getIssueDate()),
                    loan.getPaidAmount(), loan.calculateMonthlyPayment(), loan.calculateOutstandingBalance(),
                    loan.calculateTotalPayable());
        }

        System.out.print("+");
        System.out.print("-".repeat(loanIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(clientNameWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(amountWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(rateWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(monthsWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(statusWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(issueDateWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(paidWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(monthlyWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(balanceWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(totalPayWidth + 2));
        System.out.println("+");
    }

    private static void printPaymentTable(List<Payment> payments) {
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        int paymentIdWidth = 12;
        int loanIdWidth = 8;
        int amountWidth = 10;
        int dateWidth = 12;

        System.out.print("+");
        System.out.print("-".repeat(paymentIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(loanIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(amountWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(dateWidth + 2));
        System.out.println("+");

        System.out.printf("| %-" + paymentIdWidth + "s | %-" + loanIdWidth + "s | %-" + amountWidth + "s | %-" + dateWidth + "s |%n",
                "payment_id", "loan_id", "amount", "date");

        System.out.print("+");
        System.out.print("-".repeat(paymentIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(loanIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(amountWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(dateWidth + 2));
        System.out.println("+");

        for (Payment payment : payments) {
            System.out.printf("| %-" + paymentIdWidth + "d | %-" + loanIdWidth + "d | %-" + amountWidth + ".2f | %-" + dateWidth + "s |%n",
                    payment.getPaymentId(), payment.getLoanId(), payment.getAmount(),
                    dateFormat.format(payment.getPaymentDate()));
        }

        System.out.print("+");
        System.out.print("-".repeat(paymentIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(loanIdWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(amountWidth + 2));
        System.out.print("+");
        System.out.print("-".repeat(dateWidth + 2));
        System.out.println("+");
    }

    private static void viewAllClients() {
        System.out.println("\n--- All Clients ---");
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {
            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getDouble("credit_score")
                ));
            }
            printClientTable(clients);
        } catch (SQLException e) {
            System.err.println("Error retrieving clients: " + e.getMessage());
        }
    }

    private static void searchClient() {
        int id = getIntInput("Enter client ID to search: ");
        Client client = findClient(id);
        if (client != null) {
            System.out.println("\nClient Found:");
            printClientTable(Collections.singletonList(client));
        } else {
            System.out.println("Client not found!");
        }
    }

    private static void editClient() {
        System.out.println("\n--- Edit Client ---");
        int id = getIntInput("Enter client ID to edit: ");

        Client client = findClient(id);
        if (client == null) {
            System.out.println("Client not found!");
            return;
        }

        System.out.println("\nCurrent Client Details:");
        printClientTable(Collections.singletonList(client));

        System.out.println("\nEnter new values (press Enter to keep current value):");
        String name = getStringInput("Enter new name [" + client.getName() + "]: ");
        name = name.isEmpty() ? client.getName() : name;

        String phone = getStringInput("Enter new phone [" + client.getPhone() + "]: ");
        phone = phone.isEmpty() ? client.getPhone() : phone;

        String address = getStringInput("Enter new address [" + client.getAddress() + "]: ");
        address = address.isEmpty() ? client.getAddress() : address;

        String creditScoreInput = getStringInput("Enter new credit score (0-100) [" + client.getCreditScore() + "]: ");
        double creditScore;
        try {
            creditScore = creditScoreInput.isEmpty() ? client.getCreditScore() : Double.parseDouble(creditScoreInput);
            if (creditScore < 0 || creditScore > 100) {
                System.out.println("Invalid credit score! Must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid credit score format!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE clients SET name = ?, phone = ?, address = ?, credit_score = ? WHERE id = ?")) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setDouble(4, creditScore);
            pstmt.setInt(5, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nClient updated successfully!");
                Client updatedClient = new Client(id, name, phone, address, creditScore);
                System.out.println("\nUpdated Client Details:");
                printClientTable(Collections.singletonList(updatedClient));
            } else {
                System.out.println("Failed to update client.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating client: " + e.getMessage());
        }
    }

    private static void manageLoans() {
        while (true) {
            System.out.println("\n***** LOAN MANAGEMENT *****");
            System.out.println("1. Apply for Loan");
            System.out.println("2. Approve/Reject Loan");
            System.out.println("3. View All Loans");
            System.out.println("4. View Client Loans");
            System.out.println("5. Edit Loan");
            System.out.println("6. Delete Loan");
            System.out.println("7. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1: applyForLoan(); break;
                case 2: approveRejectLoan(); break;
                case 3: viewAllLoans(); break;
                case 4: viewClientLoans(); break;
                case 5: editLoan(); break;
                case 6: deleteLoan(); break;
                case 7: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void applyForLoan() {
        System.out.println("\n--- Apply for Loan ---");
        int clientId = getIntInput("Enter client ID: ");

        Client client = findClient(clientId);
        if (client == null) {
            System.out.println("Client not found!");
            return;
        }

        System.out.println("Client Details:");
        printClientTable(Collections.singletonList(client));

        if (client.getCreditScore() < MIN_CREDIT_SCORE_FOR_LOAN) {
            System.out.println("Client credit score is too low to apply for a loan.");
            return;
        }

        if (hasPendingLoans(clientId)) {
            System.out.println("Client has pending loans. Cannot apply for a new loan until resolved.");
            return;
        }

        double amount = getDoubleInput("Enter loan amount: ");
        if (amount <= 0) {
            System.out.println("Invalid loan amount! Must be positive.");
            return;
        }
        double interestRate = getDoubleInput("Enter interest rate (%): ");
        if (interestRate < 0) {
            System.out.println("Invalid interest rate! Must be non-negative.");
            return;
        }
        int duration = getIntInput("Enter duration (months): ");
        if (duration <= 0) {
            System.out.println("Invalid duration! Must be positive.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO loans (client_id, amount, interest_rate, duration_months, status, issue_date, paid_amount) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, clientId);
            pstmt.setDouble(2, amount);
            pstmt.setDouble(3, interestRate);
            pstmt.setInt(4, duration);
            pstmt.setString(5, "PENDING");
            pstmt.setString(6, dateFormat.format(new Date()));
            pstmt.setDouble(7, 0.0);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Loan application submitted for " + client.getName() + "! Loan ID: " + rs.getInt(1));
                Loan loan = new Loan(rs.getInt(1), clientId, amount, interestRate, duration, "PENDING", new Date(), 0.0, client.getName());
                System.out.println("\nLoan Details:");
                printLoanTable(Collections.singletonList(loan));
            }
        } catch (SQLException e) {
            System.err.println("Error applying for loan: " + e.getMessage());
        }
    }

    private static void editLoan() {
        System.out.println("\n--- Edit Loan ---");
        int loanId = getIntInput("Enter loan ID to edit: ");

        Loan loan = findLoan(loanId);
        if (loan == null) {
            System.out.println("Loan not found!");
            return;
        }

        System.out.println("\nCurrent Loan Details:");
        printLoanTable(Collections.singletonList(loan));

        System.out.println("\nEnter new values (press Enter to keep current value):");
        String amountInput = getStringInput("Enter new loan amount [" + loan.getAmount() + "]: ");
        double amount;
        try {
            amount = amountInput.isEmpty() ? loan.getAmount() : Double.parseDouble(amountInput);
            if (amount <= 0) {
                System.out.println("Invalid loan amount! Must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid loan amount format!");
            return;
        }

        String interestRateInput = getStringInput("Enter new interest rate (%) [" + loan.getInterestRate() + "]: ");
        double interestRate;
        try {
            interestRate = interestRateInput.isEmpty() ? loan.getInterestRate() : Double.parseDouble(interestRateInput);
            if (interestRate < 0) {
                System.out.println("Invalid interest rate! Must be non-negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid interest rate format!");
            return;
        }

        String durationInput = getStringInput("Enter new duration (months) [" + loan.getDurationMonths() + "]: ");
        int duration;
        try {
            duration = durationInput.isEmpty() ? loan.getDurationMonths() : Integer.parseInt(durationInput);
            if (duration <= 0) {
                System.out.println("Invalid duration! Must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid duration format!");
            return;
        }

        System.out.println("Select new status [" + loan.getStatus() + "]:");
        System.out.println("1. PENDING");
        System.out.println("2. APPROVED");
        System.out.println("3. REJECT");
        System.out.println("4. PAID");
        System.out.println("5. Keep current status");
        int statusChoice = getIntInput("Enter choice: ");
        String status;
        switch (statusChoice) {
            case 1: status = "PENDING"; break;
            case 2: status = "APPROVED"; break;
            case 3: status = "REJECT"; break;
            case 4: status = "PAID"; break;
            case 5: status = loan.getStatus(); break;
            default:
                System.out.println("Invalid status choice!");
                return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE loans SET amount = ?, interest_rate = ?, duration_months = ?, status = ? WHERE loan_id = ?")) {
            pstmt.setDouble(1, amount);
            pstmt.setDouble(2, interestRate);
            pstmt.setInt(3, duration);
            pstmt.setString(4, status);
            pstmt.setInt(5, loanId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nLoan updated successfully!");
                Loan updatedLoan = new Loan(loan.getLoanId(), loan.getClientId(), amount, interestRate, duration,
                        status, loan.getIssueDate(), loan.getPaidAmount(), loan.getClientName());
                System.out.println("\nUpdated Loan Details:");
                printLoanTable(Collections.singletonList(updatedLoan));
            } else {
                System.out.println("Failed to update loan.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating loan: " + e.getMessage());
        }
    }

    private static void deleteLoan() {
        System.out.println("\n--- Delete Loan ---");
        int loanId = getIntInput("Enter loan ID to delete: ");

        Loan loan = findLoan(loanId);
        if (loan == null) {
            System.out.println("Loan not found!");
            return;
        }

        System.out.println("\nLoan Details:");
        printLoanTable(Collections.singletonList(loan));

        // Check for associated payments
        if (hasAssociatedPayments(loanId)) {
            System.out.println("Cannot delete loan: Loan has associated payments.");
            return;
        }

        // Confirm deletion
        System.out.print("Are you sure you want to delete this loan? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM loans WHERE loan_id = ?")) {
            pstmt.setInt(1, loanId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Loan deleted successfully!");
                System.out.println("\nUpdated Loan List:");
                viewAllLoans();
            } else {
                System.out.println("Failed to delete loan.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting loan: " + e.getMessage());
        }
    }

    private static boolean hasAssociatedPayments(int loanId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM payments WHERE loan_id = ?")) {
            pstmt.setInt(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking associated payments: " + e.getMessage());
            return true; // Assume payments exist to prevent accidental deletion
        }
    }

    private static boolean hasPendingLoans(int clientId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM loans WHERE client_id = ? AND status = 'PENDING'")) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking pending loans: " + e.getMessage());
            return false;
        }
    }

    private static void approveRejectLoan() {
        System.out.println("\n--- Approve/Reject Loan ---");
        int loanId = getIntInput("Enter loan ID: ");

        Loan loan = findLoan(loanId);
        if (loan == null || !loan.getStatus().equals("PENDING")) {
            System.out.println("Loan not found or already processed!");
            return;
        }

        Client client = findClient(loan.getClientId());
        if (client == null) {
            System.out.println("Associated client not found!");
            return;
        }

        System.out.println("Loan Details:");
        printLoanTable(Collections.singletonList(loan));
        System.out.println("Client Details:");
        printClientTable(Collections.singletonList(client));

        if (client.getCreditScore() < MIN_CREDIT_SCORE_FOR_APPROVAL) {
            System.out.println("Cannot approve: Client credit score is below approval threshold.");
            updateLoanStatus(loanId, "REJECT");
            System.out.println("Loan automatically rejected.");
            loan.setStatus("REJECT");
            System.out.println("\nUpdated Loan Details:");
            printLoanTable(Collections.singletonList(loan));
            return;
        }

        System.out.println("1. Approve Loan");
        System.out.println("2. Reject Loan");
        int choice = getIntInput("Enter choice: ");

        String newStatus = (choice == 1) ? "APPROVED" : (choice == 2) ? "REJECT" : null;
        if (newStatus == null) {
            System.out.println("Invalid choice!");
            return;
        }

        updateLoanStatus(loanId, newStatus);
        System.out.println("Loan " + (choice == 1 ? "approved" : "rejected") + " successfully for " + loan.getClientName() + "!");
        loan.setStatus(newStatus);
        System.out.println("\nUpdated Loan Details:");
        printLoanTable(Collections.singletonList(loan));
    }

    private static void updateLoanStatus(int loanId, String status) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE loans SET status = ? WHERE loan_id = ?")) {
            pstmt.setString(1, status);
            pstmt.setInt(2, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating loan status: " + e.getMessage());
        }
    }

    private static void viewAllLoans() {
        System.out.println("\n--- All Loans ---");
        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT l.loan_id, l.client_id, l.amount, l.interest_rate, l.duration_months, " +
                             "l.status, l.issue_date, l.paid_amount, c.name AS client_name " +
                             "FROM loans l JOIN clients c ON l.client_id = c.id")) {
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("client_id"),
                        rs.getDouble("amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("duration_months"),
                        rs.getString("status"),
                        dateFormat.parse(rs.getString("issue_date")),
                        rs.getDouble("paid_amount"),
                        rs.getString("client_name")
                ));
            }
            printLoanTable(loans);
        } catch (SQLException | java.text.ParseException e) {
            System.err.println("Error retrieving loans: " + e.getMessage());
        }
    }

    private static void managePayments() {
        while (true) {
            System.out.println("\n***** PAYMENT PROCESSING *****");
            System.out.println("1. Process Payment");
            System.out.println("2. View Payment History");
            System.out.println("3. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1: processPayment(); break;
                case 2: viewPaymentHistory(); break;
                case 3: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void processPayment() {
        System.out.println("\n--- Process Payment ---");
        int loanId = getIntInput("Enter loan ID: ");

        Loan loan = findLoan(loanId);
        if (loan == null) {
            System.out.println("Loan not found!");
            return;
        }

        if (!loan.getStatus().equals("APPROVED")) {
            System.out.println("Loan is not approved or already paid/rejected!");
            return;
        }

        System.out.println("Loan Details:");
        printLoanTable(Collections.singletonList(loan));

        double outstanding = loan.calculateOutstandingBalance();
        System.out.printf("Current Outstanding Balance: %.2f%n", outstanding);

        double amount = getDoubleInput("Enter payment amount: ");
        if (amount <= 0) {
            System.out.println("Payment amount must be positive!");
            return;
        }
        if (amount > outstanding) {
            System.out.println("Payment amount cannot exceed outstanding balance!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement pstmtPayment = conn.prepareStatement(
                        "INSERT INTO payments (loan_id, amount, payment_date) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                pstmtPayment.setInt(1, loanId);
                pstmtPayment.setDouble(2, amount);
                pstmtPayment.setString(3, dateFormat.format(new Date()));
                pstmtPayment.executeUpdate();

                ResultSet rs = pstmtPayment.getGeneratedKeys();
                int paymentId = rs.next() ? rs.getInt(1) : -1;

                PreparedStatement pstmtLoan = conn.prepareStatement(
                        "UPDATE loans SET paid_amount = paid_amount + ? WHERE loan_id = ?");
                pstmtLoan.setDouble(1, amount);
                pstmtLoan.setInt(2, loanId);
                pstmtLoan.executeUpdate();

                double newPaidAmount = loan.getPaidAmount() + amount;
                double totalPayable = loan.calculateTotalPayable();
                if (newPaidAmount >= totalPayable) {
                    updateLoanStatus(loanId, "PAID");
                    System.out.println("Congratulations! Loan fully paid for " + loan.getClientName() + "!");
                    loan.setStatus("PAID");
                }

                loan.setPaidAmount(newPaidAmount);
                System.out.println("\nPayment Details:");
                printPaymentTable(Collections.singletonList(new Payment(paymentId, loanId, amount, new Date())));
                System.out.println("\nUpdated Loan Details:");
                printLoanTable(Collections.singletonList(loan));

                conn.commit();
                System.out.println("Payment processed successfully for " + loan.getClientName() + "!");
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error processing payment: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    private static void generateReports() {
        System.out.println("\n****** REPORTS ******");
        System.out.println("1. Client Report");
        System.out.println("2. Loan Portfolio");
        System.out.println("3. Collection Report");
        System.out.println("4. Detailed Client-Loan Report");
        System.out.println("5. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");
        switch (choice) {
            case 1: generateClientReport(); break;
            case 2: generateLoanPortfolio(); break;
            case 3: generateCollectionReport(); break;
            case 4: generateDetailedClientLoanReport(); break;
            case 5: return;
            default: System.out.println("Invalid choice!");
        }
    }

    private static void generateClientReport() {
        System.out.println("\n--- Client Report ---");
        viewAllClients();
    }

    private static void generateLoanPortfolio() {
        System.out.println("\n--- Loan Portfolio ---");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT SUM(amount) as total_approved, SUM(paid_amount) as total_paid, " +
                             "COUNT(*) as num_loans FROM loans WHERE status IN ('APPROVED', 'PAID')")) {
            if (rs.next()) {
                double totalApproved = rs.getDouble("total_approved");
                double totalPaid = rs.getDouble("total_paid");
                int numLoans = rs.getInt("num_loans");
                System.out.println("+-------------------------+----------------+");
                System.out.println("| Metric                  | Value          |");
                System.out.println("+-------------------------+----------------+");
                System.out.printf("| %-23s | %-14d |%n", "Number of Loans", numLoans);
                System.out.printf("| %-23s | %-14.2f |%n", "Total Approved Amount", totalApproved);
                System.out.printf("| %-23s | %-14.2f |%n", "Total Paid Amount", totalPaid);
                System.out.printf("| %-23s | %-14.2f |%n", "Total Outstanding", (totalApproved - totalPaid));
                System.out.println("+-------------------------+----------------+");
            } else {
                System.out.println("No approved or paid loans found.");
            }
        } catch (SQLException e) {
            System.err.println("Error generating loan portfolio: " + e.getMessage());
        }
    }

    private static void generateCollectionReport() {
        System.out.println("\n--- Collection Report ---");
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT p.payment_id, p.loan_id, p.amount, p.payment_date, c.name AS client_name, l.status " +
                             "FROM payments p JOIN loans l ON p.loan_id = l.loan_id JOIN clients c ON l.client_id = c.id")) {
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("loan_id"),
                        rs.getDouble("amount"),
                        dateFormat.parse(rs.getString("payment_date"))
                ));
            }
            printPaymentTable(payments);
        } catch (SQLException | java.text.ParseException e) {
            System.err.println("Error generating collection report: " + e.getMessage());
        }
    }

    private static void generateDetailedClientLoanReport() {
        System.out.println("\n--- Detailed Client-Loan Report ---");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT c.id, c.name, c.phone, c.address, c.credit_score, " +
                             "l.loan_id, l.amount, l.interest_rate, l.duration_months, l.status, l.issue_date, l.paid_amount " +
                             "FROM clients c LEFT JOIN loans l ON c.id = l.client_id ORDER BY c.id, l.loan_id")) {
            if (!rs.isBeforeFirst()) {
                System.out.println("No clients or loans found.");
                return;
            }
            int currentClientId = -1;
            List<Loan> clientLoans = new ArrayList<>();
            double totalLoans = 0.0;
            double totalPaid = 0.0;
            double totalOutstanding = 0.0;
            int loanCount = 0;

            while (rs.next()) {
                int clientId = rs.getInt("id");
                if (clientId != currentClientId) {
                    if (currentClientId != -1) {
                        System.out.println("\nLoans:");
                        printLoanTable(clientLoans);
                        printClientLoanSummary(totalLoans, totalPaid, totalOutstanding, loanCount);
                    }

                    currentClientId = clientId;
                    List<Client> clientList = Collections.singletonList(new Client(
                            clientId,
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getDouble("credit_score")
                    ));
                    System.out.println("\nClient Details:");
                    printClientTable(clientList);

                    clientLoans.clear();
                    totalLoans = 0.0;
                    totalPaid = 0.0;
                    totalOutstanding = 0.0;
                    loanCount = 0;
                }

                int loanId = rs.getInt("loan_id");
                if (!rs.wasNull()) {
                    Loan loan = new Loan(
                            loanId,
                            clientId,
                            rs.getDouble("amount"),
                            rs.getDouble("interest_rate"),
                            rs.getInt("duration_months"),
                            rs.getString("status"),
                            dateFormat.parse(rs.getString("issue_date")),
                            rs.getDouble("paid_amount"),
                            rs.getString("name")
                    );
                    clientLoans.add(loan);
                    totalLoans += loan.getAmount();
                    totalPaid += loan.getPaidAmount();
                    totalOutstanding += loan.calculateOutstandingBalance();
                    loanCount++;
                }
            }

            if (currentClientId != -1) {
                System.out.println("\nLoans:");
                printLoanTable(clientLoans);
                printClientLoanSummary(totalLoans, totalPaid, totalOutstanding, loanCount);
            }
        } catch (SQLException | java.text.ParseException e) {
            System.err.println("Error generating detailed report: " + e.getMessage());
        }
    }

    private static void printClientLoanSummary(double totalLoans, double totalPaid, double totalOutstanding, int loanCount) {
        System.out.println("\nClient Loan Summary:");
        System.out.println("+-------------------------+----------------+");
        System.out.println("| Metric                  | Value          |");
        System.out.println("+-------------------------+----------------+");
        System.out.printf("| %-23s | %-14d |%n", "Total Loans", loanCount);
        System.out.printf("| %-23s | %-14.2f |%n", "Total Amount", totalLoans);
        System.out.printf("| %-23s | %-14.2f |%n", "Total Paid", totalPaid);
        System.out.printf("| %-23s | %-14.2f |%n", "Total Outstanding", totalOutstanding);
        System.out.println("+-------------------------+----------------+");
    }

    private static Client findClient(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM clients WHERE id = ?")) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Client(rs.getInt("id"), rs.getString("name"),
                        rs.getString("phone"), rs.getString("address"),
                        rs.getDouble("credit_score"));
            }
        } catch (SQLException e) {
            System.err.println("Error finding client: " + e.getMessage());
        }
        return null;
    }

    private static boolean clientExists(int id) {
        return findClient(id) != null;
    }

    private static Loan findLoan(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT l.*, c.name AS client_name FROM loans l JOIN clients c ON l.client_id = c.id WHERE l.loan_id = ?")) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Loan(rs.getInt("loan_id"), rs.getInt("client_id"),
                        rs.getDouble("amount"), rs.getDouble("interest_rate"),
                        rs.getInt("duration_months"), rs.getString("status"),
                        dateFormat.parse(rs.getString("issue_date")), rs.getDouble("paid_amount"),
                        rs.getString("client_name"));
            }
        } catch (SQLException | java.text.ParseException e) {
            System.err.println("Error finding loan: " + e.getMessage());
        }
        return null;
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount!");
            }
        }
    }

    private static void viewClientLoans() {
        int clientId = getIntInput("Enter client ID: ");
        Client client = findClient(clientId);
        if (client == null) {
            System.out.println("Client not found!");
            return;
        }

        System.out.println("\n--- Loans for Client: " + client.getName() + " (ID: " + clientId + ") ---");
        printClientTable(Collections.singletonList(client));

        List<Loan> loans = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT l.*, c.name AS client_name FROM loans l JOIN clients c ON l.client_id = c.id WHERE l.client_id = ?")) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("client_id"),
                        rs.getDouble("amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("duration_months"),
                        rs.getString("status"),
                        dateFormat.parse(rs.getString("issue_date")),
                        rs.getDouble("paid_amount"),
                        rs.getString("client_name")
                ));
            }
            printLoanTable(loans);
            double totalOutstanding = loans.stream().mapToDouble(Loan::calculateOutstandingBalance).sum();
            System.out.printf("Total Outstanding for Client: %.2f%n", totalOutstanding);
        } catch (SQLException | java.text.ParseException e) {
            System.err.println("Error retrieving client loans: " + e.getMessage());
        }
    }

    private static void viewPaymentHistory() {
        int loanId = getIntInput("Enter loan ID: ");
        Loan loan = findLoan(loanId);
        if (loan == null) {
            System.out.println("Loan not found!");
            return;
        }

        System.out.println("\n--- Payment History for Loan ID: " + loanId + " (Client: " + loan.getClientName() + ") ---");
        printLoanTable(Collections.singletonList(loan));

        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM payments WHERE loan_id = ? ORDER BY payment_date")) {
            pstmt.setInt(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("loan_id"),
                        rs.getDouble("amount"),
                        dateFormat.parse(rs.getString("payment_date"))
                ));
            }
            printPaymentTable(payments);
            double totalPayments = payments.stream().mapToDouble(Payment::getAmount).sum();
            System.out.printf("Total Payments: %.2f%n", totalPayments);
        } catch (SQLException | java.text.ParseException e) {
            System.err.println("Error retrieving payment history: " + e.getMessage());
        }
    }
}