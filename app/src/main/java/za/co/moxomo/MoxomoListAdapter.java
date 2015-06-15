package za.co.moxomo;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import za.co.moxomo.Vacancy;

/**
 * Created by Paballo Ditshego on 5/14/15.
 */
public class MoxomoListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Vacancy> vacancyItems = new ArrayList<>();
    ;
    protected int selectedPosition;

    static class ViewHolder {
        TextView title;
        TextView description;
        TextView location;
        ImageView thumbNail;
        TextView time;

    }


    public MoxomoListAdapter(Activity activity) {
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return vacancyItems.size();
    }

    @Override
    public Object getItem(int position) {

        return vacancyItems.get(position);
    }

    @Override
    public long getItemId(int position) {

        return vacancyItems.get(position).getId();
    }

    public List<Vacancy> getList() {
        return vacancyItems;
    }

    public Vacancy[] getAllValues() {

        return vacancyItems.toArray(new Vacancy[vacancyItems.size()]);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, null);
            viewHolder = new ViewHolder();
            viewHolder.thumbNail = (ImageView) convertView
                    .findViewById(R.id.thumbnail);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.location = (TextView) convertView.findViewById(R.id.location);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Vacancy v = vacancyItems.get(position);
        if (v != null) {

            Picasso.with(activity).load(v.getImageUrl()).into(viewHolder.thumbNail);
            viewHolder.thumbNail.setTag(v.getAd_id());
            viewHolder.title.setText(v.getJob_title());
            viewHolder.title.setTag(v.getAd_id());
            viewHolder.location.setText(v.getLocation());
            viewHolder.location.setTag(v.getAd_id());
            viewHolder.description.setText(v.getDescription());
            viewHolder.description.setTag(v.getAd_id());
            viewHolder.time.setText(DateUtils.getRelativeDateTimeString(activity,
                            v.getAdvertDate().getMillis(), DateUtils.SECOND_IN_MILLIS,
                            DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL)
            );
            viewHolder.time.setTag(v.getAd_id());

        }
        return convertView;
    }


    public int indexOf(Vacancy item) {
        return vacancyItems.indexOf(item);
    }

    public Vacancy getSelectedItem() {
        if (selectedPosition < 0 || selectedPosition >= getCount()) {
            return null;
        } else {
            return vacancyItems.get(selectedPosition);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public void updateList(List<Vacancy> newList) {
        vacancyItems.clear();
        this.notifyDataSetChanged();
        vacancyItems.addAll(newList);
        this.notifyDataSetChanged();
    }
    public void clearList(){
        vacancyItems.clear();
        notifyDataSetChanged();
    }


    public void addMore(List<Vacancy> newList) {
        int pos = getCount() - 1;
        for (Vacancy vacancy : newList) {
            vacancyItems.add(pos, vacancy);
            pos++;

        }
        this.notifyDataSetChanged();
    }

}
