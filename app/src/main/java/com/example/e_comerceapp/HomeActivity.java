package com.example.e_comerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_comerceapp.Model.Products;
import com.example.e_comerceapp.Prevalent.Prevalent;
import com.example.e_comerceapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity  {

    private FloatingActionButton fab;
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    public DrawerLayout drawerLayout;
    Toolbar toolbar;
    public NavigationView navigationView;
    private String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            type = getIntent().getExtras().get("Admin").toString();
        }


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        drawerLayout = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));//change color of navigation_drawer and three dots color change in theme.xml
        toggle.syncState();

        //Add fragment java code:
//        getSupportFragmentManager().beginTransaction().replace(R.id.frag1,new head_fragment()).commit();

 //      Navigation view
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

//      Navigation header declaration
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profilecircular = headerView.findViewById(R.id.profile_image);

        if (!type.equals("Admin"))
        {
        userNameTextView.setText(Prevalent.currentOnlineUsers.getName());
        Picasso.get().load(Prevalent.currentOnlineUsers.getImage()).placeholder(R.drawable.profile).into(profilecircular);
        }
//        Picasso.get().load(model.getImage()).into(holder.imageView);
//        Picasso.get().load(Prevalent.currentOnlineUsers.getImage()).placeholder(R.drawable.profile).into(profileImageView);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {

                    case R.id.nav_cart:
                        Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_search:
                        Intent intent1=new Intent(HomeActivity.this,SearchProductsActivity.class);
                        startActivity(intent1);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        overridePendingTransition(0,0);
                        break;

                    case R.id.nav_categories:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1,new adminFragment()).commit();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        overridePendingTransition(0,0);
                        break;
                    case R.id.nav_setting:
                        Intent intent2=new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent2);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.nav_logout:
                          Paper.book().destroy();
                        Intent intent3=new Intent(HomeActivity.this,MainActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        finish();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        overridePendingTransition(0,0);
                        break;
                    case R.id.follow_insta:
                        gotourl("https://www.instagram.com/mirza_momin_baig/");//mainline1
                        drawerLayout.closeDrawer(GravityCompat.START);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.folow_facebook:
                        gotourl("https://www.facebook.com/profile.php?id=100007233825639");//mainline1
                        drawerLayout.closeDrawer(GravityCompat.START);
                        overridePendingTransition(0, 0);
                        break;

    //How to move from navigation drawer to activity

//                   case R.id.menusale:
//                        Intent intent6=new Intent(AdminActivity.this,sale.class);
//                        startActivity(intent6);
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        overridePendingTransition(0,0);
//                        break;

    //How to move from navigation drawer to fragment

//                    case R.id.nav_categories:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame1,new adminFragment()).commit();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        overridePendingTransition(0,0);


                }
                return true;
            }
        });


        fab = findViewById(R.id.add_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart()  {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef,Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull Products model) {
                        //we have to display data on text field

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price = " + model.getPrice() + " Rs");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        //we have to access our product item layout
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void gotourl(String s) {

        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.share) {
            Toast.makeText(getApplicationContext(), "you click share", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.about) {
            Toast.makeText(getApplicationContext(), "you click about", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.exit) {
            Toast.makeText(getApplicationContext(), "you click exit", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}