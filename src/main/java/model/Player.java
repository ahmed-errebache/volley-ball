package model;

import java.util.Objects;

public class Player {
    private final String firstName;
    private final String lastName;

    public Player(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player p = (Player) o;
        return firstName.equalsIgnoreCase(p.firstName)
                && lastName.equalsIgnoreCase(p.lastName);
    }

    @Override public int hashCode() {
        return Objects.hash(firstName.toLowerCase(), lastName.toLowerCase());
    }

    @Override public String toString() {
        return firstName + " " + lastName;
    }
}