package com.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.greendao.sql.DaoMaster;
import com.greendao.sql.DaoSession;
import com.greendao.sql.User;
import com.greendao.sql.UserDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText userSex;
    private EditText etDelete;
    private EditText etUpDateId;
    private EditText etUpDateName;
    private EditText etUpDateSex;
    private EditText etSearch;


    private SQLiteDatabase writableDatabase;
    private UserDao userDao;
    private ListView listView;
    private SimpleCursorAdapter simpleCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.userName);
        userSex = (EditText) findViewById(R.id.userSex);
        listView = (ListView) findViewById(R.id.list);
        etDelete = (EditText) findViewById(R.id.et_delete);
        etUpDateId = (EditText) findViewById(R.id.et_update_id);
        etUpDateName = (EditText) findViewById(R.id.et_update_name);
        etUpDateSex = (EditText) findViewById(R.id.et_update_sex);
        etSearch = (EditText) findViewById(R.id.et_search);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.search).setOnClickListener(this);

        init();
    }

    private void init() {
        writableDatabase = new DaoMaster.DevOpenHelper(this, "greendao", null).getWritableDatabase();
        DaoSession daoSession = new DaoMaster(writableDatabase).newSession();

        //得到Dao的对象
        userDao = daoSession.getUserDao();
        // 遍历表中所有的数据

        Cursor cursor = writableDatabase.query(userDao.getTablename(), userDao.getAllColumns(), null, null, null, null, null);
        String[] from = {UserDao.Properties.UserName.columnName, UserDao.Properties.UserSex.columnName};
        int[] id = {android.R.id.text1, android.R.id.text2};
        simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, id, Adapter.NO_SELECTION);
        listView.setAdapter(simpleCursorAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if (!userName.getText().toString().isEmpty() && !userSex.getText().toString().isEmpty()) {
                    addSQLite(userName.getText().toString(), userSex.getText().toString());
                    userName.getText().clear();
                    userSex.getText().clear();
                } else {
                    Toast.makeText(getApplicationContext(), "name sex must not null", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.delete:
                if (!etDelete.getText().toString().isEmpty()) {
                    deleteSQLite(Long.valueOf(etDelete.getText().toString().trim()));
                    etDelete.getText().clear();
                } else {
                    Toast.makeText(getApplicationContext(), "id illegal", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.update:
                if (!etUpDateId.getText().toString().isEmpty() && !etUpDateName.getText().toString().isEmpty() && !etUpDateSex.getText().toString().isEmpty()) {
                    upDateSQLite(Long.parseLong(etUpDateId.getText().toString().trim()), etUpDateName.getText().toString().trim(), etUpDateSex.getText().toString().trim());
                    etUpDateId.getText().clear();
                    etUpDateName.getText().clear();
                    etUpDateSex.getText().clear();
                } else {
                    Toast.makeText(getApplicationContext(), "id name sex must not null", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.search:
                if (!etSearch.getText().toString().isEmpty()) {
                    searchSQLite(Long.parseLong(etSearch.getText().toString().trim()));
                    etSearch.getText().clear();
                } else {
                    Toast.makeText(getApplicationContext(), "id illegal", Toast.LENGTH_LONG).show();
                }
                break;

        }
        Cursor cursor = writableDatabase.query(userDao.getTablename(), userDao.getAllColumns(), null, null, null, null, null);
        simpleCursorAdapter.swapCursor(cursor);
    }

    //add sql data
    private void addSQLite(String name, String sex) {

        User user = new User(null, name, sex);

        userDao.insert(user);
    }


    //delete sql data
    private void deleteSQLite(Long id) {

        userDao.deleteByKey(id);
        //delete sql all;
//        userDao.deleteAll();

    }


    //update sql data
    private void upDateSQLite(long id, String name, String sex) {
        userDao.update(new User(id, name, sex));
    }

    //search sql data
    private void searchSQLite(long id) {
        QueryBuilder<User> queryBuilder = userDao.queryBuilder().where(UserDao.Properties.Id.eq(id));
        // .list() Returns a collection of entity classes
        List<User> user = queryBuilder.list();
        // If you only want results , use .unique() method
        // Person person = queryBuilder.unique();
        new AlertDialog
                .Builder(this)
                .setMessage(user != null && user.size() > 0 ? user.get(0).getUserName() + "--" + user.get(0).getUserSex() : "data null")
                .setPositiveButton("ok", null).create().show();
    }
}
