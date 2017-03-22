package cn.qiang.zhang.recyclerandswiperefresh;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.QueryObservable;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import cn.qiang.zhang.recyclerandswiperefresh.db.HellGateDBHelper;
import cn.qiang.zhang.recyclerandswiperefresh.db.HellGateDBHelper.AccountTable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnQuery;

    private LoadAccountAdapter adapter;
    private List<AccountTable> accountTables = new ArrayList<>();

    private BriteDatabase db;
    private SwipeRefreshLayout layoutSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        btnQuery = (Button) findViewById(R.id.btn_query);
        layoutSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
//        layoutSwipe.setColorSchemeResources(R.color.colorPrimary);
        layoutSwipe.setDistanceToTriggerSync(300);
        layoutSwipe.setSize(SwipeRefreshLayout.LARGE);
        layoutSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryAccount();
            }
        });

        adapter = new LoadAccountAdapter();
        listView.setAdapter(adapter);

        SqlBrite sqlBrite = new SqlBrite.Builder().build();

        HellGateDBHelper helper = new HellGateDBHelper(this);
        db = sqlBrite.wrapDatabaseHelper(helper, AndroidSchedulers.mainThread());

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSwipe.setRefreshing(true);
                queryAccount();
            }
        });

    }

    private void queryAccount() {
        QueryObservable query = db.createQuery(AccountTable.TABLE_NAME, "SELECT * FROM " + AccountTable.TABLE_NAME);
        query.subscribe(new Action1<SqlBrite.Query>() {
            @Override
            public void call(SqlBrite.Query query) {
                if (accountTables.size() > 0) {
                    accountTables.clear();
                }
                Cursor cursor = query.run();
                if (cursor == null) {
                    return;
                }

                while (cursor.moveToNext()) {
                    AccountTable accountTable = new AccountTable();
                    accountTable.username = cursor.getString(cursor.getColumnIndex(AccountTable.COL_USERNAME));
                    accountTable.password = cursor.getString(cursor.getColumnIndex(AccountTable.COL_PASSWORD));
                    accountTable.exUid = cursor.getLong(cursor.getColumnIndex(AccountTable.COL_EX_UID));
                    accountTables.add(accountTable);
                }
                cursor.close();
                adapter.notifyDataSetChanged();

                layoutSwipe.setRefreshing(false);
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    ContentValues newUser = AccountTable.toContentValues("mrzh" + (int) (Math.random() * 9999),
                                                                         "qiang" + (int) (Math.random() * 9999),
                                                                         (long) (Math.random() * 99999));
                    long insert = db.insert(AccountTable.TABLE_NAME, newUser);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private class LoadAccountAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return accountTables.size();
        }

        @Override
        public Object getItem(int position) {
            return accountTables.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view = convertView;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_account, null);
                holder.tvUsername = (TextView) view.findViewById(R.id.account_username);
                holder.tvPassword = (TextView) view.findViewById(R.id.account_password);
                holder.tvExUid = (TextView) view.findViewById(R.id.account_ex_uid);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            AccountTable accountTable = accountTables.get(position);

            holder.tvUsername.setText(accountTable.username);
            holder.tvPassword.setText(accountTable.password);
            holder.tvExUid.setText(accountTable.exUid + "");

            return view;
        }

        private class ViewHolder {
            TextView tvUsername;
            TextView tvPassword;
            TextView tvExUid;
        }
    }
}
