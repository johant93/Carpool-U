package johannt.carpool_2.Profile_Features;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import johannt.carpool_2.Login_Phase.SignInActivity;
import johannt.carpool_2.R;
import johannt.carpool_2.Rides_And_Validator.Carpool;
import johannt.carpool_2.Rides_And_Validator.Validator;
import johannt.carpool_2.Users.User;

public class PublishActivity extends AppCompatActivity  implements View.OnClickListener {

    private EditText editTextDate;
    private EditText editTextStartTime;
    private EditText editTextEndTime;
    private EditText editTextPrice;
    private Spinner spinnerSrc, spinnerDest , spinnerFreePlace;
    private Button addRideBtn;
    private ImageButton swapSrcDstBtn , calendarBtn;
    private String id, firstName, lastName, date, endTime, startTime, price, freeSits, src, dst,phoneNumber, calendarDate , UID;
    private int Day,Month,Year;
    private Carpool carpool;
    private boolean swap;
    private Calendar calendar;
    private User secondUser;
    private ProgressDialog progressDialog;
    private Date currentDate;

    //firebase object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabaseRides;
    private DatabaseReference firebaseDatabaseUsers;
    private FirebaseDatabase databaseCarPool;
    private FirebaseUser firebaseUser;
    Validator validator = new Validator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        swap = true;

        calendar = Calendar.getInstance();
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);


        // /getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseCarPool = FirebaseDatabase.getInstance();
        firebaseDatabaseRides = databaseCarPool.getReference("Rides");
        firebaseDatabaseUsers = databaseCarPool.getReference("Users");

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if (firebaseUser == null) {
            //close this activity
            finish();
            //opening SignIn activity
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
        else{
            id = firebaseDatabaseRides.push().getKey();
            secondUser= new User();

            firebaseDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                        secondUser = userSnapshot.getValue(User.class);
                        if(firebaseUser.getUid().equals(secondUser.getUID())){
                            firstName = secondUser.getFirstName();
                            lastName = secondUser.getLastName();
                            phoneNumber = secondUser.getPhoneNumber();
                            UID = secondUser.getUID();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.w("Failed to read value.", databaseError.toException());

                }
            });
        }


        editTextDate = findViewById(R.id.dateFieldAddRide);
        editTextEndTime = findViewById(R.id.arrivalTimeFieldAddRide);
        editTextStartTime = findViewById(R.id.pickupTimeFieldAddRide);
        editTextPrice = findViewById(R.id.priceFieldAddRide);
        spinnerSrc = findViewById(R.id.spinnerSrcAddRide);
        spinnerDest = findViewById(R.id.spinnerDestAddRide);
        spinnerFreePlace =findViewById(R.id.spinnerFreePlace);



        //attaching click listener
        addRideBtn = findViewById(R.id.addRideBtn);
        swapSrcDstBtn = findViewById(R.id.swapSrcDstBtn);
        calendarBtn = findViewById(R.id.calendarButton);
        addRideBtn.setOnClickListener(this);
        swapSrcDstBtn.setOnClickListener(this);
        calendarBtn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
            super.onStart();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String currDate = formatter.format(date);
            if(currDate.charAt(0) == '0' && currDate.charAt(3) == '0'){
                formatter.applyPattern("d/M/yyyy");
                currDate = formatter.format(date);
            }
            else if(currDate.charAt(0) == '0'){
                formatter.applyPattern("d/MM/yyyy");
                currDate = formatter.format(date);
            }
            else{
                formatter.applyPattern("dd/M/yyyy");
                currDate = formatter.format(date);

            }
            editTextDate.setText(currDate);
            setCityToUniversity();

            editTextStartTime.setText("7:00");
            editTextEndTime.setText("9:00");
            editTextPrice.setText(validator.setPrice(spinnerSrc.getSelectedItem().toString(),
                                                      spinnerDest.getSelectedItem().toString()));
            spinnerFreePlace.setSelection(getIndex(spinnerFreePlace, "3"));

    }

    /**
     * @param spinner
     * @param myString
     * @return the index of the item corresponding to myString
     */
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    public void setCityToUniversity(){
        spinnerSrc.setSelection(getIndex(spinnerSrc, ProfileActivity.city));
        spinnerDest.setSelection(getIndex(spinnerDest, ProfileActivity.university));
    }
    public void setUniversityToCity(){
        spinnerSrc.setSelection(getIndex(spinnerSrc, ProfileActivity.university));
        spinnerDest.setSelection(getIndex(spinnerDest, ProfileActivity.city));
    }

    // class addArideOnClickListener implements View.OnClickListener {
    // @Override
    public void onClick(View v) {


        if( v == calendarBtn ) {
            com.wdullaer.materialdatetimepicker.date.DatePickerDialog dialog =
                    com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
                        @Override

                        public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            calendarDate = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                            editTextDate.setText(calendarDate);
                        }
                    }, Year, Month, Day);

            Calendar minDate = calendar;
            minDate.setTime(calendar.getTime());
            dialog.setMinDate(minDate);
            dialog.show(getFragmentManager(), "DatePickerDialog");
        }
        if (v == swapSrcDstBtn) {

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                    R.array.city_array, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> universityAdapter = ArrayAdapter.createFromResource(this,
                    R.array.university_array, android.R.layout.simple_spinner_item);

            // Specify the layout to use when the list of choices appears
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            if (swap == true) {
                spinnerSrc.setAdapter(universityAdapter);
                spinnerDest.setAdapter(cityAdapter);
                setUniversityToCity();
                swap = false;
            } else {
                spinnerSrc.setAdapter(cityAdapter);
                spinnerDest.setAdapter(universityAdapter);
                setCityToUniversity();
                swap = true;
            }
        }

        if (v == addRideBtn) {
            date = editTextDate.getText().toString();
            startTime = editTextStartTime.getText().toString();
            endTime = editTextEndTime.getText().toString();
            price = editTextPrice.getText().toString();
            src = spinnerSrc.getSelectedItem().toString();
            dst = spinnerDest.getSelectedItem().toString();
            freeSits = spinnerFreePlace.getSelectedItem().toString();

            boolean checker = false;
            checker = validator.checkDate(date, this) &&
                    validator.checkdst(dst, this) &&
                    validator.checkSrc(src, this) &&
                    validator.checkPrice(price, this) &&
                    validator.checkTime(endTime, this) &&
                    validator.checkTime(startTime, this);

            if (checker) {
                progressDialog.setMessage("Loading Please Wait...");
                progressDialog.show();

                carpool = new Carpool(id,firstName , lastName, date, startTime, endTime, price, freeSits, src, dst, phoneNumber , UID);
                firebaseDatabaseRides.child(id).setValue(carpool);

                Toast.makeText(PublishActivity.this, "Ride added successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, ProfileActivity.class));
                progressDialog.dismiss();
                finish();
            }

        }
    }

//    public String EstimateArrival(String departure){
//        return (String.valueOf(Integer.parseInt(departure.substring(0,departure.indexOf(':')))+1)+":00");
//    }

}
