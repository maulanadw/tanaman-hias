package com.maulanadw.tanamanhias;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterProduct extends Filter {

    private AdapterProductSeller adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProduct(AdapterProductSeller adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // validasi data untuk permintaan pencarian
        if (constraint != null && constraint.length() > 0) {
            // kolom pencarian, cari sesuatu, tampilkan hasil pencarian


            // ubah ke uppercase, untuk membuat case insensitive
            constraint = constraint.toString().toUpperCase();
            // simpan data terfilter
            ArrayList<ModelProduct> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++) {
                // cek, cari berdasarkan nama dan kategori produk
                if (filterList.get(i).getNamaProduk().toUpperCase().contains(constraint) || filterList.get(i).getKategoriProduk().toUpperCase().contains(constraint)) {
                    // tambahkan data yang difilter ke list
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;

        } else {
            // pencarian kosong, kembalikan tampilan list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productList = (ArrayList<ModelProduct>) results.values;
        // refresh adapter
        adapter.notifyDataSetChanged();
    }
}
