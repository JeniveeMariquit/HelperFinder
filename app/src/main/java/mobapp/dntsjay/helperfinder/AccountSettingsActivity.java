package mobapp.dntsjay.helperfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText txtFname;
    private EditText txtMname;
    private EditText txtLname;
    private EditText txtAddress;
    private EditText txtContact;
    private EditText txtPassword;
    private EditText txtConfirmPassword;

    public String AuthPassword;
    public String AuthUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmployerHomeActivity.class);
                startActivity(i);
            }
        });
        txtFname = findViewById(R.id.txtFname);
        txtMname = findViewById(R.id.txtMname);
        txtLname = findViewById(R.id.txtLname);
        txtAddress = findViewById(R.id.txtAddress);
        txtContact = findViewById(R.id.txtContactNumber);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        findViewById(R.id.btnSaveChanges).setOnClickListener(this);
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Firstname = dataSnapshot.child("Firstname").getValue().toString();
                String Middlename = dataSnapshot.child("Middlename").getValue().toString();
                String Lastname = dataSnapshot.child("Lastname").getValue().toString();
                String Address = dataSnapshot.child("Address").getValue().toString();
                String Contact = dataSnapshot.child("ContactNumber").getValue().toString();
                String Password = dataSnapshot.child("Password").getValue().toString();
                AuthPassword = dataSnapshot.child("Password").getValue().toString();
                AuthUserType = dataSnapshot.child("Usertype").getValue().toString();
                txtFname.setText(Firstname);
                txtMname.setText(Middlename);
                txtLname.setText(Lastname);
                txtAddress.setText(Address);
                txtContact.setText(Contact);
                txtPassword.setText(Password);
                txtConfirmPassword.setText(Password);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnSaveChanges){
            saveChanges(txtFname.getText().toString(),txtMname.getText().toString(),txtLname.getText().toString(),txtContact.getText().toString(),txtAddress.getText().toString(),txtPassword.getText().toString(), txtConfirmPassword.getText().toString());
        }
    }

    public void saveChanges(final String firstname, final String middlename, final String lastname, final String contactnumber, final String address, final String password, final String conpassword) {
        if (validateForm() == true) {
            final FirebaseUser user = mAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), AuthPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    User users = new User(firstname,middlename,lastname,contactnumber,address,user.getEmail(),password,user.getUid(),AuthUserType,false);
                                    mDatabase.child("users").child(user.getUid()).setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AccountSettingsActivity.this, "Successfully Updated Your Account.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent s = new Intent(AccountSettingsActivity.this, EmployerHomeActivity.class);
                                            startActivity(s);
                                            finish();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AccountSettingsActivity.this, "Failed to Create an Account.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Log.d("NGEK", "Password updated");
                                } else {
                                    Log.d("NGEK", "Error password not updated");
                                }
                            }
                        });
                    }
                }
            });

        } else {
//
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String password = txtPassword.getText().toString();
        String firstname = txtFname.getText().toString();
        String contactnumber = txtContact.getText().toString();
        String lastname = txtLname.getText().toString();
        String address = txtAddress.getText().toString();
        String conpass = txtConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(firstname) || TextUtils.isEmpty(lastname) || TextUtils.isEmpty(address) || TextUtils.isEmpty(contactnumber) || TextUtils.isEmpty(password) || TextUtils.isEmpty(conpass)) {
//            txtPassword.setError("Password is required.");
            Toast.makeText(AccountSettingsActivity.this, "Please input all fields!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtPassword.setError(null);
        }

        if (isValidPassword(txtPassword.getText().toString().trim())) {
//            txtPassword.setError(null);
        } else {
//            txtPassword.setError("Password must contain:\nAtleast one upper case character\nAtleast one lower case.\nAleast 6 characters");
            Toast.makeText(AccountSettingsActivity.this, "Password must contain:\\nAtleast one upper case character\\nAtleast one lower case.\\nAleast 6 characters", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (contactnumber.length() != 11 ){
//            txtContactNumber.setError("Please input a valid phone number.");
            Toast.makeText(AccountSettingsActivity.this, "Please input a valid phone number.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
//            txtContactNumber.setError(null);
        }

        if(TextUtils.equals(password,conpass)){

        }
        else{
            Toast.makeText(AccountSettingsActivity.this, "Password does not match! Please try again!", Toast.LENGTH_SHORT).show();
            valid = false;
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
}
