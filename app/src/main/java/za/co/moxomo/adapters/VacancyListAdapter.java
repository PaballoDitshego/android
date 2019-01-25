package za.co.moxomo.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import za.co.moxomo.R;
import za.co.moxomo.databinding.ListRowBinding;
import za.co.moxomo.helpers.FontCache;
import za.co.moxomo.model.Vacancy;

public class VacancyListAdapter extends PagedListAdapter<Vacancy, VacancyListAdapter.ViewHolder> {


    public VacancyListAdapter() {
        super(Vacancy.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.list_row, parent, false);

        return new ViewHolder(binding);
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

        ViewHolder(ListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    }
