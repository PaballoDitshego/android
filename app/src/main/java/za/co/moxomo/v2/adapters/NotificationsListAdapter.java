package za.co.moxomo.v2.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import lombok.Getter;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.databinding.NotificationsListRowBinding;
import za.co.moxomo.v2.model.Notification;

;


/**
 * Created by Paballo Ditshego on 7/31/15.
 */
public class NotificationsListAdapter extends PagedListAdapter<Notification, NotificationsListAdapter.ViewHolder> {

    private NotificationsListAdapter.OnItemClickListener onItemClickListener;


    public NotificationsListAdapter(NotificationsListAdapter.OnItemClickListener onItemClickListener) {
        super(Notification.DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NotificationsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationsListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.notifications_list_row, parent, false);
        return new NotificationsListAdapter.ViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationsListAdapter.ViewHolder holder, int position) {
       holder.binding.setNotification(getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private NotificationsListRowBinding binding;
        ViewHolder(NotificationsListRowBinding binding, NotificationsListAdapter.OnItemClickListener itemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> {
               itemClickListener.onItemClick(binding.getNotification());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Notification item);
    }
}