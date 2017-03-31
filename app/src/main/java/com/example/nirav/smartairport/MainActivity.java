package com.example.nirav.smartairport;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;
    private TextView Uname;

    private Toolbar mToolbar;

    private FragmentTransaction fragmentTransaction;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(getApplicationContext(),BagChkService.class));

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        View header = navigationView.getHeaderView(0);
        Uname = (TextView) header.findViewById(R.id.Uname);

        if (user.getEmail() != null | user.getEmail().length() == 0)
        {
            Uname.setText(user.getEmail());
            Toast.makeText(getApplicationContext(), user.getEmail(), Toast.LENGTH_SHORT).show();
        }

        mToolbar=(Toolbar)findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);

        mDrawerlayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        mToggle=new ActionBarDrawerToggle(this,mDrawerlayout,R.string.open,R.string.close);

        mDrawerlayout.addDrawerListener(mToggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container,new HomeFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Home");

        navigationView=(NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_account:
                        fragmentTransaction =getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new HomeFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Account");
                        item.setChecked(true);
                        mDrawerlayout.closeDrawer(Gravity.LEFT);
                        break;

                    case R.id.nav_logout:
                        mDrawerlayout.closeDrawer(Gravity.LEFT);
                        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Are you sure you want to logout?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        signOut();
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        if (user == null) {
                                            startActivity(new Intent(getApplicationContext(), Main_home.class));
                                            finish();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog=builder.create();
                        alertDialog.setTitle("Logout");
                        alertDialog.show();
                        break;

                    case R.id.nav_barcode:
                        fragmentTransaction =getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container,new ticket_scan());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Ticket Scan");
                        item.setChecked(true);
                        mDrawerlayout.closeDrawer(Gravity.LEFT);
                        
                        break;
                }
                return true;
            }
        });
    }

    public void signOut()
    {
        firebaseAuth.signOut();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
