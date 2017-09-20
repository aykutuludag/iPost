package app.isend.model;

public class ContactItem {

    private String ID;
    private String name;
    private String phoneNumber;
    private String contactPhoto;
    private String whatsapp;
    private String messenger;
    private String mail;

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactPhoto() {
        return contactPhoto;
    }

    public void setContactPhoto(String contactPhoto) {
        this.contactPhoto = contactPhoto;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhastaspp(String whastaspp) {
        this.whatsapp = whastaspp;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}