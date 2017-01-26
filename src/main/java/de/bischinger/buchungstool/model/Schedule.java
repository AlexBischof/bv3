package de.bischinger.buchungstool.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

/**
 * Created by bischofa on 28/06/16.
 */
@Entity
public class Schedule extends RootPojo {

    private static final long serialVersionUID = -7533486297869240859L;

    @OneToMany(cascade = { PERSIST, REMOVE }, fetch = EAGER)
    private List<Booking> bookingList = new ArrayList<>();

    public Schedule addBooking(LocalTime from, LocalTime to, BookingTyp[] typ) {
        bookingList.add(new Booking(from, to, typ));
        return this;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void addBookingList(List<Booking> bookingList){
        this.bookingList.addAll(bookingList);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "bookingList=" + bookingList +
                '}';
    }
}
