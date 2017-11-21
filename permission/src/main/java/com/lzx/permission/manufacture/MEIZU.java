package com.lzx.permission.manufacture;

/**
 * Created by Lzx on 2017/11/3.
 */

public class MEIZU extends DefaultManufacturer {

    @Override
    public boolean needCheckByChecker() {
        return true;
    }
}
