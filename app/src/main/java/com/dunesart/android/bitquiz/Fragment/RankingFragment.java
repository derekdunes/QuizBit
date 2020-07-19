package com.dunesart.android.bitquiz.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dunesart.android.bitquiz.Common;
import com.dunesart.android.bitquiz.ItemClickListener;
import com.dunesart.android.bitquiz.Model.QuestionScore;
import com.dunesart.android.bitquiz.Model.Ranking;
import com.dunesart.android.bitquiz.R;
import com.dunesart.android.bitquiz.RankingCallback;
import com.dunesart.android.bitquiz.ScoreDetail;
import com.dunesart.android.bitquiz.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment {

    DatabaseReference questionScore, rankingTbl;
    int sum = 0;
    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseAuth auth;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;
    String Tag = RankingFragment.class.getSimpleName();


    public static RankingFragment rankingFragment() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    public RankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionScore = FirebaseDatabase.getInstance().getReference("Question_Score");

        rankingTbl = FirebaseDatabase.getInstance().getReference("Ranking");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        rankingList = view.findViewById(R.id.rakinglist);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);


        updateScore(Common.currentUser.getUserName(), new RankingCallback<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
                Log.e(Tag, "Ranking Username is " + ranking.getUserName());
                rankingTbl.child(ranking.getUserName())
                        .setValue(ranking);
//                showRanking();

            }
        });

        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.layout_ranking,
                RankingViewHolder.class,
                rankingTbl.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, final Ranking model, int position) {
                viewHolder.txt_name.setText(model.getUserName());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongPressed) {
                        Intent scoreDetail = new Intent(getActivity(), ScoreDetail.class);
                        scoreDetail.putExtra("viewUser", model.getUserName());
                        startActivity(scoreDetail);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);
        return view;
    }

    private void updateScore(final String userName, final RankingCallback<Ranking> callback) {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            QuestionScore ques = data.getValue(QuestionScore.class);
                            sum += Integer.parseInt(ques.getScore());
                        }
                        Ranking ranking = new Ranking(userName, sum);
                        callback.callBack(ranking);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

}
