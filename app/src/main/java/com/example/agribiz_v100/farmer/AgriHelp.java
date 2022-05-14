package com.example.agribiz_v100.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agribiz_v100.R;
import com.example.agribiz_v100.dialog.RequestAssistanceDialog;
import com.example.agribiz_v100.entities.AssistanceModel;
import com.example.agribiz_v100.services.AssistanceManagement;
import com.example.agribiz_v100.validation.AuthValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AgriHelp extends Fragment {

    TextView no_request;
    Button requestAssistance_btn;
    RequestAssistanceDialog requestAssistanceDialog;

    ListView request_item_list;
    List<AssistanceModel> assistanceItems;
    AssistanceRequestAdapter arAdapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ListenerRegistration registration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agri_help, container, false);
        requestAssistance_btn = view.findViewById(R.id.requestAssistance_btn);
        request_item_list = view.findViewById(R.id.request_item_list);
        no_request = view.findViewById(R.id.no_request);

        requestAssistanceDialog = new RequestAssistanceDialog(getActivity(), this);
        requestAssistanceDialog.buildDialog();

        assistanceItems = new ArrayList<>();
        arAdapter = new AssistanceRequestAdapter(getContext(), assistanceItems);
        request_item_list.setAdapter(arAdapter);
        request_item_list.setEmptyView(no_request);

        requestAssistance_btn.setOnClickListener(view1 -> {
            requestAssistanceDialog.showDialog();
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Removes listener being tracked
        registration.remove();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Display assistance lists
        displayAssistanceList();
    }

    private void displayAssistanceList() {
        // Registration callback listens an event source
        // and receives event updates/notification
        registration = AssistanceManagement.getRequestAssistance(user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                assistanceItems.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Log.d("data", user.getUid() + " inside for each");
                    AssistanceModel am = doc.toObject(AssistanceModel.class);
                    assistanceItems.add(am);
                    arAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public class AssistanceRequestAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        List<AssistanceModel> assistanceItems;

        String title = "";
        String message = "";

        public AssistanceRequestAdapter(Context context, List<AssistanceModel> assistanceItems) {
            this.context = context;
            this.assistanceItems = assistanceItems;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return assistanceItems.size();
        }

        @Override
        public AssistanceModel getItem(int i) {
            return assistanceItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            }

            if (view == null) {
                view = inflater.inflate(R.layout.assistance_request_item_list, null);
            }

            Chip assistanceStatus_chip = view.findViewById(R.id.assistanceStatus_chip);
            TextView request_type_tv = view.findViewById(R.id.request_type_tv);
            TextView request_amountEquipment_tv = view.findViewById(R.id.request_amountEquipment_tv);
            TextView request_desc_tv = view.findViewById(R.id.request_desc_tv);
            TextView repayDate_tv = view.findViewById(R.id.repayDate_tv);

            ImageButton delete_request_ib = view.findViewById(R.id.delete_request_ib); // stop here

            String getStatus = assistanceItems.get(i).getAssistanceStatus();
            String getAssistantType = "Type: " + assistanceItems.get(i).getAssistanceType();
            String getAmountEquipment = "Amount or Equipment: " + assistanceItems.get(i).getAssistanceAmountEquipment();
            String getDesc = "Purpose: " + assistanceItems.get(i).getAssistanceDescription();
            String getRepayDate = "";

            if (assistanceItems.get(i).getAssistanceType().equalsIgnoreCase("Borrow Money")) {
                getRepayDate = "Repay Date: " + assistanceItems.get(i).getAssistanceRepayDate();
            }
            else if (assistanceItems.get(i).getAssistanceType().equalsIgnoreCase("Borrow Farm Equipment")) {
                getRepayDate = "Return Date: " + assistanceItems.get(i).getAssistanceRepayDate();
            }
            else {
                getRepayDate = "Date: None";
            }

            assistanceStatus_chip.setText(getStatus);
            request_type_tv.setText(getAssistantType);
            request_amountEquipment_tv.setText(getAmountEquipment);
            request_desc_tv.setText(getDesc);
            repayDate_tv.setText(getRepayDate);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            if (getStatus.equalsIgnoreCase("Pending")) {
                delete_request_ib.setImageResource(R.drawable.ic_round_do_not_disturb_24);
                delete_request_ib.setVisibility(View.VISIBLE);

                title = "Cancel Request";
                message = "Do you want to cancel this request? Once you cancel your request, it cannot be undone.";

                builder.setTitle(title);
                builder.setMessage(message);
            }
            else if (getStatus.equalsIgnoreCase("Approved")) {
                delete_request_ib.setVisibility(View.GONE);
            }
            else {
                delete_request_ib.setImageResource(R.drawable.ic_baseline_delete_forever_24);
                delete_request_ib.setVisibility(View.VISIBLE);

                title = "Delete Request";
                message = "Do you want to delete this request?";

                builder.setTitle(title);
                builder.setMessage(message);
            }

            delete_request_ib.setOnClickListener(v -> {

                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int s) {
                        Log.d("data", "I'm here outside cancel request.");
                        AssistanceManagement.cancelRequest(assistanceItems.get(i).getAssistanceID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AuthValidation.successToast(context, "Cancelled request").show();
                                    if (i != -1 && i < assistanceItems.size())
                                    {
                                        assistanceItems.remove(i);
                                        notifyDataSetChanged();
                                    }
                                }
                                else {
                                    AuthValidation.failedToast(context, task.getException().getMessage()).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("data","Dismissed");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                positiveButton.setTextColor(getResources().getColor(R.color.army_green));
                negativeButton.setTextColor(getResources().getColor(R.color.red));
            });

            return view;
        }
    }
}