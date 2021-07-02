package com.example.to_do_app20;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private NewTaskAdded newTaskAdded;
    private AddNewTask context = this;
    private EditRequest editRequest;
    private ToDoAdapter toDoAdapter;
    private TaskList taskList;
    private RecyclerView recyclerView;
    private List<GetToDoUserResponse> todoList;
    private Context mContext;



    public AddNewTask(Context c) {
        mContext = c;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = requireView().findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;

            String task = bundle.getString("task");
            newTaskText.setText(task);

            assert task != null;
            if (task.length() > 0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }


        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                if (finalIsUpdate) {
                    EditRequest editRequest = new EditRequest();
                    editRequest.setTitle(text);
                    editTask(editRequest);

                } else {

                    NewTaskAdded newTaskAdded = new NewTaskAdded();
                    newTaskAdded.setTitle(text);
                    addNewTask(newTaskAdded);


                }


                dismiss();

            }
        });
    }

    private void addNewTask(NewTaskAdded newTaskAdded) {

        Call<ResponseBody> task = ApiClient.getUserService().addNewTask(TaskList.token, newTaskAdded);
        task.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (mContext instanceof TaskList) {
                        ((TaskList) mContext).getAllResponse();


                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void editTask(EditRequest editRequest) {
        Call<EditResponse> task = ApiClient.getUserService().editTask(TaskList.token, ToDoAdapter.editPositionID, editRequest);
        task.enqueue(new Callback<EditResponse>() {
            @Override
            public void onResponse(Call<EditResponse> call, Response<EditResponse> response) {

                if (response.isSuccessful()) {
                    if (mContext instanceof TaskList) {
                        ((TaskList) mContext).getAllResponse();


                    }

                }
            }

            @Override
            public void onFailure(Call<EditResponse> call, Throwable t) {

            }
        });

    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener)
            ((DialogCloseListener) activity).handleDialogClose(dialog);
    }


}
