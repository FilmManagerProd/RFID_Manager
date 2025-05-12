package com.FilmManager.RFID.Scanner;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.FilmManager.RFID.Scanner.utils.PcUtils;
import com.gg.reader.api.dal.GClient;
import com.gg.reader.api.protocol.gx.EnumG;
import com.gg.reader.api.protocol.gx.LogBaseEpcInfo;
import com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc;
import com.gg.reader.api.protocol.gx.MsgBaseSetFreqRange;
import com.gg.reader.api.protocol.gx.MsgBaseSetPower;
import com.gg.reader.api.protocol.gx.MsgBaseSetTagLog;
import com.gg.reader.api.protocol.gx.MsgBaseStop;
import com.FilmManager.RFID.Scanner.adapter.TagAdapter;
import com.FilmManager.RFID.Scanner.entity.TagInfo;
import com.FilmManager.RFID.Scanner.utils.GlobalClient;
import com.FilmManager.RFID.Scanner.utils.PowerUtil;
import com.gg.reader.api.protocol.gx.MsgBaseWriteEpc;
import com.gg.reader.api.protocol.gx.ParamEpcFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** @noinspection ALL*/
public class MainActivity extends AppCompatActivity {
    private Button inventory_btn;
    private Button receive_btn;
    private Button dispatch_btn;
    private Button write_btn;
    private RecyclerView listView;
    private TextView tagTotal, tagSpeed, tagReadTime;
    private final GClient client = GlobalClient.getClient();
    private final List<TagInfo> tagList = new ArrayList<>();
    private final LinkedHashMap<String, TagInfo> tagMap = new LinkedHashMap<>();
    private TagAdapter tagAdapter;
    private final boolean showLightTag = false;
    private Timer timer;
    private long time = 0;
    private int speed = 0;
    private View selectedView = null;
    private final OkHttpClient httpClient = new OkHttpClient();
    private String baseUrl = BuildConfig.BASE_URL;
    private String apiKey = BuildConfig.API_KEY;
    private Button languageToggle;
    ProgressBar receiveSpinner, dispatchSpinner;

    private void showToast(final String msg) {
        final Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG);
        toast.show();

        new Handler(Looper.getMainLooper()).postDelayed(toast::cancel, 2000);
    }
    private static class Barcode {
        String id;
        String barcode;
        int totalCount;
    }

    private void setButtonsEnabled(boolean inventory, boolean clean, boolean receive, boolean dispatch, boolean write) {
        inventory_btn.setEnabled(inventory);
        findViewById(R.id.inventory_clean).setEnabled(clean);
        receive_btn.setEnabled(receive);
        dispatch_btn.setEnabled(dispatch);
        write_btn.setEnabled(write);
    }

    private void showButtonSpinner(Button button, ProgressBar spinner) {
        runOnUiThread(() -> {
            button.setEnabled(false);
            spinner.setVisibility(View.VISIBLE);
        });
    }

    private void hideButtonSpinner(Button button, ProgressBar spinner) {
        runOnUiThread(() -> {
            button.setEnabled(true);
            spinner.setVisibility(View.GONE);
        });
    }

    public String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Settings", MODE_PRIVATE);
        return prefs.getString("My_Lang", "zh");
    }

    private void toggleLanguage() {
        String newLang = getSavedLanguage(this).equals("en") ? "zh" : "en";  // Added context
        saveLanguage(newLang);
        recreateApp();
    }

    private void saveLanguage(String langCode) {
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
    }

    private void recreateApp() {
        finish();
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.setLocale(base, getSavedLanguage(base)));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this, getSavedLanguage(this));
        setContentView(R.layout.scan_activity);

        PowerUtil.power("1");

        Button inventory_clean = findViewById(R.id.inventory_clean);
        inventory_btn = findViewById(R.id.inventory_btn);
        receive_btn = findViewById(R.id.receive_btn);
        dispatch_btn = findViewById(R.id.dispatch_btn);
        languageToggle = findViewById(R.id.language_toggle);
        write_btn = findViewById(R.id.write_btn);

        setButtonsEnabled(true, true, false, false, tagMap.size() == 1);

        listView = findViewById(R.id.read_listView);
        tagTotal = findViewById(R.id.tagTotal);
        tagSpeed = findViewById(R.id.tagSpeed);
        tagReadTime = findViewById(R.id.tagReadTime);
        receiveSpinner = findViewById(R.id.receive_spinner);
        dispatchSpinner = findViewById(R.id.dispatch_spinner);

        tagTotal.setText("0");
        tagSpeed.setText("0 t/s");
        tagReadTime.setText("0 ms");

        initAdapter();

        inventory_btn.setOnClickListener(v -> inventoryEvent());
        inventory_clean.setOnClickListener(v -> cleanEvent());
        receive_btn.setOnClickListener(v -> receiveEvent());
        dispatch_btn.setOnClickListener(v -> dispatchEvent());
        languageToggle.setOnClickListener(v -> toggleLanguage());
        write_btn.setOnClickListener(v -> showWriteDialog());

        boolean connected = false;
        try {
            connected = client.openAndroidSerial("/dev/ttyS3:115200", 1000);
        } catch (Exception e) {
            showToast(getString(R.string.error_port, e.getMessage()));
        }
        if (!connected) {
            showToast(getString(R.string.error_rfid_connection));
        } else {
            try {
                MsgBaseSetFreqRange freqMsg = new MsgBaseSetFreqRange();
                freqMsg.setFreqRangeIndex(0);
                client.sendSynMsg(freqMsg);

                MsgBaseSetPower powerMsg = new MsgBaseSetPower();
                Hashtable<Integer, Integer> powerTable = new Hashtable<>();
                powerTable.put(1, 30);
                powerMsg.setDicPower(powerTable);
                client.sendSynMsg(powerMsg);

                MsgBaseSetTagLog tagLog = new MsgBaseSetTagLog();
                tagLog.setRepeatedTime(0);
                tagLog.setRssiTV(0);
                client.sendSynMsg(tagLog);
            } catch (Exception e) {
                showToast(getString(R.string.error_generic, e.getMessage()));
            }
        }
    }

    private void initAdapter() {
        tagAdapter = new TagAdapter(tagList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listView.setLayoutManager(lm);
        listView.setAdapter(tagAdapter);

        tagAdapter.setOnItemClickListener((view, pos) -> {
            if (selectedView != null && view != selectedView) {
                selectedView.setBackgroundColor(0);
            }
            selectedView = view;
            selectedView.setBackgroundColor(0xFF87CEEB);
        });

        tagAdapter.setOnItemLongClickListener((view, pos) -> {
            TagInfo t = tagList.get(pos);
            String epc = TextUtils.isEmpty(t.getEpc()) ? "No EPC" : t.getEpc();
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText(getString(R.string.tag_epc), epc));
            return true;
        });
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    tagList.clear();
                    synchronized (tagMap) { tagList.addAll(tagMap.values()); }
                    tagAdapter.notifyDataSetChanged();
                    tagTotal.setText(String.valueOf(tagMap.size()));
                    setButtonsEnabled(true, true, !tagMap.isEmpty(), !tagMap.isEmpty(), tagMap.size() == 1);
                    break;
                case 2:
                    tagReadTime.setText(time + " ms");
                    break;
                case 3:
                    tagSpeed.setText(msg.arg1 + " t/s");
                    break;
            }
        }
    };

    private void onTagCallback() {
        client.onTagEpcLog = (s, info) -> {
            if (info.getResult() == 0) {
                synchronized (tagMap) { processTag(info); }
            }
        };
    }

    private void processTag(LogBaseEpcInfo info) {
        String key = info.getEpc() + info.getTid();
        if (tagMap.containsKey(key)) {
            tagMap.get(key).setCount(tagMap.get(key).getCount() + 1);
        } else {
            TagInfo ti = new TagInfo();
            ti.setIndex(tagMap.size() + 1);
            ti.setErrorTag(info.getResult() != 0);
            ti.setEpc(info.getEpc());
            ti.setTid(info.getTid());
            ti.setCount(1);
            tagMap.put(key, ti);
        }
        handler.sendEmptyMessage(0);
    }

    private void readTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {
                time += 10;
                handler.sendEmptyMessage(2);
                if (time % 1000 == 0) {
                    synchronized (tagMap) {
                        int sum = tagMap.values().stream().mapToInt(TagInfo::getCount).sum();
                        int rate = sum - speed;
                        speed = sum;
                        handler.sendMessage(handler.obtainMessage(3, rate, 0));
                    }
                }
            }
        }, 0, 10);
    }

    @SuppressLint("SetTextI18n")
    private void inventoryEvent() {
        try {
            if (getString(R.string.scan).equals(inventory_btn.getText().toString())) {
                cleanEvent();
                setButtonsEnabled(true, false, false, false, tagMap.size() == 1);
                onTagCallback();
                client.sendSynMsg(new MsgBaseStop());
                MsgBaseInventoryEpc msg = new MsgBaseInventoryEpc();
                msg.setAntennaEnable(EnumG.AntennaNo_1);
                msg.setInventoryMode(1);
                client.sendUnsynMsg(msg);
                readTimer();
                inventory_btn.setText(R.string.stop);
            } else {
                client.sendSynMsg(new MsgBaseStop());
                if (timer != null) timer.cancel();
                inventory_btn.setText(R.string.scan);
                boolean hasTags = !tagMap.isEmpty();
                setButtonsEnabled(true, true, hasTags, hasTags, tagMap.size() == 1);
            }
        } catch (Exception e) {
            showToast(getString(R.string.error_generic, e.getMessage()));
        }
    }

    private void receiveEvent() {
        setButtonsEnabled(false, false, false, false, tagMap.size() == 1);
        receive_btn.setText("");
        showButtonSpinner(receive_btn, receiveSpinner);

        new Thread(() -> {
            try {
                List<Barcode> barcodes = fetchBarcodes();
                Map<String, Integer> epcCounts = groupTagsByEpc();

                JSONArray updates = new JSONArray();
                JSONArray creates = new JSONArray();

                for (Map.Entry<String, Integer> entry : epcCounts.entrySet()) {
                    String epc = entry.getKey();
                    int count = entry.getValue();
                    Barcode existing = findBarcodeByEpc(barcodes, epc);

                    if (existing != null) {
                        JSONObject update = new JSONObject();
                        update.put("id", existing.id);
                        update.put("operation", "receive");
                        update.put("count", count);
                        updates.put(update);
                    } else {
                        JSONObject create = new JSONObject();
                        create.put("barcode", epc);
                        create.put("count", count);
                        create.put("group", "consumable");
                        create.put("location", "");
                        create.put("itemName", "New Item");
                        create.put("pointsToRedeem", 0);
                        creates.put(create);
                    }
                }

                if (updates.length() > 0) {
                    sendBatchRequest(updates, "PUT");
                }
                if (creates.length() > 0) {
                    sendBatchRequest(creates, "POST");
                }

                runOnUiThread(() -> {
                    showToast(getString(R.string.items_received));
                    hideButtonSpinner(receive_btn, receiveSpinner);
                    receive_btn.setText(R.string.receive);
                    setButtonsEnabled(true, true, true, true, tagMap.size() == 1);
                    cleanEvent();
                });

                runOnUiThread(this::cleanEvent);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showToast(getString(R.string.error_generic, e.getMessage()));
                    hideButtonSpinner(receive_btn, receiveSpinner);
                    receive_btn.setText(R.string.receive);
                    setButtonsEnabled(true, true, true, true, tagMap.size() == 1);
                });
            }
        }).start();
    }

    private void dispatchEvent() {
        setButtonsEnabled(false, false, false, false, tagMap.size() == 1);
        dispatch_btn.setText("");
        showButtonSpinner(dispatch_btn, dispatchSpinner);

        new Thread(() -> {
            try {
                List<Barcode> barcodes = fetchBarcodes();
                Map<String, Integer> epcCounts = groupTagsByEpc();

                JSONArray updates = new JSONArray();
                List<String> errors = new ArrayList<>();

                for (Map.Entry<String, Integer> entry : epcCounts.entrySet()) {
                    String epc = entry.getKey();
                    int count = entry.getValue();
                    Barcode existing = findBarcodeByEpc(barcodes, epc);

                    if (existing == null) {
                        errors.add(epc);
                        continue;
                    }

                    JSONObject update = new JSONObject();
                    update.put("id", existing.id);
                    update.put("operation", "dispatch");
                    update.put("count", count);
                    updates.put(update);
                }

                if (updates.length() > 0) {
                    sendBatchRequest(updates, "PUT");
                }

                if (!errors.isEmpty()) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        LinearLayout headerLayout = new LinearLayout(MainActivity.this);
                        headerLayout.setOrientation(LinearLayout.VERTICAL);
                        headerLayout.setPadding(20, 20, 20, 10);

                        TextView title = new TextView(MainActivity.this);
                        title.setText(R.string.dispatch_success_title);
                        title.setTextSize(20);
                        title.setTypeface(null, Typeface.BOLD);

                        TextView subheader = new TextView(MainActivity.this);
                        subheader.setText(R.string.dispatch_insufficient_stock);
                        subheader.setTextSize(14);
                        subheader.setPadding(0, 10, 0, 0);

                        headerLayout.addView(title);
                        headerLayout.addView(subheader);

                        String message = TextUtils.join("\n", errors);
                        final TextView textView = new TextView(MainActivity.this);
                        textView.setText(message);
                        textView.setPadding(20, 20, 20, 20);
                        textView.setTextIsSelectable(true);

                        ScrollView scrollView = new ScrollView(MainActivity.this);
                        scrollView.addView(textView);

                        LinearLayout parentLayout = new LinearLayout(MainActivity.this);
                        parentLayout.setOrientation(LinearLayout.VERTICAL);
                        parentLayout.addView(headerLayout);
                        parentLayout.addView(scrollView);

                        builder.setView(parentLayout);
                        builder.setPositiveButton(getString(android.R.string.ok), null);
                        builder.show();
                    });
                }

                runOnUiThread(() -> {
                    showToast(getString(R.string.items_dispatched));
                    hideButtonSpinner(dispatch_btn, dispatchSpinner);
                    dispatch_btn.setText(R.string.dispatch);
                    setButtonsEnabled(true, true, true, true, tagMap.size() == 1);
                    cleanEvent();
                });

                runOnUiThread(this::cleanEvent);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showToast(getString(R.string.error_generic, e.getMessage()));
                    hideButtonSpinner(dispatch_btn, dispatchSpinner);
                    dispatch_btn.setText(R.string.dispatch);
                    setButtonsEnabled(true, true, true, true, tagMap.size() == 1);
                });
            }
        }).start();
    }

    private Map<String, Integer> groupTagsByEpc() {
        Map<String, Integer> epcCounts = new HashMap<>();
        synchronized (tagMap) {
            for (TagInfo tag : tagMap.values()) {
                String epc = tag.getEpc();
                epcCounts.put(epc, epcCounts.getOrDefault(epc, 0) + tag.getCount());
            }
        }
        return epcCounts;
    }

    private List<Barcode> fetchBarcodes() throws IOException, JSONException {
        Request req = new Request.Builder()
                .url(baseUrl + "/api/barcodes")
                .header("bypass-tunnel-reminder", "true")
                .header("api_key", apiKey)
                .build();

        try (Response res = httpClient.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                throw new IOException("HTTP " + res.code());
            }
            JSONObject json = new JSONObject(res.body().string());
            JSONArray arr = json.getJSONArray("result");
            List<Barcode> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Barcode b = new Barcode();
                b.id = o.getString("id");
                b.barcode = o.optString("barcode");
                b.totalCount = o.optInt("totalCount", 0);
                list.add(b);
            }
            return list;
        }
    }

    private Barcode findBarcodeByEpc(List<Barcode> bars, String epc) {
        for (Barcode b : bars) if (epc.equals(b.barcode)) return b;
        return null;
    }

    private void sendBatchRequest(JSONArray body, String method) {
        try {
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody rb = RequestBody.create(body.toString(), JSON);
            Request.Builder rbld = new Request.Builder()
                    .url(baseUrl + "/api/barcodes")
                    .header("bypass-tunnel-reminder","true")
                    .header("api_key", apiKey);
            if ("POST".equalsIgnoreCase(method)) rbld.post(rb);
            else rbld.put(rb);
            try (Response r = httpClient.newCall(rbld.build()).execute()) {
                JSONObject resp = new JSONObject(r.body().string());
                if (resp.has("errors")) {
                    JSONArray errs = resp.getJSONArray("errors");
                    for (int i = 0; i < errs.length(); i++) {
                        showToast(getString(R.string.server_error_detail, errs.getJSONObject(i).toString()));
                    }
                } else {
                    showToast(getString(R.string.success));
                }
            } catch (Exception e) {
                Log.d("DEBUGGG", "Error: " + e.getMessage());
                showToast(getString(R.string.error_generic, e.getMessage()));
            }
        } catch (Exception e) {
            Log.d("DEBUGGG", "Error: " + e.getMessage());
            showToast(getString(R.string.error_generic, e.getMessage()));
        }
    }

    private void showWriteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_write, null);

        EditText writeValue = dialogView.findViewById(R.id.write_value);
        TextView warningText = dialogView.findViewById(R.id.warning_text);

        builder.setTitle(R.string.write_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.write, (dialog, which) -> {
                    String value = writeValue.getText().toString();
                    Log.d("WriteDialog", "Input value: " + value);
                    if (validateHex(value)) {
                        Log.d("WriteDialog", "Selected tag type: ISO18000-6C");
                        performWriteOperation(getString(R.string.iso18000_6c), value.toUpperCase());
                    } else {
                        showToast(getString(R.string.invalid_hex));
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateHex(String input) {
        return input.matches("[0-9A-Fa-f]+");
    }

    private void performWriteOperation(String tagType, String hexData) {
        new Thread(() -> {
            try {
                client.sendSynMsg(new MsgBaseStop());

                if (tagList == null || tagList.isEmpty()) {
                    runOnUiThread(() ->
                            showToast(getString(R.string.write_failed, getString(R.string.write_error_no_tags))));
                    return;
                }

                TagInfo targetTag = tagList.get(0);
                int errorCode = write6CTag(hexData);

                runOnUiThread(() -> {
                    if (errorCode == 0) {
                        showToast(getString(R.string.write_success));
                        cleanEvent();
                    } else {
                        String errorMessage = getWriteErrorMessage(errorCode);
                        showToast(getString(R.string.write_failed, errorMessage));
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        showToast(getString(R.string.write_failed, e.getMessage())));
            }
        }).start();
    }

    private int write6CTag(String hexData) throws Exception {
        client.sendSynMsg(new MsgBaseStop());

        String targetEpc = tagList.get(0).getEpc();
        int valueLen = PcUtils.getValueLen(hexData);

        MsgBaseWriteEpc msg = new MsgBaseWriteEpc();
        msg.setAntennaEnable(EnumG.AntennaNo_1);
        msg.setArea(1);
        msg.setStart(1);
        msg.setHexPassword("00000000");

        String pc = PcUtils.getPc(valueLen);
        String dataWithPc = pc + PcUtils.padLeft(hexData, valueLen*4, '0');
        msg.setHexWriteData(dataWithPc);

        ParamEpcFilter filter = new ParamEpcFilter();
        filter.setArea(EnumG.ParamFilterArea_EPC);
        filter.setBitStart(32);
        filter.setBitLength(targetEpc.length() * 4);
        filter.setHexData(targetEpc);
        msg.setFilter(filter);

        client.sendSynMsg(msg);
        return msg.getRtCode();
    }

    private String getWriteErrorMessage(int errorCode) {
        switch (errorCode) {
            case 0:
                return getString(R.string.write_error_success);
            case 1:
                return getString(R.string.write_error_tag_lost);
            case 2:
                return getString(R.string.write_error_memory_locked);
            case 3:
                return getString(R.string.write_error_memory_overrun);
            case 4:
                return getString(R.string.write_error_memory_damaged);
            case 5:
                return getString(R.string.write_error_not_supported);
            case 6:
                return getString(R.string.write_error_password);
            case 7:
                return getString(R.string.write_error_memory_other);
            case 8:
                return getString(R.string.write_error_inventory_failed);
            case 9:
                return getString(R.string.write_error_invalid_parameter);
            case 10:
                return getString(R.string.write_error_power);
            case 11:
                return getString(R.string.write_error_hardware);
            case 12:
                return getString(R.string.write_error_timeout);
            default:
                return getString(R.string.write_error_unknown, errorCode);
        }
    }

    @SuppressLint("SetTextI18n")
    private void cleanEvent() {
        tagMap.clear();
        tagList.clear();
        tagAdapter.notifyDataSetChanged();
        tagTotal.setText("0");
        tagSpeed.setText("0 t/s");
        tagReadTime.setText("0 ms");
        time = speed = 0;
        if (timer != null) timer.cancel();
        if (selectedView != null) {
            selectedView.setBackgroundColor(0);
            selectedView = null;
        }
        setButtonsEnabled(true, true, false, false, tagMap.size() == 1);
    }
}