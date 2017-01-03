package de.bischinger.buchungstool.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bischofa on 28/06/16.
 */
public class Schedule implements Serializable {
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
