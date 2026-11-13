import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Hotel Booking Management System.
 * Handles all console I/O and delegates business logic to {@link Hotel}.
 */
public class Main {

    // ── ANSI colour codes (auto-disabled on systems that don't support them) ─
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";
    private static final String BLUE   = "\u001B[34m";

    private static final Scanner scanner = new Scanner(System.in);
    private static final Hotel   hotel   = new Hotel();

    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        hotel.initializeRooms();
        printWelcome();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> viewAllRooms();
                case 2 -> viewAvailableRooms();
                case 3 -> bookRoom();
                case 4 -> cancelBooking();
                case 5 -> viewBookingDetails();
                case 6 -> { running = false; printGoodbye(); }
                default -> printError("Invalid option. Please enter a number between 1 and 6.");
            }
        }

        scanner.close();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Menu handlers
    // ═════════════════════════════════════════════════════════════════════════

    /** Option 1 – display every room regardless of availability. */
    private static void viewAllRooms() {
        printSectionHeader("ALL ROOMS");
        List<Room> rooms = hotel.getAllRooms();
        if (rooms.isEmpty()) {
            printInfo("No rooms configured.");
        } else {
            for (Room r : rooms) System.out.println("  " + r);
        }
        System.out.println();
    }

    /** Option 2 – display only rooms that can be booked right now. */
    private static void viewAvailableRooms() {
        printSectionHeader("AVAILABLE ROOMS");
        List<Room> available = hotel.getAvailableRooms();
        if (available.isEmpty()) {
            printInfo("No rooms are currently available.");
        } else {
            for (Room r : available) System.out.println("  " + r);
        }
        System.out.println();
    }

    /** Option 3 – collect guest details and create a booking. */
    private static void bookRoom() {
        printSectionHeader("BOOK A ROOM");

        // Show available rooms first so the user can choose
        List<Room> available = hotel.getAvailableRooms();
        if (available.isEmpty()) {
            printInfo("Sorry, no rooms are currently available. Please try again later.");
            System.out.println();
            return;
        }
        System.out.println("  Available rooms:");
        for (Room r : available) System.out.println("    " + r);
        System.out.println();

        // Collect input
        System.out.print("  Guest name         : ");
        String guestName = scanner.nextLine().trim();

        int roomNumber    = readInt("  Room number        : ");
        int numberOfNights = readInt("  Number of nights   : ");

        // Delegate to Hotel – catch all validation errors
        try {
            Booking booking = hotel.bookRoom(guestName, roomNumber, numberOfNights);
            printSuccess("Booking confirmed!");
            System.out.println();
            System.out.println(indent(booking.toString()));
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        }
        System.out.println();
    }

    /** Option 4 – cancel an active booking by ID. */
    private static void cancelBooking() {
        printSectionHeader("CANCEL A BOOKING");

        // Show active bookings for reference
        List<Booking> all = hotel.getAllBookings();
        List<Booking> active = all.stream().filter(Booking::isActive).toList();
        if (active.isEmpty()) {
            printInfo("There are no active bookings to cancel.");
            System.out.println();
            return;
        }
        System.out.println("  Active bookings:");
        for (Booking b : active) {
            System.out.printf("    [ID %d] Room %d – %s (%d night(s))%n",
                    b.getBookingId(), b.getRoomNumber(), b.getGuestName(), b.getNumberOfNights());
        }
        System.out.println();

        int bookingId = readInt("  Enter Booking ID to cancel: ");

        try {
            Booking cancelled = hotel.cancelBooking(bookingId);
            printSuccess("Booking #" + cancelled.getBookingId() + " cancelled successfully. "
                    + "Room " + cancelled.getRoomNumber() + " is now available.");
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        }
        System.out.println();
    }

    /** Option 5 – look up and display the full details of one booking. */
    private static void viewBookingDetails() {
        printSectionHeader("VIEW BOOKING DETAILS");

        List<Booking> all = hotel.getAllBookings();
        if (all.isEmpty()) {
            printInfo("No bookings have been made yet.");
            System.out.println();
            return;
        }

        // Let the user pick all or a specific booking
        System.out.println("  Options:");
        System.out.println("    0 – Show ALL bookings");
        System.out.println("    n – Enter a specific Booking ID");
        System.out.println();
        int input = readInt("  Enter 0 or a Booking ID: ");

        if (input == 0) {
            System.out.println();
            for (Booking b : all) {
                System.out.println(divider('-', 50));
                System.out.println(indent(b.toString()));
            }
            System.out.println(divider('-', 50));
        } else {
            Booking b = hotel.findBooking(input);
            if (b == null) {
                printError("No booking found with ID " + input + ".");
            } else {
                System.out.println();
                System.out.println(divider('-', 50));
                System.out.println(indent(b.toString()));
                System.out.println(divider('-', 50));
            }
        }
        System.out.println();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  Input helpers
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Reads a single integer from the console, consuming the newline afterwards.
     * Reprompts the user if the input is not a valid integer.
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine();   // discard trailing newline
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();   // discard the bad token
                printError("Please enter a whole number.");
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  UI helpers
    // ═════════════════════════════════════════════════════════════════════════

    private static void printWelcome() {
        System.out.println();
        System.out.println(BOLD + CYAN + divider('═', 56) + RESET);
        System.out.println(BOLD + CYAN + "   ★  HOTEL BOOKING MANAGEMENT SYSTEM  ★" + RESET);
        System.out.println(BOLD + CYAN + divider('═', 56) + RESET);
        System.out.println("   Welcome! Use the menu below to manage bookings.");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println(BOLD + BLUE + divider('─', 40) + RESET);
        System.out.println(BOLD + BLUE + "  MAIN MENU" + RESET);
        System.out.println(BOLD + BLUE + divider('─', 40) + RESET);
        System.out.println("  1. View all rooms");
        System.out.println("  2. View available rooms");
        System.out.println("  3. Book a room");
        System.out.println("  4. Cancel a booking");
        System.out.println("  5. View booking details");
        System.out.println("  6. Exit");
        System.out.println(BOLD + BLUE + divider('─', 40) + RESET);
    }

    private static void printSectionHeader(String title) {
        System.out.println(BOLD + YELLOW + "── " + title + " " + divider('─', 36 - title.length()) + RESET);
    }

    private static void printSuccess(String msg) {
        System.out.println(GREEN + "  ✔  " + msg + RESET);
    }

    private static void printError(String msg) {
        System.out.println(RED + "  ✖  ERROR: " + msg + RESET);
    }

    private static void printInfo(String msg) {
        System.out.println(YELLOW + "  ℹ  " + msg + RESET);
    }

    private static void printGoodbye() {
        System.out.println();
        System.out.println(BOLD + CYAN + divider('═', 56) + RESET);
        System.out.println(BOLD + CYAN + "   Thank you for using the Hotel Booking System!" + RESET);
        System.out.println(BOLD + CYAN + "   Goodbye! Have a wonderful stay." + RESET);
        System.out.println(BOLD + CYAN + divider('═', 56) + RESET);
        System.out.println();
    }

    private static String divider(char ch, int length) {
        return String.valueOf(ch).repeat(Math.max(0, length));
    }

    private static String indent(String text) {
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("\\r?\\n")) {
            sb.append("  ").append(line).append(System.lineSeparator());
        }
        return sb.toString().stripTrailing();
    }
}
