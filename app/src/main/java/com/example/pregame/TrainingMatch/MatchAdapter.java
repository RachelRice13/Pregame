package com.example.pregame.TrainingMatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pregame.CoachHomeActivity;
import com.example.pregame.Model.Match;
import com.example.pregame.PlayerHomeActivity;
import com.example.pregame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ExampleViewHolder> {
    public static final String TAG = "MatchAdapter";
    public static final String Match = "Match";
    private List<Match> matches;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public MatchAdapter (List<Match> matches, Context context) {
        this.matches = matches;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, startTime, month, date, dayOfTheWeek;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_name);
            startTime = itemView.findViewById(R.id.start_time);
            month = itemView.findViewById(R.id.month_tv);
            date = itemView.findViewById(R.id.date_tv);
            dayOfTheWeek = itemView.findViewById(R.id.day_of_the_week_tv);
            cardView = itemView.findViewById(R.id.match_card_view);
        }

        @Override
        public void onClick(View view) {
            int position = this.getLayoutPosition();
            Match match = matches.get(position);

            Intent intent = new Intent(view.getContext(), TrainingMatchFragment.class);
            intent.putExtra(Match, (Serializable) match);
            view.getContext().startActivity(intent);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_row_layout, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Match match = matches.get(position);
        holder.title.setText(match.getMatchTitle());
        holder.startTime.setText("Start Time: " + match.getStartTime());

        // Get the date
        Date date = new Date();
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(match.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.month.setText(new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
        holder.date.setText(new SimpleDateFormat("d", Locale.ENGLISH).format(date));
        holder.dayOfTheWeek.setText(new SimpleDateFormat("EEE", Locale.ENGLISH).format(date));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Match match = matches.get(position);
                viewMatchDetails(match);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public Context getContext() {
        return context;
    }

    public void viewMatchDetails(Match match) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View viewMatchDetailsV = layoutInflater.inflate(R.layout.view_match_details, null);
        AlertDialog.Builder viewMatchDetailsAD = new AlertDialog.Builder(getContext());

        TextView matchTitleTv = viewMatchDetailsV.findViewById(R.id.match_title_tv);
        TextView homeOrAwayTv = viewMatchDetailsV.findViewById(R.id.home_or_away);
        TextView statusTv = viewMatchDetailsV.findViewById(R.id.status_tv);
        TextView scoreTv = viewMatchDetailsV.findViewById(R.id.score_tv);
        TextView dateTv = viewMatchDetailsV.findViewById(R.id.date_tv);
        TextView opponentTv = viewMatchDetailsV.findViewById(R.id.opponent_name_tv);
        TextView startTimeTv = viewMatchDetailsV.findViewById(R.id.start_time_tv);
        TextView meetTimeTv = viewMatchDetailsV.findViewById(R.id.meet_time_tv);
        TextView locationTv = viewMatchDetailsV.findViewById(R.id.location_tv);
        Button goBack = viewMatchDetailsV.findViewById(R.id.go_back_button);

        matchTitleTv.setText(match.getMatchTitle());

        if (match.isHomeOrAway()) {
            homeOrAwayTv.setText("Home");
        } else {
            homeOrAwayTv.setText("Away");
        }
        statusTv.setText("Status: " + match.getStatus());
        String score = match.getTeamScore() + " - " + match.getOpponentScore();
        scoreTv.setText("Score: " + score);
        dateTv.setText("Date: " + match.getDate());
        opponentTv.setText("Opponent: " + match.getOpponentName());
        startTimeTv.setText("Start Time: " + match.getStartTime());
        meetTimeTv.setText("Meet Time: " + match.getMeetTime());
        locationTv.setText("Location: " + match.getLocation());

        viewMatchDetailsAD.setCancelable(false).setView(viewMatchDetailsV);
        AlertDialog alert = viewMatchDetailsAD.create();
        alert.show();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void deleteMatch(int position) {
        Match match = matches.get(position);

        firebaseFirestore.collection("team").whereEqualTo("teamName", PlayerHomeActivity.currentTeam.getTeamName()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        String teamDoc = queryDocumentSnapshot.getId();
                                        firebaseFirestore.collection("team").document(teamDoc).collection("match").document(match.MatchId).delete();
                                        matches.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                }
                            }
                        });
    }

    public void editMatchDetails(int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editMatchDetailsV = layoutInflater.inflate(R.layout.edit_match_details, null);
        AlertDialog.Builder editMatchDetailsAD = new AlertDialog.Builder(getContext());

        Match match = matches.get(position);
        TextView matchTitleTv = editMatchDetailsV.findViewById(R.id.match_title_tv);
        TextView homeOrAwayTv = editMatchDetailsV.findViewById(R.id.home_or_away);
        TextView statusTv = editMatchDetailsV.findViewById(R.id.status_tv);
        TextView scoreTv = editMatchDetailsV.findViewById(R.id.score_tv);
        TextView dateTv = editMatchDetailsV.findViewById(R.id.date_tv);
        TextView opponentTv = editMatchDetailsV.findViewById(R.id.opponent_name_tv);
        TextView startTimeTv = editMatchDetailsV.findViewById(R.id.start_time_tv);
        TextView meetTimeTv = editMatchDetailsV.findViewById(R.id.meet_time_tv);
        TextView locationTv = editMatchDetailsV.findViewById(R.id.location_tv);
        Button goBack = editMatchDetailsV.findViewById(R.id.go_back_button);

        matchTitleTv.setText(match.getMatchTitle());
        if (match.isHomeOrAway()) {
            homeOrAwayTv.setText("Home");
        } else {
            homeOrAwayTv.setText("Away");
        }
        statusTv.setText("Status: " + match.getStatus());
        String score = match.getTeamScore() + " - " + match.getOpponentScore();
        scoreTv.setText("Score: " + score);
        dateTv.setText("Date: " + match.getDate());
        opponentTv.setText("Opponent: " + match.getOpponentName());
        startTimeTv.setText("Start Time: " + match.getStartTime());
        meetTimeTv.setText("Meet Time: " + match.getMeetTime());
        locationTv.setText("Location: " + match.getLocation());

        editMatchDetailsAD.setCancelable(false).setView(editMatchDetailsV);
        AlertDialog alert = editMatchDetailsAD.create();
        alert.show();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(position);
                alert.cancel();
            }
        });

        scoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editScore(match);
                alert.cancel();
            }
        });

        matchTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editString(match, match.getMatchTitle(), R.string.edit_match_title, R.string.match_title, R.drawable.ic_basketball, "matchTitle");
                alert.cancel();
            }
        });

        opponentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editString(match, match.getOpponentName(), R.string.edit_match_opponent, R.string.opponent_name, R.drawable.ic_basketball, "opponentName");
                alert.cancel();
            }
        });

        locationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editString(match, match.getLocation(), R.string.edit_match_location, R.string.location, R.drawable.ic_location, "location");
                alert.cancel();
            }
        });
    }

    public void editScore(Match match) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editMatchScoreV = layoutInflater.inflate(R.layout.edit_match_score, null);
        AlertDialog.Builder editMatchScoreAD = new AlertDialog.Builder(getContext());

        EditText editTeamScoreEt = editMatchScoreV.findViewById(R.id.team_score_et);
        TextInputLayout editTeamScoreLO = editMatchScoreV.findViewById(R.id.team_score);
        EditText editOpponentScoreEt = editMatchScoreV.findViewById(R.id.opponent_score_et);
        TextInputLayout editOpponentScoreLO = editMatchScoreV.findViewById(R.id.opponent_score);
        Button editButton = editMatchScoreV.findViewById(R.id.edit_button);
        Button goBack = editMatchScoreV.findViewById(R.id.cancel_button);

        editTeamScoreEt.setText(String.valueOf(match.getTeamScore()));
        editOpponentScoreEt.setText(String.valueOf(match.getOpponentScore()));

        editMatchScoreAD.setCancelable(false).setView(editMatchScoreV);
        AlertDialog alert = editMatchScoreAD.create();
        alert.show();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamScoreString = editTeamScoreEt.getText().toString();
                String opponentScoreString = editOpponentScoreEt.getText().toString();

                boolean validTeamScore = validateBlank(teamScoreString, editTeamScoreLO);
                boolean validOpponentScore = validateBlank(opponentScoreString, editOpponentScoreLO);

                if (validTeamScore && validOpponentScore) {
                    int teamScore = Integer.parseInt(teamScoreString);
                    int opponentScore = Integer.parseInt(opponentScoreString);
                    int compare = Integer.compare(teamScore, opponentScore);

                    if (compare == 1) {
                        updateFirestoreString(match, "status", "Won");
                    } else if (compare == -1) {
                        updateFirestoreString(match, "status", "Lose");
                    } else {
                        updateFirestoreString(match, "status", "Draw");
                    }

                    updateFirestoreInt(match, "teamScore", teamScore);
                    updateFirestoreInt(match, "opponentScore", opponentScore);

                    notifyDataSetChanged();
                    alert.cancel();
                    Log.e("MatchAdapter", "Team Score: " + teamScore + "\tOpponent Score: " + opponentScore + "\tComparison: " + compare);
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    public void editString(Match match, String data, int title, int hint, int icon, String dataType) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editStringV = layoutInflater.inflate(R.layout.edit_details, null);
        AlertDialog.Builder editStringAD = new AlertDialog.Builder(getContext());

        TextView titleTv = editStringV.findViewById(R.id.edit_detail_title);
        EditText editText = editStringV.findViewById(R.id.edit_text);
        TextInputLayout editTextLO = editStringV.findViewById(R.id.text_input_layout);
        Button editButton = editStringV.findViewById(R.id.edit_button);
        Button goBack = editStringV.findViewById(R.id.cancel_button);

        titleTv.setText(title);
        editText.setText(data);
        editTextLO.setHint(hint);
        editTextLO.setStartIconDrawable(icon);

        editStringAD.setCancelable(false).setView(editStringV);
        AlertDialog alert = editStringAD.create();
        alert.show();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();

                boolean validString = validateBlank(text, editTextLO);

                if (validString) {
                    updateFirestoreString(match, dataType, text);
                    notifyDataSetChanged();
                    alert.cancel();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                alert.cancel();
            }
        });
    }

    public boolean validateBlank(String text, TextInputLayout layout) {
        if (text.isEmpty()) {
            layout.setError("This is Required");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public void updateFirestoreString(Match match, String dataType, String data) {
        firebaseFirestore.collection("team").whereEqualTo("teamName", CoachHomeActivity.currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                firebaseFirestore.collection("team").document(teamDoc).collection("match").document(match.MatchId)
                                        .update(dataType, data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.e(TAG, "Successfully updated " + dataType + " for " + match.getMatchTitle());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Failed to update " + dataType + " for " + match.getMatchTitle() + "\n" + e.getMessage());
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void updateFirestoreInt(Match match, String dataType, int data) {
        firebaseFirestore.collection("team").whereEqualTo("teamName", CoachHomeActivity.currentTeam.getTeamName()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String teamDoc = queryDocumentSnapshot.getId();

                                firebaseFirestore.collection("team").document(teamDoc).collection("match").document(match.MatchId)
                                        .update(dataType, data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.e(TAG, "Successfully updated " + dataType + " for " + match.getMatchTitle());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Failed to update " + dataType + " for " + match.getMatchTitle() + "\n" + e.getMessage());
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}