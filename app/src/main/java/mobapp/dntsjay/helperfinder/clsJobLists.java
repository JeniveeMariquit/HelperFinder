package mobapp.dntsjay.helperfinder;

public class clsJobLists {
    public String LookingFor;
    public String RatePerDay;
    public String JobDescription;
    public String Qualification;
    public String Location;
    public String DatePosted;
    public Integer JobID;
    public String Uid;

    public clsJobLists(){

    }

    public clsJobLists(String lookingFor, String ratePerDay, String jobDescription, String qualification, String location, String datePosted, Integer jobID, String uid) {
        this.LookingFor = lookingFor;
        this.RatePerDay = ratePerDay;
        this.JobDescription = jobDescription;
        this.Qualification = qualification;
        this.Location = location;
        this.DatePosted = datePosted;
        this.JobID = jobID;
        this.Uid = uid;

    }
}
