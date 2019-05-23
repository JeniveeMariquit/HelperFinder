package mobapp.dntsjay.helperfinder;

//public class clsMessageList {
//    public String OwnRecNo;
//    public Integer MessageNumber;
//    public String DateSent;
//    public String Content;
//    public String SentByName;
//    public String SentByID;
//
//    public clsMessageList() {
//        // Default constructor required for calls to DataSnapshot.getValue(User.class)
//    }
//
//    public clsMessageList(String ownRecNo, Integer messageNumber, String dateSent, String content, String sentByID, String sentByName) {
//
//        this.OwnRecNo = ownRecNo;
//        this.MessageNumber = messageNumber;
//        this.DateSent = dateSent;
//        this.Content = content;
//        this.SentByName = sentByName;
//        this.SentByID = sentByID;
//    }
//}

public class clsMessageList {
    private String OwnRecNo;
    private Integer MessageNumber;
    private String DateSent;
    private String Content;
    private String SentByName;
    private String SentByID;

    public String getOwnRecNo() {
        return OwnRecNo;
    }

    public void setOwnRecNo(String ownRecNo) {
        this.OwnRecNo = ownRecNo;
    }

    public Integer getMessageNumber() {
        return MessageNumber;
    }

    public void setMessageNumber(Integer messageNumber) {
        this.MessageNumber = messageNumber;
    }

    public String getDateSent() {
        return DateSent;
    }

    public void setDateSent(String dateSent) {
        this.DateSent = dateSent;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public String getSentByName() {
        return SentByName;
    }

    public void setSentByName(String sentByName) {
        this.SentByName = sentByName;
    }

    public String getSentByID() {
        return SentByID;
    }

    public void setSentByID(String sentByID) {
        this.SentByID = sentByID;
    }

//    public clsMessageList() {
//        // Default constructor required for calls to DataSnapshot.getValue(User.class)
//    }
//
//    public clsMessageList(String ownRecNo, Integer messageNumber, String dateSent, String content, String sentByID, String sentByName) {
//
//        this.OwnRecNo = ownRecNo;
//        this.MessageNumber = messageNumber;
//        this.DateSent = dateSent;
//        this.Content = content;
//        this.SentByName = sentByName;
//        this.SentByID = sentByID;
//    }
}
