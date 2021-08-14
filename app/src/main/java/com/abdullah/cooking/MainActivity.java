package com.abdullah.cooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    ListView mainListView;
    ProgressBar mainProgressBar;
    TextView mainFailTextView, mainTextView;
    Button addButton;
    FirebaseFirestore db;
    List<Cooking> mainList = new Vector<>();
    CookingAdapter mainArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Clickable(true);
        findingViews();
        Gone();
        Load();

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Clickable(false);
                Intent ToCookingDetailsActivityIntent = new Intent(MainActivity.this, CookingDetailsActivity.class);
                ToCookingDetailsActivityIntent.putExtra("cooking", mainList.get(position));
                startActivity(ToCookingDetailsActivityIntent);
            }
        });
    }

    private boolean Connected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    private void Clickable(boolean b) {
        if(b) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void Gone() {
        mainTextView.setVisibility(View.GONE);
        mainListView.setVisibility(View.GONE);
        mainFailTextView.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
    }

    private void Load() {
        mainProgressBar.setVisibility(View.VISIBLE);
        if(Connected()) {
            db.collection("Cooking List")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                mainFailTextView.setVisibility(View.VISIBLE);
                                mainFailTextView.setText("لا توجد أي وصفات");
                                mainListView.setVisibility(View.GONE);
                                mainList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mainFailTextView.setVisibility(View.GONE);
                                    mainFailTextView.setText("");
                                    mainListView.setVisibility(View.VISIBLE);
                                    mainList.add(new Cooking(document.getId(), document.getData().get("name").toString(),
                                            document.getData().get("quantity").toString() , document.getData().get("production").toString()));
                                }
                                mainArrayAdapter = new CookingAdapter(mainList, getApplicationContext());
                                mainListView.setAdapter(null);
                                mainListView.setAdapter(mainArrayAdapter);
                                mainProgressBar.setVisibility(View.GONE);
                                mainTextView.setVisibility(View.VISIBLE);
                                addButton.setVisibility(View.VISIBLE);
                            } else {
                                mainFailTextView.setVisibility(View.VISIBLE);
                                mainFailTextView.setText("فشل في تحمبل البيانات");
                                mainProgressBar.setVisibility(View.GONE);
                                mainTextView.setVisibility(View.VISIBLE);
                                addButton.setVisibility(View.VISIBLE);
                                mainListView.setVisibility(View.GONE);
                            }
                        }
                    });
        }else {
            mainFailTextView.setVisibility(View.VISIBLE);
            mainFailTextView.setText("يرجى التحقق من الإتصال بالإنترنت");
            mainProgressBar.setVisibility(View.GONE);
            mainTextView.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            mainListView.setVisibility(View.GONE);
        }
    }

    private void findingViews() {
        mainListView = findViewById(R.id.mainListView);
        mainProgressBar = findViewById(R.id.mainProgressBar);
        mainFailTextView = findViewById(R.id.mainFailTextView);
        mainTextView = findViewById(R.id.mainTextView);
        addButton = findViewById(R.id.addButton);
        db = FirebaseFirestore.getInstance();
    }

    public void addButtonClicked(View view) {
        Clickable(false);
        Intent ToAddAndEditActivityIntent = new Intent(MainActivity.this, AddAndEditActivity.class);
        ToAddAndEditActivityIntent.putExtra("state", "Add");
        startActivity(ToAddAndEditActivityIntent);
    }
}
