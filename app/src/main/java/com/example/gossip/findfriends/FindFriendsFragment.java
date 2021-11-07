package com.example.gossip.findfriends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gossip.Common.NodeNames;
import com.example.gossip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FindFriendsFragment extends Fragment {

    private RecyclerView rvFindFriends;
    private FindFriendAdapter findFriendAdapter;
    private List<FindFriendModel> findFriendModelList;
    private TextView tvEmptyFriendList;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private View progressBar;

    public FindFriendsFragment() {

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFindFriends = view.findViewById(R.id.rvFindFriends);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyFriendList = view.findViewById(R.id.tvEmptyFriendsList);

        rvFindFriends.setLayoutManager(new LinearLayoutManager(getActivity()));

        findFriendModelList=new ArrayList<>();
        findFriendAdapter=new FindFriendAdapter(getActivity(),findFriendModelList);
        rvFindFriends.setAdapter(findFriendAdapter);

        databaseReference= FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        tvEmptyFriendList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Query query = databaseReference.orderByChild(NodeNames.NAME);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                findFriendModelList.clear();
                for(DataSnapshot ds: datasnapshot.getChildren() ){
                    String userId=ds.getKey();

                    if(userId.equals(currentUser.getUid()))
                        return;

                    if(ds.child(NodeNames.NAME).getValue()!=null){
                        String fullName = ds.child(NodeNames.NAME).getValue().toString();
                        String photoName = ds.child(NodeNames.PHOTO).getValue().toString();

                        findFriendModelList.add(new FindFriendModel(fullName,photoName,userId,false));
                        findFriendAdapter.notifyDataSetChanged();

                        tvEmptyFriendList.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), getString(R.string.failed_to_fetch_friends,databaseerror.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}