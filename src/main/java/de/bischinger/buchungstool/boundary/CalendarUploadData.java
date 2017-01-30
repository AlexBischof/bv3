package de.bischinger.buchungstool.boundary;

import javax.ws.rs.FormParam;

/**
 * Created by bischofa on 30/01/17.
 */
public class CalendarUploadData {
    @FormParam("pausencalculation")
    private String pausencalculation;

    @FormParam("file")
    private byte[] file;

    public String getPausencalculation() {
        return pausencalculation;
    }

    public void setPausencalculation(String pausencalculation) {
        this.pausencalculation = pausencalculation;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
