package johannt.carpool_2.Profile_Features;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import johannt.carpool_2.Rides_And_Validator.Carpool;
import johannt.carpool_2.Rides_And_Validator.RideHistoryAdapter;
import johannt.carpool_2.R;

public class RidesHistory extends AppCompatActivity {


    private ListView carpoolListViewHistory;

    private Carpool ride;
    private List<Carpool> carpoolListHistory;
    private ProgressDialog progressDialog;
    private RideHistoryAdapter carpoolHistoryAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabaseRides;
    private FirebaseDatabase databaseCarPool;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_history);

        // /getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseCarPool = FirebaseDatabase.getInstance();
        firebaseDatabaseRides = databaseCarPool.getReference("Rides");

        carpoolListViewHistory = findViewById(R.id.list_view_carpool_history);

        progressDialog = new ProgressDialog(this);


        carpoolListHistory = new ArrayList<>();

        progressDialog.setMessage("Searching please wait...");
        progressDialog.show();

        ride = new Carpool();

        firebaseDatabaseRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
                    ride = rideSnapshot.getValue(Carpool.class);
                    if (firebaseUser.getUid().equals(ride.getUID())) {
                        carpoolListHistory.add(ride);
                    }
                }
                progressDialog.dismiss();
                if (carpoolListHistory.isEmpty()) {
                    Toast.makeText(RidesHistory.this, "No rides were found", Toast.LENGTH_LONG).show();
                } else {
                    carpoolHistoryAdapter = new RideHistoryAdapter(RidesHistory.this, carpoolListHistory);
                    carpoolListViewHistory.setAdapter(carpoolHistoryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Failed to read value.", databaseError.toException());

            }
        });
    }
}
