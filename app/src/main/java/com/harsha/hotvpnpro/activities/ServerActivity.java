package com.harsha.hotvpnpro.activities;
/*Made By Harsha*/


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.harsha.hotvpnpro.R;
import com.harsha.hotvpnpro.adapters.LocationListAdapter;
import com.harsha.hotvpnpro.dialog.CountryData;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.harsha.hotvpnpro.utils.BillConfig.BUNDLE;
import static com.harsha.hotvpnpro.utils.BillConfig.COUNTRY_DATA;

public class ServerActivity extends AppCompatActivity {

    @BindView(R.id.regions_recycler_view)
    RecyclerView regionsRecyclerView;

    @BindView(R.id.regions_progress)
    ProgressBar regionsProgressBar;

    private LocationListAdapter regionAdapter;
    private RegionChooserInterface regionChooserInterface;
    ImageView backToActivity;
    TextView activity_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        ButterKnife.bind(this);

        activity_name = (TextView) findViewById(R.id.activity_name);
        backToActivity = (ImageView) findViewById(R.id.finish_activity);
        activity_name.setText("Servers");
        backToActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        regionChooserInterface = new RegionChooserInterface() {
            @Override
            public void onRegionSelected(CountryData item) {
                if (!item.isPro()) {
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    Gson gson = new Gson();
                    String json = gson.toJson(item);

                    args.putString(COUNTRY_DATA, json);
                    intent.putExtra(BUNDLE, args);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent(ServerActivity.this, GetPremiumActivity.class);
                    startActivity(intent);
                }
            }
        };

        regionsRecyclerView.setHasFixedSize(true);
        regionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        regionAdapter = new LocationListAdapter(new LocationListAdapter.RegionListAdapterInterface() {
            @Override
            public void onCountrySelected(CountryData item) {
                regionChooserInterface.onRegionSelected(item);
            }
        }, ServerActivity.this);
        regionsRecyclerView.setAdapter(regionAdapter);
        loadServers();
    }

    private void loadServers() {
        showProgress();
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                hideProress();
                regionAdapter.setRegions(countries.getCountries());
            }

            @Override
            public void failure(@NonNull VpnException e) {
                hideProress();
            }
        });
    }

    private void showProgress() {
        regionsProgressBar.setVisibility(View.VISIBLE);
        regionsRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProress() {
        regionsProgressBar.setVisibility(View.GONE);
        regionsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(CountryData item);
    }
}
