package app.ipost.model;

public class PostItem {

    private int ID;
    private String receiverName;
    private String receiverMail;
    private String receiverPhone;
    private long postTime;
    private int isSuccess;

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

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMail() {
        return receiverMail;
    }

    public void setReceiverMail(String receiverMail) {
        this.receiverMail = receiverMail;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMailTitle() {
        return mailTitle;
    }

    public void setMailTitle(String mailTitle) {
        this.mailTitle = mailTitle;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String getMailAttachment() {
        return mailAttachment;
    }

    public void setMailAttachment(String mailAttachment) {
        this.mailAttachment = mailAttachment;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getMessengerContent() {
        return mailTitle;
    }

    public void setMessengerContent(String messengerContent) {
        this.messengerContent = messengerContent;
    }

    public String getMessengerAttachment() {
        return messengerAttachment;
    }

    public void setMessengerAttachment(String messengerAttachment) {
        this.messengerAttachment = messengerAttachment;
    }

    public String getWhatsappContent() {
        return whatsappContent;
    }

    public void setWhatsappContent(String whatsappContent) {
        this.whatsappContent = whatsappContent;
    }

    public String getWhatsappAttachment() {
        return whatsappAttachment;
    }

    public void setWhatsappAttachment(String whatsappAttachment) {
        this.whatsappAttachment = whatsappAttachment;
    }
}