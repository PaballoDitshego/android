package za.co.moxomo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import za.co.moxomo.R;
import za.co.moxomo.databinding.AlertListRowBinding;
import za.co.moxomo.model.Alert;



public class AlertListAdapter extends PagedListAdapter<Alert, AlertListAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;


    public AlertListAdapter(OnItemClickListener onItemClickListener) {
        super(Alert.DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AlertListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.alert_list_row, parent, false);
        return new ViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
       holder.binding.setAlert(getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AlertListRowBinding binding;
        ViewHolder(AlertListRowBinding binding, OnItemClickListener itemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> {
                itemClickListener.onItemClick(binding.getAlert());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Alert item);
    }

    }
