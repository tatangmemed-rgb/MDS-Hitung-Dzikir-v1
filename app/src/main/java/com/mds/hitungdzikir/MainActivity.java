package com.mds.hitungdzikir;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {

    private static final String PREF = "mds_dzikir";
    private static final int GREEN = Color.rgb(11, 122, 83);

    private SharedPreferences prefs;
    private LinearLayout root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREF, MODE_PRIVATE);

        if (!prefs.getBoolean("intro_done", false)) {
            showIntro();
        } else {
            showHome();
        }
    }

    private TextView text(String value, float size, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(Color.DKGRAY);
        view.setPadding(24, 16, 24, 16);

        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return view;
    }

    private Button button(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setTextSize(16);
        button.setAllCaps(false);
        return button;
    }

    private LinearLayout page() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 18);
        root.setBackgroundColor(Color.WHITE);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);

        setContentView(scroll);

        return root;
    }

    private void showIntro() {
        LinearLayout page = page();

        TextView title = text(
                "MDS Hitung Dzikir",
                30,
                true
        );

        title.setTextColor(GREEN);
        title.setGravity(Gravity.CENTER);
        page.addView(title);

        TextView subtitle = text(
                "Aplikasi sederhana untuk membantu menghitung " +
                "dan menjaga kesinambungan dzikir.",
                18,
                false
        );

        subtitle.setGravity(Gravity.CENTER);
        page.addView(subtitle);

        Space space = new Space(this);
        page.addView(
                space,
                new LinearLayout.LayoutParams(1, 40)
        );

        page.addView(
                text("Nasehat Ulama", 22, true)
        );

        page.addView(
                text(
                        "Dzikir adalah jalan untuk mengingat Allah. " +
                        "Lakukan dengan ikhlas, tenang, dan istiqamah.",
                        17,
                        false
                )
        );

        page.addView(
                text(
                        "“Ingatlah, hanya dengan mengingat Allah " +
                        "hati menjadi tenteram.”\n" +
                        "(QS. Ar-Ra'd: 28)",
                        17,
                        false
                )
        );

        Button start = button(
                "Mulai Menggunakan Aplikasi"
        );

        start.setOnClickListener(v -> {
            prefs.edit()
                    .putBoolean("intro_done", true)
                    .apply();

            showHome();
        });

        page.addView(start);
    }

    private void showHome() {
        LinearLayout page = page();

        TextView title = text(
                "MDS Hitung Dzikir",
                28,
                true
        );

        title.setTextColor(GREEN);
        page.addView(title);

        page.addView(
                text(
                        "Dzikir Anda tersimpan otomatis di perangkat.",
                        15,
                        false
                )
        );

        Button add = button("+ DZIKIR");
        add.setTextSize(19);

        add.setOnClickListener(
                v -> addDzikirDialog()
        );

        page.addView(add);

        Button advice = button("Nasehat Ulama");

        advice.setOnClickListener(
                v -> adviceDialog()
        );

        page.addView(advice);

        page.addView(
                text("DAFTAR DZIKIR", 18, true)
        );

        String ids = prefs.getString("ids", "");

        if (ids.trim().isEmpty()) {
            page.addView(
                    text(
                            "Belum ada dzikir.\n\n" +
                            "Tekan + DZIKIR untuk menambahkan " +
                            "dzikir Anda sendiri.",
                            17,
                            false
                    )
            );

            return;
        }

        for (String id : ids.split(",")) {

            if (id.trim().isEmpty()) {
                continue;
            }

            String name =
                    prefs.getString(
                            "name_" + id,
                            "Dzikir"
                    );

            String latin =
                    prefs.getString(
                            "latin_" + id,
                            ""
                    );

            long count =
                    prefs.getLong(
                            "count_" + id,
                            0
                    );

            long target =
                    prefs.getLong(
                            "target_" + id,
                            0
                    );

            String label =
                    name +
                    "\n" +
                    (latin.isEmpty()
                            ? ""
                            : latin + "\n") +
                    count + " / " + target;

            Button item = button(label);
            item.setTextSize(17);

            item.setOnClickListener(
                    v -> showCounter(id)
            );

            item.setOnLongClickListener(v -> {

                new AlertDialog.Builder(this)
                        .setTitle("Hapus dzikir?")
                        .setMessage(
                                name +
                                " akan dihapus dari daftar."
                        )
                        .setNegativeButton(
                                "Batal",
                                null
                        )
                        .setPositiveButton(
                                "Hapus",
                                (dialog, which) ->
                                        deleteDzikir(id)
                        )
                        .show();

                return true;
            });

            page.addView(item);
        }
    }

    private void adviceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Nasehat Ulama")
                .setMessage(
                        "Perbanyak dzikir dengan hati yang hadir " +
                        "dan istiqamah.\n\n" +
                        "Jangan menjadikan banyaknya hitungan " +
                        "sebagai tujuan utama. Yang utama adalah " +
                        "ingat kepada Allah dan menjaga adab dzikir."
                )
                .setPositiveButton("Tutup", null)
                .show();
    }

    private EditText field(
            String hint,
            int inputType
    ) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setTextSize(17);
        edit.setInputType(inputType);
        edit.setPadding(24, 12, 24, 12);
        return edit;
    }

    private void addDzikirDialog() {

        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setPadding(
                12,
                0,
                12,
                0
        );

        EditText name =
                field(
                        "Nama dzikir",
                        InputType.TYPE_CLASS_TEXT
                );

        EditText arab =
                field(
                        "Teks Arab (boleh kosong)",
                        InputType.TYPE_CLASS_TEXT
                );

        EditText latin =
                field(
                        "Teks Latin (boleh kosong)",
                        InputType.TYPE_CLASS_TEXT
                );

        EditText target =
                field(
                        "Target hitungan",
                        InputType.TYPE_CLASS_NUMBER
                );

        box.addView(name);
        box.addView(arab);
        box.addView(latin);
        box.addView(target);

        new AlertDialog.Builder(this)
                .setTitle("Tambah Dzikir")
                .setView(box)
                .setNegativeButton(
                        "Batal",
                        null
                )
                .setPositiveButton(
                        "Simpan",
                        (dialog, which) -> {

                            String n =
                                    name.getText()
                                            .toString()
                                            .trim();

                            long t;

                            try {
                                t = Long.parseLong(
                                        target.getText()
                                                .toString()
                                                .trim()
                                );
                            } catch (Exception e) {
                                t = 0;
                            }

                            if (n.isEmpty() || t <= 0) {
                                Toast.makeText(
                                        this,
                                        "Nama dan target harus diisi.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            String id =
                                    String.valueOf(
                                            System.currentTimeMillis()
                                    );

                            String old =
                                    prefs.getString(
                                            "ids",
                                            ""
                                    );

                            String all =
                                    old.isEmpty()
                                            ? id
                                            : old + "," + id;

                            prefs.edit()
                                    .putString("ids", all)
                                    .putString(
                                            "name_" + id,
                                            n
                                    )
                                    .putString(
                                            "arab_" + id,
                                            arab.getText()
                                                    .toString()
                                    )
                                    .putString(
                                            "latin_" + id,
                                            latin.getText()
                                                    .toString()
                                    )
                                    .putLong(
                                            "target_" + id,
                                            t
                                    )
                                    .putLong(
                                            "count_" + id,
                                            0
                                    )
                                    .apply();

                            showHome();
                        }
                )
                .show();
    }

    private void showCounter(String id) {

        LinearLayout page = page();

        String name =
                prefs.getString(
                        "name_" + id,
                        "Dzikir"
                );

        String arab =
                prefs.getString(
                        "arab_" + id,
                        ""
                );

        String latin =
                prefs.getString(
                        "latin_" + id,
                        ""
                );

        page.addView(
                text(name, 28, true)
        );

        if (!arab.isEmpty()) {
            TextView arabic =
                    text(arab, 28, false);

            arabic.setGravity(Gravity.CENTER);
            page.addView(arabic);
        }

        if (!latin.isEmpty()) {
            TextView latinView =
                    text(latin, 18, false);

            latinView.setGravity(Gravity.CENTER);
            page.addView(latinView);
        }

        TextView count =
                text("", 42, true);

        count.setGravity(Gravity.CENTER);
        page.addView(count);

        TextView target =
                text("", 17, false);

        target.setGravity(Gravity.CENTER);
        page.addView(target);

        Button plus =
                button("+ 1");

        plus.setTextSize(30);
        page.addView(plus);

        Button reset =
                button("Reset hitungan");

        page.addView(reset);

        Button back =
                button("Kembali");

        page.addView(back);

        Runnable refresh = () -> {

            long c =
                    prefs.getLong(
                            "count_" + id,
                            0
                    );

            long t =
                    prefs.getLong(
                            "target_" + id,
                            0
                    );

            count.setText(
                    String.valueOf(c)
            );

            target.setText(
                    "Target: " +
                    t +
                    (c >= t
                            ? "  ✓ TERCAPAI"
                            : "")
            );
        };

        plus.setOnClickListener(v -> {

            long c =
                    prefs.getLong(
                            "count_" + id,
                            0
                    );

            long t =
                    prefs.getLong(
                            "target_" + id,
                            0
                    );

            if (c < t) {
                c++;
            }

            prefs.edit()
                    .putLong(
                            "count_" + id,
                            c
                    )
                    .apply();

            refresh.run();
        });

        reset.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle(
                            "Reset hitungan?"
                    )
                    .setMessage(
                            "Hitungan akan kembali ke 0."
                    )
                    .setNegativeButton(
                            "Batal",
                            null
                    )
                    .setPositiveButton(
                            "Reset",
                            (dialog, which) -> {

                                prefs.edit()
                                        .putLong(
                                                "count_" + id,
                                                0
                                        )
                                        .apply();

                                refresh.run();
                            }
                    )
                    .show();
        });

        back.setOnClickListener(
                v -> showHome()
        );

        refresh.run();
    }

    private void deleteDzikir(String id) {

        String old =
                prefs.getString(
                        "ids",
                        ""
                );

        ArrayList<String> list =
                new ArrayList<>(
                        Arrays.asList(
                                old.split(",")
                        )
                );

        list.remove(id);

        StringBuilder result =
                new StringBuilder();

        for (String value : list) {

            if (value.isEmpty()) {
                continue;
            }

            if (result.length() > 0) {
                result.append(",");
            }

            result.append(value);
        }

        prefs.edit()
                .putString(
                        "ids",
                        result.toString()
                )
                .remove("name_" + id)
                .remove("arab_" + id)
                .remove("latin_" + id)
                .remove("target_" + id)
                .remove("count_" + id)
                .apply();

        showHome();
    }
}
