package com.example.to_do_app20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskList extends AppCompatActivity implements DialogCloseListener {
    RecyclerView recyclerView;
    ToDoAdapter toDoAdapter;
    static String token;
    private FloatingActionButton fab;
    public static List<GetToDoUserResponse> todolist;
    static String profile = "";
    DatabaseHandler db;
    Context context = this;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                toDoAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            AlertDialog.Builder builder = new AlertDialog.Builder(toDoAdapter.getContext());
            builder.setTitle("Profile");
            builder.setMessage(profile);


            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                        }
                    });
            builder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db = new DatabaseHandler(toDoAdapter.getContext());
                    db.openDatabase();
                    List<TokenModel> tokenModelList = db.getAllTokens();

                    db.delete();
                    (TaskList.this).finishAffinity();
                    Intent intent = new Intent(toDoAdapter.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        db = new DatabaseHandler(this);
        db.openDatabase();
        List<TokenModel> tokenModelList = db.getAllTokens();

        if (tokenModelList.size() != 0) {

            TokenModel item = tokenModelList.get(0);
            token = "Token " + item.getToken();

        } else if (!MainActivity.keepMeLoggedIn) {
            (TaskList.this).finishAffinity();
            startActivity(new Intent(TaskList.this, MainActivity.class));
            finish();

        }

        if (tokenModelList.size() == 0) {
            if (MainActivity.keepMeLoggedIn) {


                token = "Token " + MainActivity.tokenX;
                MainActivity.keepMeLoggedIn = false;

            }
        }


        recyclerView = (findViewById(R.id.RecyclerView));
        fab = findViewById(R.id.fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        toDoAdapter = new ToDoAdapter(this, TaskList.this);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if (profile.isEmpty()) getProfile();
        getAllResponse();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask task = new AddNewTask(TaskList.this);
                task.show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });


    }

    public void getAllResponse() {
        Call<List<GetToDoUserResponse>> list = ApiClient.getUserService().getAllResponse(token);
        list.enqueue(new Callback<List<GetToDoUserResponse>>() {
            @Override
            public void onResponse(Call<List<GetToDoUserResponse>> call, Response<List<GetToDoUserResponse>> response) {
                if (response.isSuccessful()) {

                    List<GetToDoUserResponse> list = response.body();
                    if (list != null) Collections.reverse(list);
                    todolist = list;
                    toDoAdapter.setTasks(list);
                    recyclerView.setAdapter(toDoAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<GetToDoUserResponse>> call, Throwable t) {
                Toast.makeText(TaskList.this, "Get Task Failed" + t, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getProfile() {
        Call<ProfileResponse> call = ApiClient.getUserService().getProfile(token);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    profile += "Name: " + profileResponse.getName() + "\n";
                    profile += "Username: " + profileResponse.getUsername() + "\n";
                    profile += "E-mail: " + profileResponse.getEmail() + "\n";
                }

            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(TaskList.this, "Get Profile Failed" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {



    }
}