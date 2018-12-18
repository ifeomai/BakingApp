package com.ifeomai.apps.bakingapp.ui.step;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifeomai.apps.bakingapp.R;
import com.ifeomai.apps.bakingapp.data.model.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private ArrayList<Step> steps;
    private Context context;
    private StepListener stepListener;

    StepsAdapter(@NonNull Context context, @NonNull ArrayList<Step> steps, StepListener stepListener) {
        this.steps = steps;
        this.context = context;
        this.stepListener = stepListener;
    }

    interface StepListener {
        void onStepClick(int stepPosition);
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.step_single, viewGroup, false);
        return new StepsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder stepsViewHolder, int position) {
        stepsViewHolder.bind(steps.get(position), position);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_label_text_view)
        TextView stepLabelTextView;

        @BindView(R.id.step_icon_image_view)
        ImageView stepIconImageView;

        StepsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        void bind(Step step, int stepNumber) {
            stepLabelTextView.setText(context.getString(
                    R.string.step_item_label,
                    String.valueOf(stepNumber),
                    step.getShortDescription())
            );

            if (!TextUtils.isEmpty(step.getThumbnailUrl())) {
                Picasso.get()
                        .load(step.getThumbnailUrl())
                        .error(R.drawable.ic_pots)
                        .into(stepIconImageView);
            }
        }

        @Override
        public void onClick(View view) {
            stepListener.onStepClick(getAdapterPosition());
        }
    }
}

