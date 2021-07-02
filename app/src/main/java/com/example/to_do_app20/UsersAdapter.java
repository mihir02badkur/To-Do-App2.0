package com.example.to_do_app20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserAdapterVH> {

    private List<GetToDoUserResponse> list;
    private Context context;
    private TaskList taskList;

    public UsersAdapter(TaskList taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }
    public void setData(List<GetToDoUserResponse> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public UserAdapterVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.row_users, parent, false);
        return new UserAdapterVH(itemView);

//        context = parent.getContext();
//        return new UsersAdapter.UserAdapterVH(LayoutInflater.from(context).inflate(R.layout.row_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapterVH holder, int position) {
        GetToDoUserResponse userResponse = list.get(position);
        String task = userResponse.getTitle();
        holder.task.setText(task);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserAdapterVH extends RecyclerView.ViewHolder {
        TextView task;

        public UserAdapterVH(@NonNull @NotNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.task);
        }
    }

}
