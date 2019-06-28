package za.co.moxomo.v2.adapters;

import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.databinding.ListRowBinding;

@Getter
public class VacancyViewHolder  extends RecyclerView.ViewHolder {

    private ListRowBinding binding;

    VacancyViewHolder(ListRowBinding binding, VacancyListAdapter.OnItemClickListener itemClickListener) {
        super(binding.getRoot());
        this.binding=binding;
        this.binding.getRoot().setOnClickListener(view -> {
            itemClickListener.onItemClick(binding.getVacancy(), view);
        });
        this.binding.getRoot().findViewById(R.id.favourite_btn).setOnClickListener(view -> {
            itemClickListener.onItemClick(binding.getVacancy(), view);
        });
    }
}
