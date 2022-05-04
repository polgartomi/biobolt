package com.example.biobolt;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private ArrayList<ShopItem> itemList;
    private ShopItemAdapter itemAdapter;

    private FrameLayout redCircle;
    private TextView contentTextView;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private int gridNumber = 1;
    private int cartItems = 0;
    private boolean viewRow = true;
    private int productsQueryLimit = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        auth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        itemList = new ArrayList<>();

        itemAdapter = new ShopItemAdapter(this, itemList);
        recyclerView.setAdapter(itemAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        this.registerReceiver(batteryReceiver, filter);
    }

    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null)
                return;
            if (action == Intent.ACTION_BATTERY_OKAY || action == Intent.ACTION_SCREEN_ON) {
                productsQueryLimit = 12;
            }
            if (action == Intent.ACTION_BATTERY_LOW || action == Intent.ACTION_SCREEN_OFF) {
                productsQueryLimit = 6;
            }
            queryData();
        }
    };

    private void queryData() {
        itemList.clear();

        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(12).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShopItem item = document.toObject(ShopItem.class);
                item.setId(document.getId());
                itemList.add(item);
            }

            if (itemList.size() == 0) {
                initData();
                queryData();
            }

            itemAdapter.notifyDataSetChanged();
        });
    }

    public void deleteData(ShopItem shopItem) {
        DocumentReference reference = mItems.document(shopItem._getId());
        reference.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Termék törölve: " + shopItem._getId());
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "A termék nem törölhető: " + shopItem._getId(), Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "A termék nem törölhető: " + shopItem._getId());
        });
        queryData();
    }

    private void initData() {
        String[] itemsName = getResources().getStringArray(R.array.productName);
        String[] itemsInfo = getResources().getStringArray(R.array.description);
        String[] itemsPrice = getResources().getStringArray(R.array.price);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.itemImage);

        for(int i = 0; i < itemsName.length; i++) {
            mItems.add((new ShopItem(itemsName[i], itemsInfo[i], itemsPrice[i], itemsImageResource.getResourceId(i, 0), 0)));
        }

        itemsImageResource.recycle();
    }

    private void PopularItems() {
        itemList.clear();
        mItems.whereGreaterThan("cartedCount", 8).orderBy("cartedCount", Query.Direction.DESCENDING).limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShopItem shopItem = document.toObject(ShopItem.class);
                shopItem.setId(document.getId());
                itemList.add(shopItem);
            }
            itemAdapter.notifyDataSetChanged();
        });
    }

    private void NotSoPopularItems() {
        itemList.clear();
        mItems.whereLessThan("cartedCount", 1).orderBy("cartedCount", Query.Direction.DESCENDING).limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShopItem shopItem = document.toObject(ShopItem.class);
                shopItem.setId(document.getId());
                itemList.add(shopItem);
            }
            itemAdapter.notifyDataSetChanged();
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                itemAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                return true;
            case R.id.view_selector:
                if(viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_column, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_stream, 2);
                }
                return true;
            case R.id.mostcarteditems:
                PopularItems();
                return true;
            case R.id.leastcarteditems:
                NotSoPopularItems();
                return true;
            case R.id.setting:
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        manager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShopItem shopItem) {
        cartItems = (cartItems + 1);
        if(0 < cartItems) {
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
        mItems.document(shopItem._getId()).update("cartedCount", shopItem.getCartedCount() + 1).addOnFailureListener(failure -> {
            Toast.makeText(this, "A mezőt nem lehet megváltoztatni.", Toast.LENGTH_LONG).show();
        });

        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }
}