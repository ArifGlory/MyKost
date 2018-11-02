package iqbal.mykost.Kelas;

import android.net.Uri;

/**
 * Created by Glory on 02/11/2018.
 */

public class Galery {
    public Uri uriGambar;

    public Galery(Uri uriGambar) {
        this.uriGambar = uriGambar;
    }

    public Uri getUriGambar() {
        return uriGambar;
    }

    public void setUriGambar(Uri uriGambar) {
        this.uriGambar = uriGambar;
    }
}
