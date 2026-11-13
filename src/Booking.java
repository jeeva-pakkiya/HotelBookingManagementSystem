/**
 * Represents a hotel room booking.
 */
public class Booking {

    private static int idCounter = 1;   // Auto-incrementing booking ID seed

    private int    bookingId;
    private String guestName;
    private int    roomNumber;
    private int    numberOfNights;
    private double totalAmount;
    private boolean active;             // false once cancelled

    /**
     * Creates a new active booking and auto-generates a unique booking ID.
     *
     * @param guestName     name of the guest (must be non-blank)
     * @param roomNumber    room being booked
     * @param numberOfNights number of nights (must be >= 1)
     * @param pricePerNight  nightly rate used to compute the total
     */
    public Booking(String guestName, int roomNumber, int numberOfNights, double pricePerNight) {
        this.bookingId      = idCounter++;
        this.guestName      = guestName;
        this.roomNumber     = roomNumber;
        this.numberOfNights = numberOfNights;
        this.totalAmount    = pricePerNight * numberOfNights;
        this.active         = true;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getBookingId()       { return bookingId; }
    public String getGuestName()       { return guestName; }
    public int    getRoomNumber()      { return roomNumber; }
    public int    getNumberOfNights()  { return numberOfNights; }
    public double getTotalAmount()     { return totalAmount; }
    public boolean isActive()          { return active; }

    // ── Setter ────────────────────────────────────────────────────────────────

    public void setActive(boolean active) { this.active = active; }

    // ── Display ───────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
                "Booking ID : %d%n" +
                "Guest Name : %s%n" +
                "Room       : %d%n" +
                "Nights     : %d%n" +
                "Total      : $%.2f%n" +
                "Status     : %s",
                bookingId, guestName, roomNumber,
                numberOfNights, totalAmount,
                active ? "Active" : "Cancelled");
    }
}
