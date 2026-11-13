import java.util.ArrayList;
import java.util.List;

/**
 * Core business logic: manages the room inventory and booking operations.
 * All validation rules are enforced here, keeping I/O in Main.
 */
public class Hotel {

    private List<Room>    rooms    = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Populates rooms 101–110 with a balanced mix of types and prices.
     */
    public void initializeRooms() {
        rooms.add(new Room(101, "Single",  89.99));
        rooms.add(new Room(102, "Single",  89.99));
        rooms.add(new Room(103, "Single",  94.99));
        rooms.add(new Room(104, "Double", 139.99));
        rooms.add(new Room(105, "Double", 139.99));
        rooms.add(new Room(106, "Double", 149.99));
        rooms.add(new Room(107, "Double", 154.99));
        rooms.add(new Room(108, "Suite",  249.99));
        rooms.add(new Room(109, "Suite",  279.99));
        rooms.add(new Room(110, "Suite",  319.99));
    }

    // ── Room queries ──────────────────────────────────────────────────────────

    /** Returns an unmodifiable view of all rooms. */
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    /** Returns only the rooms that are currently available. */
    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isAvailable()) available.add(r);
        }
        return available;
    }

    /**
     * Looks up a room by its number.
     *
     * @return the Room, or {@code null} if not found
     */
    public Room findRoom(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    // ── Booking operations ────────────────────────────────────────────────────

    /**
     * Books an available room.
     *
     * @param guestName     must be non-blank
     * @param roomNumber    must correspond to an existing, available room
     * @param numberOfNights must be >= 1
     * @return the created Booking
     * @throws IllegalArgumentException on any validation failure
     */
    public Booking bookRoom(String guestName, int roomNumber, int numberOfNights) {
        // ── Validate guest name ──────────────────────────────────────────────
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name cannot be blank.");
        }

        // ── Validate nights ──────────────────────────────────────────────────
        if (numberOfNights < 1) {
            throw new IllegalArgumentException(
                    "Number of nights must be at least 1 (got " + numberOfNights + ").");
        }

        // ── Find the room ────────────────────────────────────────────────────
        Room room = findRoom(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException(
                    "Room " + roomNumber + " does not exist.");
        }

        // ── Check availability ───────────────────────────────────────────────
        if (!room.isAvailable()) {
            throw new IllegalArgumentException(
                    "Room " + roomNumber + " is already booked.");
        }

        // ── Create booking ───────────────────────────────────────────────────
        Booking booking = new Booking(
                guestName.trim(), roomNumber, numberOfNights, room.getPricePerNight());
        bookings.add(booking);
        room.setAvailable(false);
        return booking;
    }

    /**
     * Cancels an active booking by ID, freeing the associated room.
     *
     * @param bookingId must correspond to an existing, active booking
     * @return the cancelled Booking
     * @throws IllegalArgumentException if the booking is not found or already cancelled
     */
    public Booking cancelBooking(int bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                if (!b.isActive()) {
                    throw new IllegalArgumentException(
                            "Booking " + bookingId + " has already been cancelled.");
                }
                b.setActive(false);
                Room room = findRoom(b.getRoomNumber());
                if (room != null) room.setAvailable(true);
                return b;
            }
        }
        throw new IllegalArgumentException(
                "No booking found with ID " + bookingId + ".");
    }

    /**
     * Retrieves a specific booking by ID.
     *
     * @return the Booking, or {@code null} if not found
     */
    public Booking findBooking(int bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) return b;
        }
        return null;
    }

    /** Returns all bookings (active + cancelled). */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}
