/**
 * Represents a hotel room with its attributes.
 */
public class Room {

    private int roomNumber;
    private String roomType;
    private double pricePerNight;
    private boolean isAvailable;

    /**
     * Constructs a Room with the given details.
     *
     * @param roomNumber   unique room identifier
     * @param roomType     type of room (e.g. Single, Double, Suite)
     * @param pricePerNight nightly rate in USD
     */
    public Room(int roomNumber, String roomType, double pricePerNight) {
        this.roomNumber   = roomNumber;
        this.roomType     = roomType;
        this.pricePerNight = pricePerNight;
        this.isAvailable  = true;   // All rooms start available
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getRoomNumber()     { return roomNumber; }
    public String getRoomType()    { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable()   { return isAvailable; }

    // ── Setter ───────────────────────────────────────────────────────────────

    public void setAvailable(boolean available) { this.isAvailable = available; }

    // ── Display ───────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Booked";
        return String.format("Room %-4d | %-8s | $%-8.2f/night | %s",
                roomNumber, roomType, pricePerNight, status);
    }
}
