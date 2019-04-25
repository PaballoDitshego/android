package za.co.moxomo.adapters;

import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;
import za.co.moxomo.databinding.ProgressRowBinding;

@Getter
public class ProgressViewHolder extends RecyclerView.ViewHolder {
    ProgressRowBinding progressRowBinding;

    ProgressViewHolder(ProgressRowBinding binding, VacancyListAdapter.OnItemClickListener itemClickListener) {
        super(binding.getRoot());
        this.progressRowBinding= binding;

    }
}
