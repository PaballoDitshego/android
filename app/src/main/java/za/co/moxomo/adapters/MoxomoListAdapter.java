package za.co.moxomo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import za.co.moxomo.R;
import za.co.moxomo.helpers.FontCache;
import za.co.moxomo.model.Vacancy;

/**
 * Created by Paballo Ditshego on 5/14/15.
 */
public class MoxomoListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Vacancy> vacancyItems = new ArrayList<>();
    private int lastPosition = -1;

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
            Typeface typeface = FontCache.get("Roboto-Thin.ttf", activity);
            viewHolder.title.setText(v.getJob_title());
            viewHolder.title.setTypeface(typeface);

            viewHolder.location.setText(v.getLocation());
            viewHolder.description.setText(v.getDescription());
            Typeface regular = FontCache.get("Roboto-Regular.ttf", activity);
            viewHolder.description.setTypeface(regular);
            viewHolder.time.setText(DateUtils.getRelativeDateTimeString(activity,
                            v.getAdvertDate().getMillis(), DateUtils.SECOND_IN_MILLIS,
                            DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL)
            );
            viewHolder.time.setTypeface(regular);
            Animation animation = AnimationUtils.loadAnimation(activity, (position > lastPosition) ?
                    R.anim.up_from_bottom : R.anim.down_from_top);
            convertView.startAnimation(animation);
            lastPosition = position;

        }
        return convertView;
    }



    public void updateList(List<Vacancy> newList) {
        vacancyItems.clear();
        vacancyItems.addAll(newList);
        notifyDataSetChanged();
    }

    public void clearList() {
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

    static class ViewHolder {
        TextView title;
        TextView description;
        TextView location;
        ImageView thumbNail;
        TextView time;

    }

}
