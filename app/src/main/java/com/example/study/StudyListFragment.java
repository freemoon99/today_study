package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudyListFragment extends Fragment {

    private RecyclerView studyRecyclerView;
    private List<DocumentSnapshot> studySnapshotList;
    private StudyListAdapter studyListAdapter;
    private FirebaseFirestore db;

    public StudyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        studyRecyclerView = view.findViewById(R.id.studyRecyclerView);
        studySnapshotList = new ArrayList<>();
        studyListAdapter = new StudyListAdapter(studySnapshotList);
        db = FirebaseFirestore.getInstance();

        studyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        studyRecyclerView.setAdapter(studyListAdapter);

        studyListAdapter.setOnItemClickListener(new StudyListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot studySnapshot) {
                // 아이템 클릭 시 실행되는 로직

                // 클릭된 아이템의 정보를 Bundle에 담기
                Bundle bundle = new Bundle();
                bundle.putString("studyId", studySnapshot.getId());

                // activity_study_detail로 전환하면서 Bundle 전달
                Intent intent = new Intent(requireContext(), StudyDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // Firestore에서 스터디 목록 가져오기
        db.collection("studies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                studySnapshotList.add(document);
                            }
                            studyListAdapter.notifyDataSetChanged();
                        } else {
                            // 실패한 경우 처리
                        }
                    }
                });
    }

    private static class StudyListAdapter extends RecyclerView.Adapter<StudyListAdapter.ViewHolder> {

        private List<DocumentSnapshot> studyList;
        private OnItemClickListener onItemClickListener;

        public StudyListAdapter(List<DocumentSnapshot> studyList) {
            this.studyList = studyList;
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DocumentSnapshot studySnapshot = studyList.get(position);

            // 아이템 내용 설정
            String title = studySnapshot.getString("title");
            String description = studySnapshot.getString("description");

            holder.titleTextView.setText(title);
            holder.descriptionTextView.setText(description);
        }

        @Override
        public int getItemCount() {
            return studyList.size();
        }

        public interface OnItemClickListener {
            void onItemClick(DocumentSnapshot studySnapshot);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView titleTextView;
            public TextView descriptionTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.item_title_textView);
                descriptionTextView = itemView.findViewById(R.id.item_description_textView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        DocumentSnapshot studySnapshot = studyList.get(position);
                        onItemClickListener.onItemClick(studySnapshot);
                    }
                }
            }
        }
    }
}

