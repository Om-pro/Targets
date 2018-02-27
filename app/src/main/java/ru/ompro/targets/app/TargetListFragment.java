package ru.ompro.targets.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.ompro.targets.app.alarm.AlarmManagerBroadcastReceiver;

/**
 * Created by Ompro on 10.01.2017.
 */

public class TargetListFragment extends Fragment implements TextToSpeech.OnInitListener {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private TextView emptyView;
    ItemTouchHelper mItemTouchHelper;

    private Target target;
    final public static String ONE_TIME = "onetime";

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private AlarmManagerBroadcastReceiver alarm;
    NotificationManager nm;

    private TextToSpeech mTTS;

    public TargetListFragment() {
        alarm = new AlarmManagerBroadcastReceiver();

    }

    public void startRepeatingTimer(View view) {
        Context context = this.getContext();
        if (alarm != null) {
            alarm.SetAlarm(context);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTTS = new TextToSpeech(this.getContext(), this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(ru.ompro.targets.app.R.layout.fragment_crime_list, container, false);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(ru.ompro.targets.app.R.id.floatingActionButton3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                target = new Target();
                TargetLab.get(getActivity()).addCrime(target);
                Intent intent = TargetPagerActivity
                        .newIntent(getActivity(), target.getId());
                startActivity(intent);
            }
        });

        mCrimeRecyclerView = (RecyclerView) view.findViewById(ru.ompro.targets.app.R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCrimeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });
        emptyView = (TextView) view.findViewById(ru.ompro.targets.app.R.id.text_none);

        ItemTouchHelper.Callback callback =
                new EditItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);
        mCrimeRecyclerView.setAdapter(mAdapter);
//        emptyView.setText("fafasf");

//        if(mAdapter.getItemCount() == 0) {
//            emptyView.setVisibility(View.VISIBLE);
//            mCrimeRecyclerView.setVisibility(View.GONE);
//        }
//        else {
//            mCrimeRecyclerView.setVisibility(View.VISIBLE);
//            emptyView.setVisibility(View.GONE);
//        }

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(ru.ompro.targets.app.R.menu.fragment_crime_list, menu);

//        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
//        if(mSubtitleVisible) {
//            subtitleItem.setTitle(R.string.hide_subtitle);
//        }
//        else {
//            subtitleItem.setTitle(R.string.show_subtitle);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_item_new_crime:
//                target = new Target();
//                TargetLab.get(getActivity()).addCrime(target);
//                Intent intent = TargetPagerActivity
//                        .newIntent(getActivity(), target.getId());
//                startActivity(intent);
//                return true;
//            case R.id.menu_item_show_subtitle:
//                mSubtitleVisible = !mSubtitleVisible;
//                getActivity().invalidateOptionsMenu();
//                updateSubtitle();
//                return true;
            case ru.ompro.targets.app.R.id.menu_item_delete_crime:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateSubtitle() {
        TargetLab targetLab = TargetLab.get(getActivity());
        int crimeCount = targetLab.getmCrimes().size();
        String subtitle = getResources().getQuantityString(ru.ompro.targets.app.R.plurals.subtitle_plural, crimeCount, crimeCount);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        TargetLab targetLab = TargetLab.get(getActivity());
        List<Target> targets = targetLab.getmCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(targets);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(targets);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    @Override
    public void onInit(int status) {
// TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");

            int result = mTTS.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            } else {
                System.out.println("fasfa");
            }

        } else {
            Log.e("TTS", "Ошибка!");
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Target mTarget;

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(ru.ompro.targets.app.R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(ru.ompro.targets.app.R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(ru.ompro.targets.app.R.id.list_item_crime_solved_check_box);
        }

        @Override
        public void onClick(View v) {
            Intent intent = TargetPagerActivity.newIntent(getActivity(), mTarget.getId());
            startActivity(intent);
        }

        public void bindCrime(Target target) {
            mTarget = target;
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
            Date date = mTarget.getDate();
            mTitleTextView.setText(mTarget.getTitle());
            String formatB = format1.format(date.getTime());
            mDateTextView.setText("Дата ожидания результата: " + "\n" +
                    formatB);
            Date currentDate = new Date();
            String formatA = format1.format(currentDate.getTime());
            if (formatA.equals(formatB)) {
                mTitleTextView.setText(mTarget.getTitle() + " - сегодня дата ожидаемого результата!");
                String text = mTarget.getTitle() + " - сегодня дата ожидаемого результата!";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    return;
                } else {
                    mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    return;

                }


//                MyService myService = new MyService();
//                myService.start();

//                builder.setContentIntent(contentIntent)
//                        // обязательные настройки
//                        .setSmallIcon(R.drawable.ic_launcher_cat)
//                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
//                        .setContentTitle("Напоминание")
//                        //.setContentText(res.getString(R.string.notifytext))
//                        .setContentText("Пора покормить кота") // Текст уведомления
//                        // необязательные настройки
////                        .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.hungrycat)) // большая
//                        // картинка
//                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
//                        .setTicker("Последнее китайское предупреждение!")
//                        .setWhen(System.currentTimeMillis())
//                        .setAutoCancel(true); // автоматически закрыть уведомление после нажатия
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                // Альтернативный вариант
//                // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//                notificationManager.notify(NOTIFY_ID, builder.build());
            }
            mSolvedCheckBox.setChecked(mTarget.isSolved());
        }
    }

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TargetListFragment.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);//Задаем параметр интента
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//Устанавливаем интервал срабатывания в 5 секунд.
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi);
    }

    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Target> mTargets;

        public CrimeAdapter(List<Target> targets) {
            mTargets = targets;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(ru.ompro.targets.app.R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Target target = mTargets.get(position);
            holder.bindCrime(target);
        }

        @Override
        public int getItemCount() {
            if (mTargets.isEmpty()) {
                Toast toast = Toast.makeText(getContext(),
                        "Добавьте Вашу первую цель!", Toast.LENGTH_SHORT);
                toast.show();
            }
            return mTargets.size();
        }

        public void setCrimes(List<Target> targets) {
            mTargets = targets;
        }


        public void onItemDismiss(int position) {
            mTargets.remove(position);
            notifyItemRemoved(position);
        }


        public boolean onItemMove(int fromPosition, int toPosition) {
            //Log.v("", "Log position" + fromPosition + " " + toPosition);
            if (fromPosition < mTargets.size() && toPosition < mTargets.size()) {
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mTargets, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mTargets, i, i - 1);
                    }
                }
                notifyItemMoved(fromPosition, toPosition);
            }
            return true;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}
