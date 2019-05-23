package mobapp.dntsjay.helperfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Spinner spinnerUserType;
    private EditText txtEmailAddress;
    private EditText txtPassword;
    private EditText txtFirstname;
    private EditText txtMiddlename;
    private EditText txtLastname;
    private EditText txtContactNumber;
    private EditText txtAddress;
    private static final String[] paths = {"Choose type of user..","EMPLOYER", "APPLICANT"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtFirstname = findViewById(R.id.txtFname);
        txtFirstname.requestFocus();
        txtMiddlename = findViewById(R.id.txtMname);
        txtLastname = findViewById(R.id.txtLname);
        txtContactNumber = findViewById(R.id.txtContactNumber);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmailAddress = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        spinnerUserType = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegistrationActivity.this,
                android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);
        spinnerUserType.setOnItemSelectedListener(this);

    }

    //These are the buttons onClick
    @Override
    public void onClick(View v) {

    }

    public void createAccount(View v){
        if(spinnerUserType.getSelectedItem().toString() == "Choose type of user.."){
            Toast.makeText(RegistrationActivity.this, "Please select a type of user first!", Toast.LENGTH_SHORT).show();
        }
        else{
            signUp(txtFirstname.getText().toString(),txtMiddlename.getText().toString(),txtLastname.getText().toString(),txtContactNumber.getText().toString(),txtAddress.getText().toString(),txtEmailAddress.getText().toString(),txtPassword.getText().toString(),spinnerUserType.getSelectedItem().toString());

        }
    }

    public void signUp(final String firstname, final String middlename, final String lastname, final String contactnumber, final String address, final String email, final String password, final  String usertype){
        if(validateForm() == true ){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                User_Rating ur = new User_Rating(0,0,0.00);
                                mDatabase.child("user_rating").child(user.getUid()).setValue(ur);
                                User users = new User(firstname,middlename,lastname,contactnumber,address,email,password,user.getUid(), usertype, false);
                                mDatabase.child("users").child(user.getUid()).setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegistrationActivity.this, "Successfully Created an Account.",
                                                Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        Intent s = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(s);
                                        finish();

                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener(){
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegistrationActivity.this, "Failed to Create an Account.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("USER", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Authentication failed:\n"+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        else{
//
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String email = txtEmailAddress.getText().toString();
        if (TextUtils.isEmpty(email)) {
//            txtEmailAddress.setError("Email address is required.");
            Toast.makeText(RegistrationActivity.this, "Email address is required.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtEmailAddress.setError(null);
        }

        if(!email.matches(emailPattern)){
//            txtEmailAddress.setError("Please input a valid email address.");
            Toast.makeText(RegistrationActivity.this, "Please input a valid email address.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtEmailAddress.setError(null);
        }

        String password = txtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
//            txtPassword.setError("Password is required.");
            Toast.makeText(RegistrationActivity.this, "Please input a valid email address.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtPassword.setError(null);
        }

        if (isValidPassword(txtPassword.getText().toString().trim())) {
//            txtPassword.setError(null);
        } else {
//            txtPassword.setError("Password must contain:\nAtleast one upper case character\nAtleast one lower case.\nAleast 6 characters");
            Toast.makeText(RegistrationActivity.this, "Password must contain:\\nAtleast one upper case character\\nAtleast one lower case.\\nAleast 6 characters", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        String contactnumber = txtContactNumber.getText().toString();
        if (TextUtils.isEmpty(contactnumber) || contactnumber.length() != 11 ){
//            txtContactNumber.setError("Please input a valid phone number.");
            Toast.makeText(RegistrationActivity.this, "Please input a valid phone number.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtContactNumber.setError(null);
        }


        String firstname = txtFirstname.getText().toString();
        if (TextUtils.isEmpty(firstname)) {
//            txtFirstname.setError("Firstname is required.");
            Toast.makeText(RegistrationActivity.this, "Firstname is required.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtFirstname.setError(null);
        }


        String lastname = txtLastname.getText().toString();
        if (TextUtils.isEmpty(lastname)) {
//            txtLastname.setError("Lastname is required.");
            Toast.makeText(RegistrationActivity.this, "Lastname is required.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtLastname.setError(null);
        }

        String address = txtAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
//            txtAddress.setError("Address is required.");
            Toast.makeText(RegistrationActivity.this, "Address is required.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtAddress.setError(null);
        }
        return valid;
    }
    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static class User {
        public String Firstname;
        public String Middlename;
        public String Lastname;
        public String ContactNumber;
        public String Address;
        public String Email;
        public String Password;
        public String Uid;
        public String Usertype;
        public Boolean WithAdditional;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String firstname, String middlename, String lastname, String contactNumber, String address, String email, String password, String uid, String usertype, Boolean withAdditional ) {
            this.Firstname = firstname;
            this.Middlename = middlename;
            this.Lastname = lastname;
            this.ContactNumber = contactNumber;
            this.Address = address;
            this.Email = email;
            this.Password = password;
            this.Uid = uid;
            this.Usertype = usertype;
            this.WithAdditional = withAdditional;

        }
    }

    public static class User_Rating {
        public Integer NumberOfRating;
        public Integer Rating;
        public Double TotalRating;

        public User_Rating() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User_Rating(Integer numberOfRating, Integer rating, Double totalRating) {
            this.NumberOfRating = numberOfRating;
            this.Rating = rating;
            this.TotalRating = totalRating;

        }
    }

    public void gotoLogin(View v){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
