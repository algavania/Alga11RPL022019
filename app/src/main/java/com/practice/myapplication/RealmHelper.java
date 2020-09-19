package com.practice.myapplication;

import android.util.Log;

import com.practice.myapplication.model.ItemProperty;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {

    Realm realm;
    List<ItemProperty> storeList;

    public RealmHelper(Realm realm) {
        this.realm = realm;
    }

    public void save(final ItemProperty model) {
        Log.d("Save", "yes");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realm != null) {
                    Log.e("Created", "Database was created");
                    Number currentIdNum = realm.where(ItemProperty.class).max("id");
                    int nextId;
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                    model.setId(nextId);
                    ItemProperty itemModel = realm.copyToRealm(model);
                    final RealmResults<ItemProperty> item = realm.where(ItemProperty.class).findAll();
                } else {
                    Log.e("Log", "execute: Database not Exist");
                }
            }
        });
    }

    public List<ItemProperty> getAllMovies() {
        RealmResults<ItemProperty> results = realm.where(ItemProperty.class).findAll();
        return results;
    }

    public List delete(ItemProperty itemProperty){
        final RealmResults<ItemProperty> model = realm.where(ItemProperty.class).equalTo("description", itemProperty.getDescription()).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.deleteAllFromRealm();
                final RealmResults<ItemProperty> allItems = realm.where(ItemProperty.class).findAll();
                storeList = realm.copyFromRealm(allItems);;
                Collections.sort(storeList);
            }
        });
        Log.d("Store List", ""+storeList.size());
        return storeList;
    }

}
