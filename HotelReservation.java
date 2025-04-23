package project;

import java.io.*; // we use this for the access of input and output to read and write files. 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // for handling date error 
import java.util.ArrayList; // inventory file for room availability
import java.util.Scanner;

/**
 * Abstract Room class shares properties and behaviors such as room number, rate, and availability.
 * Contains the abstract method `getRoomDetails()` for subclass-specific details.
 */
abstract class Room { // Parent class
    private final String roomNumber; // Unique room number
    private final double rate; // Room rate per night
    private boolean isAvailable = true; // Room availability status

    // Constructor to initialize room number and rate
    public Room(String roomNumber, double rate) {
        this.roomNumber = roomNumber;
        this.rate = rate;
    }

    // Getter for room number
    public String getRoomNumber() {
        return roomNumber;
    }

    // Getter for room rate
    public double getPrice() {
        return rate;
    }

    // Getter for availability status
    public boolean isAvailable() {
        return isAvailable;
    }

    // Setter for availability status
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Abstract method to get room-specific details
    public abstract String getRoomDetails();
}

/**
 * StandardRoom class represents standard rooms.
 * Extends the abstract Room class and sets a fixed price for this room type.
 */
class StandardRoom extends Room {
    public StandardRoom(String roomNumber) {
        super(roomNumber, 1945); // Fixed price for Standard Room
    }

    @Override 
    public String getRoomDetails() { //specific details of the room
        return "\nRoom Type: Standard Room" +
               "\nRoom No: " + getRoomNumber() +
               "\nRoom Rate: PHP " + getPrice() + "\n";
    }
}

/**
 * ClassicRoom class represents classic rooms.
 * Extends the abstract Room class and sets a fixed price for this room type.
 */
class ClassicRoom extends Room {
    public ClassicRoom(String roomNumber) {
        super(roomNumber, 2200); // Fixed price for Classic Room
    }

    @Override
    public String getRoomDetails() {
        return "Room Type: Classic Room" +
               "\nRoom No: " + getRoomNumber() +
               "\nRoom Rate: PHP " + getPrice() + "\n";
    }
}

/**
 * DeluxeRoom class represents deluxe rooms.
 * Extends the abstract Room class and sets a fixed price for this room type.
 */
class DeluxeRoom extends Room {
    public DeluxeRoom(String roomNumber) {
        super(roomNumber, 2980); // Fixed price for Deluxe Room
    }

    @Override
    public String getRoomDetails() {
        return "Room Type: Deluxe Room" +
               "\nRoom No: " + getRoomNumber() +
               "\nRoom Rate: PHP " + getPrice() + "\n";
    }
}

/**
 * FamilyRoom class represents family rooms.
 * Extends the abstract Room class and sets a fixed price for this room type.
 */
class FamilyRoom extends Room {
    public FamilyRoom(String roomNumber) {
        super(roomNumber, 4200); // Fixed price for Family Room
    }

    @Override
    public String getRoomDetails() {
        return "Room Type: Family Room" +
               "\nRoom No: " + getRoomNumber() +
               "\nRoom Rate: PHP " + getPrice() + "\n";
    }
}

/**
 * Guest class encapsulates guest information.
 * Stores details such as name, contact number, email, and special requests. 
 */
class Guest { //for confidentiality of the guest 
    private final String guestName; // Guest's full name 
    private final String contactNumber; // Guest's contact number
    private final String email; // Guest's email
    private final String specialRequest; // Any special requests from the guest

    // Constructor to initialize guest details 
    public Guest(String guestName, String contactNumber, String email, String specialRequest) {
        this.guestName = guestName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.specialRequest = specialRequest;
    }

    // Getter for guest name
    public String getName() {
        return guestName;
    }

    // Getter for contact number
    public String getContactNumber() {
        return contactNumber;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Getter for special requests. Uniqueness of the system. .
    public String getSpecialRequest() {
        return specialRequest;
    }
}

/**
 * Reservation class handles the booking details.
 * Includes check-in, check-out dates, room details, total cost calculations, and VAT.
 */
class Reservation { 
    private final Room room; // Reserved room
    final LocalDate checkInDate; // Check-in date
    final LocalDate checkOutDate; // Check-out date
    private final int nightsStayed; // Number of nights the guest will stay
    private final double totalPrice; // Total price before tax
    private final double VATRate; // VAT amount (12% of subtotal)

    // Constructor to initialize reservation details
    public Reservation(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nightsStayed = (int) checkInDate.until(checkOutDate).getDays(); // Calculate nights
        double basePrice = room.getPrice() * nightsStayed; // Calculate subtotal
        this.totalPrice = basePrice;
        this.VATRate = totalPrice * 0.12; // Calculate VAT
    }

    // Getter for reserved room
    public Room getRoom() {
        return room;
    }

    // Getter for number of nights stayed
    public int getNightsStayed() {
        return nightsStayed;
    }

    // Getter for VAT amount
    public double getTaxAmount() {
        return VATRate;
    }

    // Getter for total price including tax
    public double getTotalWithTax() {
        return totalPrice + VATRate;
    }

    // Getter for subtotal (before tax)
    public double getSubtotal() {
        return totalPrice;
    }
}
/**
 * Main HotelReservation class.
 * Provides an interface for reserving rooms and managing the hotel inventory.
 */
public class HotelReservation {
    private final Scanner scanner = new Scanner(System.in);
    
    // Static list of all available rooms
    private static final ArrayList<Room> rooms = new ArrayList<>();
    
    // List of reservations made in the current session
    private final ArrayList<Reservation> reservations = new ArrayList<>();
    
    // Details of the current guest making a reservation
    private Guest guest = null;
    
    // Starting receipt number
    private static int receiptNumber = 1001;

    // File paths for saving/loading data
    private static final String INVENTORY_FILE = "Room Availability.txt";
    private static final String RESERVATIONS_FILE = "reservation.txt";

    // Static block to initialize room inventory from file at startup
    static {
        loadInventory(); 
    }

   
    public static void loadInventory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");  // Split line into room details
                String roomNumber = details[0];
                String type = details[1];
                boolean isAvailable = Boolean.parseBoolean(details[2]);

                Room room;
                // Create different room objects based on the room type
                switch (type) {
                    case "Standard" -> room = new StandardRoom(roomNumber);
                    case "Classic" -> room = new ClassicRoom(roomNumber);
                    case "Deluxe" -> room = new DeluxeRoom(roomNumber);
                    case "Family" -> room = new FamilyRoom(roomNumber);
                    default -> throw new IllegalArgumentException("Unknown room type: " + type);
                }
                room.setAvailable(isAvailable);  // Set the availability status
                rooms.add(room);  // Add the room to the list of rooms
            }
        } catch (IOException e) {
            System.out.println("No inventory file found. Loading default inventory...");
            initializeDefaultInventory();  // If the file doesn't exist, load default rooms
        }
    }

    public static void initializeDefaultInventory() {
        // Add rooms to the inventory with default room numbers
        rooms.add(new StandardRoom("01"));
        rooms.add(new StandardRoom("02"));
        rooms.add(new StandardRoom("03"));
        rooms.add(new ClassicRoom("04"));
        rooms.add(new ClassicRoom("05"));
        rooms.add(new ClassicRoom("06"));
        rooms.add(new DeluxeRoom("07"));
        rooms.add(new DeluxeRoom("08"));
        rooms.add(new DeluxeRoom("09"));
        rooms.add(new FamilyRoom("10"));
        rooms.add(new FamilyRoom("11"));
        rooms.add(new FamilyRoom("12"));
        saveInventory();  // Save the default inventory to file
    }

    public static void saveInventory() { //to make sure that the room availability is not available after the reservation.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            // Write each room's details to the inventory file
            for (Room room : rooms) {
                writer.write(room.getRoomNumber() + "," + room.getClass().getSimpleName().replace("Room", "") + "," + room.isAvailable());
                writer.newLine();  // Write a new line after each room's details
            }
        } catch (IOException e) { 
            System.out.println("Error saving inventory: " + e.getMessage()); //It will print if there is an issue saving the reservation, such as lacking permission to write to the file.
        } 
    } 

    // Display room types and their rates to the user
    public void start() {
        System.out.println("\t\t\t- Welcome to Aerostop Hotel Reservation System -");
        System.out.println("\nRoom Types and Rates:");
        System.out.println("[1] STANDARD: ₱1,945 per night "+"\n - for 1 - 2 pax with 2 single bed"+"\n - 24 Hours Wifi"+ "\n - Fitness Center"
        + "\n - Swimming pool"+"\n - Free breakfast");
        System.out.println("\n[2] CLASSIC: ₱2,200 per night "+ "\n - solo or 2 with Queen bed"+
          "\n - 24 Hours Wifi"+ "\n - Fitness Center"+ "\n - Swimming pool"+ "\n - Free breakfast");
        System.out.println("\n[3] DELUXE: ₱2,980 per night \n - Perfect for 2 - 4 pax \n - 24 Hours Wifi "
                + "       \n - Fitness Center \n - Swimming pool\n - Free breakfast");
        System.out.println("\n[4] FAMILY: ₱4,200 per night \n - Up to 6 pax \n - 24 Hours Wifi "
                + "       \n - Fitness Center \n - Swimming pool \n - Free breakfast \n - Free Access to the gym");

        // Ask user if they want to reserve a room
        if (!askYesNo("\n---------------------------------------------------"+
                      "\nDo you want to reserve a room? (Y/N): "))
        {
            System.out.println("Exiting reservation process.\n");
            return;
        }

        boolean continueReserving = true;

        // Loop to allow multiple reservations
        while (continueReserving) { 
            displayAvailableRooms();  // Display available rooms
            Room selectedRoom = selectRoom();  // Let the user select a room
            LocalDate checkInDate = getValidCheckInDate();  // Get valid check-in date
            LocalDate checkOutDate = checkInDate.plusDays(getNightsStayed());  // Calculate check-out date

            // If no guest is set, ask for guest details
            if (guest == null) {
                guest = new Guest(
                        getInput("\nEnter your name: "),
                        getValidContactNumber(),
                        getValidEmail(),
                        getInput("\nDo you have any special requests? \n(ex: Wheelchair accessible room: ) ")
                );
            }

            // Confirm reservation
            if (askYesNo("\n---------------------------------------------------------"+"\nDo you want to confirm your reservation? (Y/N): ") )
            {
                reservations.add(new Reservation(selectedRoom, checkInDate, checkOutDate));  // Add reservation
                selectedRoom.setAvailable(false);  // Mark room as unavailable
                saveInventory();  // Save updated inventory
                printReservationSummary(selectedRoom);  // Print reservation summary
                continueReserving = askYesNo("\n---------------------------------------------------------" + 
                "\nDo you want to reserve more rooms? (Y/N): " );
                System.out.println("---------------------------------------------------------");
            } else {
                System.out.println("\nReservation canceled. Starting over...\n");
            }
        }

        // If there are reservations, print the official receipt
        if (!reservations.isEmpty()) {
            printOfficialReceipt();
            System.out.println("\nThank you for choosing Aerostop Hotel! We look forward to your stay.\n-------------------------------------------------\n\n");
        }
    } 

    // Save reservation details to a file
    private void saveReservationToFile(Reservation reservation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATIONS_FILE, true))) {
            // Write reservation details to the file
            writer.write("Receipt No.: " + receiptNumber + "\n");
            writer.write("Date: " + LocalDate.now() + "\n");
            writer.write("Guest: " + guest.getName() + "\n");
            writer.write("Contact: " + guest.getContactNumber() + "\n");
            writer.write("Email: " + guest.getEmail() + "\n");
            writer.write("Special Request: " + guest.getSpecialRequest() + "\n");
            writer.write("Room: " + reservation.getRoom().getRoomDetails() + "\n");
            writer.write("Check-in: " + reservation.checkInDate + "\n");
            writer.write("Check-out: " + reservation.checkOutDate + "\n");
            writer.write("Nights stayed: " + reservation.getNightsStayed() + "\n");
            writer.write("Subtotal: PHP " + reservation.getSubtotal() + "\n");
            writer.write("Tax (12%): PHP " + reservation.getTaxAmount() + "\n");
            writer.write("Total Amount: PHP " + reservation.getTotalWithTax() + "\n");
            writer.write("----------------------------------------\n");
        } catch (IOException e) {
            System.err.println("Error saving reservation: " + e.getMessage()); 
        }
    }

    // Display available rooms to the user
    private void displayAvailableRooms() {
        System.out.println("---------------------------------------------------" + "\n\n\t\t   Available Rooms:" + 
                           "\n---------------------------------------------------");
        for (Room room : rooms) {
            if (room.isAvailable()) {  // Only show available rooms
                System.out.println(room.getRoomDetails());
            }
        }
    }

    // Let the user select a room
    private Room selectRoom() {
        Room selectedRoom = null;
        while (selectedRoom == null) {
            String roomNumber = getInput("---------------------------------------------------\n\nEnter room number to reserve: ");
            for (Room room : rooms) {
                if (room.getRoomNumber().equalsIgnoreCase(roomNumber) && room.isAvailable()) {
                    selectedRoom = room;  // Room selected
                    break;
                }
            }
            if (selectedRoom == null) {
                System.out.println("Invalid room number or room is not available. Try again.");
            }
        }
        return selectedRoom;
    }

    // Get a valid check-in date from the user
    private LocalDate getValidCheckInDate() {
        while (true) {
            try {
                String dateInput = getInput("\n---------------------------------------------------\n\nEnter check-in date (YYYY-MM-DD): ");
                LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
                if (!date.isBefore(LocalDate.now())) {
                    return date;  // Return valid date
                } else {
                    System.out.println("Check-in date cannot be in the past.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Try again.");
            }
        }
    }

    // Get the number of nights the guest will stay
    private int getNightsStayed() {
        while (true) {
            try {
                int nights = Integer.parseInt(getInput("Enter number of nights: "));
                if (nights > 0) {
                    return nights;  // Return valid number of nights
                } else {
                    System.out.println("Number of nights must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid number.");
            }
        }
    }

    // Validate the contact number entered by the guest
    private String getValidContactNumber() {
        while (true) {
            String contactNumber = getInput("\nEnter your contact number: ");
            if (contactNumber.matches("\\d{11}")) {
                return contactNumber;  // Return valid contact number
            } else {
                System.out.println("Invalid contact number. Must be 11 digits.");
            }
        }
    }

    // Validate the email entered by the guest
    private String getValidEmail() {
        while (true) {
            String email = getInput("Enter your email: ");
            if (email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                return email;  // Return valid email
            } else {
                System.out.println("Invalid email format. Try again.");
            }
        }
    }

    // Print the reservation summary for the guest
    private void printReservationSummary(Room room) {
        Reservation reservation = reservations.get(reservations.size() - 1);  // Get last reservation
        System.out.println("\n=========================================================");
        System.out.println("                         Invoice ");
        System.out.println("=========================================================");
        System.out.println("\nGuest: " + guest.getName());
        System.out.println("\nRoom Details");
        System.out.println(room.getRoomDetails());
        System.out.println("Check-in Date: " + reservation.checkInDate);
        System.out.println("Check-out Date: " + reservation.checkOutDate);
        System.out.println("Special Request: " + guest.getSpecialRequest());
        System.out.println("Nights stayed: " + reservation.getNightsStayed());
        System.out.println("Subtotal: PHP " + reservation.getSubtotal());
        System.out.println("VATRate (12%): PHP " + reservation.getTaxAmount());
        System.out.println("Total Amount: PHP " + reservation.getTotalWithTax());

        saveReservationToFile(reservation);  // Save reservation details to a file
    }

    // Print the official receipt after completing the reservation
    private void printOfficialReceipt() {
        System.out.println("\n\n                ---- Aerostop Hotel ----");
        System.out.println("=========================================================");
        System.out.println("\n                    Official Receipt\n");
        System.out.println("=========================================================");
        System.out.println("Receipt No.: " + receiptNumber);
        System.out.println("Reservation Date: " + LocalDate.now());
        System.out.println("Guest Name: " + guest.getName());

        // Print details of all reservations
        for (Reservation reservation : reservations) {
            System.out.println("Room Details: ");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
            System.out.println(reservation.getRoom().getRoomDetails());
            System.out.println("Nights stayed: " + reservation.getNightsStayed());
            System.out.println("Check-in Date: " + reservation.checkInDate);
            System.out.println("Check-out Date: " + reservation.checkOutDate);
            System.out.println("Special Request: " + guest.getSpecialRequest());
            System.out.println("Subtotal: PHP " + reservation.getSubtotal());
            System.out.println("VATRate (12%): PHP " + reservation.getTaxAmount());
            System.out.println("\nTotal Amount: PHP " + reservation.getTotalWithTax());
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
            System.out.println("Amount Paid: PHP " + reservation.getTotalWithTax());
        }
        receiptNumber++;  // Increment receipt number
    }

    // Helper method to ask the user a Yes/No question
    private boolean askYesNo(String question) { 
        while (true) {
            String response = getInput(question);
            if (response.equalsIgnoreCase("Y")) {
                return true;  // User confirmed
            } else if (response.equalsIgnoreCase("N")) {
                return false;  // User canceled
            } else {
                System.out.println("Invalid input. Enter Y or N.");
            }
        }
    }

    // Helper method to get input from the user
    private String getInput(String user) { 
        System.out.print(user);
        return scanner.nextLine();
    }

    // Main method to start the program
    public static void main(String[] args) {
       for(int i = 0; ; i++) {
    	   new HotelReservation().start();
       }
    }
}