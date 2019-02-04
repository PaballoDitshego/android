package za.co.moxomo.adapters;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import za.co.moxomo.R;
import za.co.moxomo.databinding.ListRowBinding;
import za.co.moxomo.model.Vacancy;

public class VacancyListAdapter extends PagedListAdapter<Vacancy, VacancyListAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;


    public VacancyListAdapter(OnItemClickListener onItemClickListener) {
        super(Vacancy.DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.list_row, parent, false);
        return new ViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
       holder.binding.setVacancy(getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ListRowBinding binding;
        ViewHolder(ListRowBinding binding, OnItemClickListener itemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> {
                itemClickListener.onItemClick(binding.getVacancy());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Vacancy item);
    }

    }
