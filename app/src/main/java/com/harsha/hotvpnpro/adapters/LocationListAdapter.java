package com.harsha.hotvpnpro.adapters;
/*Made By Harsha*/
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.harsha.hotvpnpro.BuildConfig;
import com.harsha.hotvpnpro.Preference;
import com.harsha.hotvpnpro.R;
import com.harsha.hotvpnpro.dialog.CountryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.harsha.hotvpnpro.utils.BillConfig.PRIMIUM_STATE;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    public Context context;
    private Preference preference;
    private List<CountryData> regions;
    private RegionListAdapterInterface listAdapterInterface;

    public LocationListAdapter(RegionListAdapterInterface listAdapterInterface, Activity cntec) {
        this.listAdapterInterface = listAdapterInterface;
        this.context = cntec;
        preference = new Preference(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_list_free, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final CountryData datanew = this.regions.get(holder.getAdapterPosition());
        final Country data = datanew.getCountryvalue();
        Locale locale = new Locale("", data.getCountry());

        if (position == 0) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/earthspeed", null, context.getPackageName()));
            holder.app_name.setText(R.string.best_performance_server);
            holder.limit.setVisibility(View.GONE);
        } else {
            ImageView imageView = holder.flag;
            Resources resources = context.getResources();
            String sb = "drawable/" + data.getCountry().toLowerCase();
            imageView.setImageResource(resources.getIdentifier(sb, null, context.getPackageName()));
            holder.app_name.setText(locale.getDisplayCountry());
            holder.limit.setVisibility(View.VISIBLE);
        }
        if (datanew.isPro()) {
            holder.pro.setVisibility(View.VISIBLE);
        } else {
            holder.pro.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CountryData newData = new CountryData();
            newData.setCountryvalue(list.get(i));
            if (i % 2 == 0) {
                newData.setPro(false);
                regions.add(newData);
            } else {
                if (list.get(i).getServers() > 0) {
                    if (BuildConfig.USE_IN_APP_PURCHASE) {
                        if (preference.isBooleenPreference(PRIMIUM_STATE)) {
                            newData.setPro(false);
                        } else {
                            newData.setPro(true);
                        }
                    } else {
                        newData.setPro(false);
                    }
                    regions.add(newData);
                } else {
                    newData.setPro(false);
                    regions.add(newData);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(CountryData item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView app_name;
        ImageView flag, pro;
        ImageView limit;

        ViewHolder(View v) {
            super(v);
            this.app_name = itemView.findViewById(R.id.region_title);
            this.limit = itemView.findViewById(R.id.region_limit);
            this.flag = itemView.findViewById(R.id.country_flag);
            this.pro = itemView.findViewById(R.id.pro);
        }
    }
}
