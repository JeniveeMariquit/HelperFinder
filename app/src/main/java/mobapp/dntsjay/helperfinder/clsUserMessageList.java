package mobapp.dntsjay.helperfinder;

public class clsUserMessageList {
    public String MessageID;
    public String SentToID;
    public String SentByID;
    public String SentByName;

    public clsUserMessageList() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public clsUserMessageList(String messageID, String sentToID, String sentByID, String sentByName) {
        this.MessageID = messageID;
        this.SentToID = sentToID;
        this.SentByID = sentByID;
        this.SentByName = sentByName;
    }
}

