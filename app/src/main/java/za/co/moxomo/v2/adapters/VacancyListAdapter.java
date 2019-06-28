package za.co.moxomo.v2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.databinding.ListRowBinding;
import za.co.moxomo.v2.databinding.ProgressRowBinding;
import za.co.moxomo.v2.helpers.ApplicationConstants;
import za.co.moxomo.v2.model.Vacancy;

public class VacancyListAdapter extends PagedListAdapter<Vacancy, RecyclerView.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private String progressLoadStatus;

    public VacancyListAdapter(OnItemClickListener onItemClickListener) {
        super(Vacancy.DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
    }

    private boolean isHasExtraRow() {
        return null != progressLoadStatus && !progressLoadStatus.equalsIgnoreCase(ApplicationConstants.LOADED);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.list_row) {
            ListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.list_row, parent, false);
            return new VacancyViewHolder(binding, onItemClickListener);
        } else {
            ProgressRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.progress_row, parent, false);
            return new ProgressViewHolder(binding, onItemClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.list_row:
                VacancyViewHolder vacancyViewHolder = (VacancyViewHolder) holder;
                vacancyViewHolder.getBinding().setVacancy(getItem(position));
                break;
            case R.layout.progress_row:
                ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
                progressViewHolder.getProgressRowBinding()
                        .progressBar.setVisibility((null != progressLoadStatus &&
                        progressLoadStatus.equals(ApplicationConstants.LOADING)
                        ? View.VISIBLE : View.INVISIBLE));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isHasExtraRow() && position == getItemCount() - 1) {
            return R.layout.progress_row;
        } else {
            return R.layout.list_row;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (isHasExtraRow() ? 1 : 0);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setNetworkState(String progressLoadStatus) {
        String previousLoadStatus = this.progressLoadStatus;
        boolean hadExtraRow = isHasExtraRow();
        this.progressLoadStatus = progressLoadStatus;
        boolean hasExtraRow = isHasExtraRow();
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasExtraRow && previousLoadStatus != progressLoadStatus) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Vacancy item, View view);
    }

}
