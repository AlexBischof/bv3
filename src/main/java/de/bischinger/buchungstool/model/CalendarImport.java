package de.bischinger.buchungstool.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Date;

/**
 * Created by Alex Bischof on 23.01.2017.
 */
@Entity
public class CalendarImport extends RootPojo {
    private static final long serialVersionUID = 7601741731159213692L;

    @Basic(optional = false)
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Lob
    private byte[] filedata;

    private boolean sommer;

    @Basic(optional = false)
    private Date createdOn;

    public CalendarImport() {
    }

    public CalendarImport(String fileName, byte[] content, boolean sommer) {
        createdOn = new Date();
        this.fileName = fileName;
        this.filedata = content;
        this.sommer = sommer;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFiledata() {
        return filedata;
    }

    public void setFiledata(byte[] filedata) {
        this.filedata = filedata;
    }

    public boolean isSommer() {
        return sommer;
    }

    public void setSommer(boolean sommer) {
        this.sommer = sommer;
    }

    @Override
    public String toString() {
        return "CalendarImport{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}
