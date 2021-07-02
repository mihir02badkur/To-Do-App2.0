package com.example.to_do_app20;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> implements Filterable {

    public static List<GetToDoUserResponse> todoList;
    private List<GetToDoUserResponse> todoListAll;
    private TaskList activity;
    static int editPositionID;
    static int editPosition;
    private Context context;

    public ToDoAdapter(Context context, TaskList activity) {
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_users, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        final GetToDoUserResponse item = todoList.get(position);
        holder.task.setText(item.getTitle());


    }


//    public void update(int position, int status){
//        ToDoModel item = todoList.get(position);
//        item.setStatus(status);
//        todoList.set(position, item);
//    }


    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<GetToDoUserResponse> todoList) {
        ToDoAdapter.todoList = todoList;
        this.todoListAll = new ArrayList<>(todoList);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {

        Call<Void> call = ApiClient.getUserService().deleteTask(TaskList.token, ToDoAdapter.editPositionID);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (context instanceof TaskList) {
                        ((TaskList) context).getAllResponse();


                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }



    public void editItem(int position) {
        GetToDoUserResponse item = todoList.get(position);

        editPositionID = item.getId();
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTitle());
        AddNewTask fragment = new AddNewTask(context);
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }


    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<GetToDoUserResponse> filteredList = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(todoListAll);
            }else{
                for(GetToDoUserResponse task_filter: todoListAll ){
                    if(task_filter.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(task_filter);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            todoList.clear();
            todoList.addAll((Collection<? extends GetToDoUserResponse>) filterResults.values);
            notifyDataSetChanged();


        }
    };
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.task);
        }
    }
}