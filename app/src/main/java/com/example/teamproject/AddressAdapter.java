    package com.example.teamproject;

    import android.annotation.SuppressLint;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.android.libraries.places.api.model.Place;

    import java.util.List;

    public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

        private List<Place> placeList;

        @SuppressLint("NotifyDataSetChanged")
        public void updateAddresses(List<Place> places) {
            Log.d("List Update", "Success");
            this.placeList = places;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("Create ViewHolder", "Success");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
            return new AddressViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
            Place place = placeList.get(position);
            holder.bind(place);
        }

        @Override
        public int getItemCount() {
            return placeList != null ? placeList.size() : 0;
        }

        public static class AddressViewHolder extends RecyclerView.ViewHolder {
            private final TextView addressTextView;

            public AddressViewHolder(@NonNull View itemView) {
                super(itemView);
                Log.d("RecyclerView", "생성자 실행");
                addressTextView = itemView.findViewById(R.id.addressTextView);
            }

            @SuppressLint("SetTextI18n")
            public void bind(Place place) {
                addressTextView.setText(place.getName() + "\n" + place.getAddress());
            }
        }
    }
