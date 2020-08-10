package ru.strijar.astromanta.android;

import android.app.Activity;
import android.content.Intent;
import ru.strijar.astro.Chart;

public interface EditChart {
    void to(Intent intent);
    void from(Intent intent);
    void edit(Activity activity,  int code);
    void load(Activity activity,  int code);
    void save(Activity activity,  int code);
    Chart getChart();
}
