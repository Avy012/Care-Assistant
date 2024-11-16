package com.example.teamproject;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    // 주소 리스트를 AddressCodeVO 타입으로 저장
    private List<AddressCodeVO> addresses = new ArrayList<>();

    // 주소 리스트 업데이트 메소드
    @SuppressLint("NotifyDataSetChanged")
    public void updateAddresses(List<AddressCodeVO> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_address 레이아웃을 사용하여 뷰 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        // AddressCodeVO에서 주소를 가져와서 표시
        AddressCodeVO addressCodeVO = addresses.get(position);
        holder.addressTextView.setText(addressCodeVO.getAddress());
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    // ViewHolder 클래스
    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_address 레이아웃에서 주소를 표시할 TextView 초기화
            addressTextView = itemView.findViewById(R.id.addressTextView);
        }
    }
}
