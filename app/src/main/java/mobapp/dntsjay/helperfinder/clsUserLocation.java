package mobapp.dntsjay.helperfinder;

public class clsUserLocation {
    public String LatLng;
    public String Uid;
    public String UserLatitude;
    public String UserLongitude;

    public clsUserLocation() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public clsUserLocation(String latLng, String uid, String userLatitude, String userLongitude) {
        this.LatLng = latLng;
        this.Uid = uid;
        this.UserLatitude = userLatitude;
        this.UserLongitude = userLongitude;
    }
}
