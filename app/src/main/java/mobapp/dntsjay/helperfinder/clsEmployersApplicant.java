package mobapp.dntsjay.helperfinder;

public class clsEmployersApplicant {
    public String ApplicantID;
    public String DateApplied;
    public Integer JobID;
    public String JobOwnerID;
    public String Status;
    public String NameofApplicant;

    public clsEmployersApplicant() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public clsEmployersApplicant(String applicantID, String dateApplied, Integer jobID, String jobOwnerID, String status, String nameofApplicant) {
        this.ApplicantID = applicantID;
        this.DateApplied = dateApplied;
        this.JobID = jobID;
        this.JobOwnerID = jobOwnerID;
        this.Status = status;
        this.NameofApplicant = nameofApplicant;
    }
}
