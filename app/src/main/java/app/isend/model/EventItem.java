package app.isend.model;

public class EventItem {

    private String ID;
    private String title;
    private String photoURI;
    private String description;
    private long startTime;
    private long endTime;
    private String location;
    private String owner;
    private String background;
    private int isMailActive;
    private int isSMSActive;
    private int isMessengerActive;
    private int isWhatsappActive;

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photoURI;
    }

    public void setPhoto(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getIsMailActive() {
        return isMailActive;
    }

    public void setIsMailActive(int isMailActive) {
        this.isMailActive = isMailActive;
    }

    public int getIsSMSActive() {
        return isSMSActive;
    }

    public void setIsSMSActive(int isSMSActive) {
        this.isSMSActive = isSMSActive;
    }

    public int getIsMessengerActive() {
        return isMessengerActive;
    }

    public void setIsMessengerActive(int isMessengerActive) {
        this.isMessengerActive = isMessengerActive;
    }

    public int getIsWhatsappActive() {
        return isWhatsappActive;
    }

    public void setIsWhatsappActive(int isWhatsappActivee) {
        this.isWhatsappActive = isWhatsappActivee;
    }
}