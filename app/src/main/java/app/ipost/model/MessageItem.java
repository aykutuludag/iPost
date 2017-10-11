package app.ipost.model;

public class MessageItem {

    private int ID;
    private String senderMail;
    private String receiverName;
    private String receiverMail;
    private String receiverPhone;
    private long sendingTime;
    private boolean isSuccess;

    // Mail components
    private String mailTitle;
    private String mailContent;
    private String mailAttachment;

    // SMS components
    private String smsContent;

    // FB Messenger components
    private String messengerContent;
    private String messengerAttachment;

    // Whatsapp components
    private String whatsappContent;
    private String whatsappAttachment;

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public String senderMail() {
        return senderMail;
    }

    public void setsenderMail(String senderMail) {
        this.senderMail = senderMail;
    }
}