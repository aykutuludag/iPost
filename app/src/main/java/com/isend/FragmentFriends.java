package com.isend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentFriends extends Fragment {

    private static final int REQUEST_READ_CONTACT = 0;
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        verifyContactPermissions();
        return v;
    }

    private void getContact() {
        //BURADA TÜM LİSTEYİ ÇEKİP DATABESE E YAZDIRACAĞIZ
    }

    public void verifyContactPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permission = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_CONTACT, REQUEST_READ_CONTACT);
            } else {
                getContact();
            }
        } else {
            getContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContact();
                    Toast.makeText(getActivity(), "Ayarlarınız kaydedildi...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Bir hata oluştu! Lütfen daha sonra tekrar deneyiniz...", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
