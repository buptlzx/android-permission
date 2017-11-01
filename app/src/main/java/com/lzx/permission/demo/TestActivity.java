package com.lzx.permission.demo;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.lzx.permission.PermissionsRequest;
import com.lzx.permission.interfaces.IPermissionRequestListener;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void record(View view) {
        PermissionsRequest.with(this)
                .request(new IPermissionRequestListener() {
                    @Override
                    public void onPermissionsGrant(String... permissions) {
                        toast("record grant");
                    }

                    @Override
                    public void onPermissionDenied(String... permissions) {
                        toast("record denied");
                    }
                }, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
    }

    public void contacts(View view) {
        PermissionsRequest.with(this)
                .request(new IPermissionRequestListener() {
                    @Override
                    public void onPermissionsGrant(String... permissions) {
                        toast("call grant");
                    }

                    @Override
                    public void onPermissionDenied(String... permissions) {
                        toast("call denied");
                    }
                }, Manifest.permission.READ_CONTACTS);
    }

    public void location(View view) {
        PermissionsRequest.with(this)
                .request(new IPermissionRequestListener() {
                    @Override
                    public void onPermissionsGrant(String... permissions) {
                        toast("location grant");
                    }

                    @Override
                    public void onPermissionDenied(String... permissions) {
                        toast("location denied");
                    }
                }, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void toast(String info) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
}
