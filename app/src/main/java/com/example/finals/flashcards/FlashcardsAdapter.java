package com.example.finals.flashcards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finals.R;
import com.example.finals.data.Flashcard;

import java.util.List;

public class FlashcardsAdapter extends ListAdapter<Flashcard, FlashcardsAdapter.VH> {

    interface OnItemInteractionListener {
        void onEdit(Flashcard card);
        void onDelete(Flashcard card);
    }

    private final OnItemInteractionListener listener;

    protected FlashcardsAdapter(@NonNull List<Flashcard> initial, OnItemInteractionListener listener) {
        super(DIFF);
        this.listener = listener;
        submitList(initial);
    }

    static final DiffUtil.ItemCallback<Flashcard> DIFF = new DiffUtil.ItemCallback<Flashcard>() {
        @Override
        public boolean areItemsTheSame(@NonNull Flashcard oldItem, @NonNull Flashcard newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Flashcard oldItem, @NonNull Flashcard newItem) {
            return oldItem.question.equals(newItem.question)
                    && oldItem.answer.equals(newItem.answer)
                    && ((oldItem.categoryId == null && newItem.categoryId == null) || (oldItem.categoryId != null && oldItem.categoryId.equals(newItem.categoryId)))
                    && oldItem.updatedAt == newItem.updatedAt;
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    class VH extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;
        ImageButton btnEdit, btnDelete;
        boolean showingAnswer = false;

        VH(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Flashcard card) {
            tvQuestion.setText(card.question);
            tvAnswer.setText(card.answer);
            tvAnswer.setVisibility(View.GONE);
            showingAnswer = false;

            itemView.setOnClickListener(v -> flip());
            btnEdit.setOnClickListener(v -> listener.onEdit(card));
            btnDelete.setOnClickListener(v -> listener.onDelete(card));
        }

        void flip() {
            View front = tvQuestion;
            View back = tvAnswer;
            float centerX = (front.getWidth()) / 2f;
            front.setCameraDistance(8000);
            back.setCameraDistance(8000);

            if (!showingAnswer) {
                front.animate().rotationY(90).setDuration(150).setInterpolator(new DecelerateInterpolator()).withEndAction(() -> {
                    front.setVisibility(View.GONE);
                    back.setVisibility(View.VISIBLE);
                    back.setRotationY(-90);
                    back.animate().rotationY(0).setDuration(150).setInterpolator(new DecelerateInterpolator()).start();
                }).start();
            } else {
                back.animate().rotationY(-90).setDuration(150).setInterpolator(new DecelerateInterpolator()).withEndAction(() -> {
                    back.setVisibility(View.GONE);
                    front.setVisibility(View.VISIBLE);
                    front.setRotationY(90);
                    front.animate().rotationY(0).setDuration(150).setInterpolator(new DecelerateInterpolator()).start();
                }).start();
            }
            showingAnswer = !showingAnswer;
        }
    }
}
