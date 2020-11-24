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

    public void update(final String formerDescription, final String imageUrl, final String title, final String description, final String releaseDate, final String voteAverage){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ItemProperty model = realm.where(ItemProperty.class)
                        .equalTo("description", formerDescription)
                        .findFirst();
                Log.d("title", model.getTitle());
                model.setImageUrl(imageUrl);
                model.setDescription(description);
                model.setTitle(title);
                model.setReleaseDate(releaseDate);
                model.setVoteAverage(voteAverage);
                Log.d("test", "success");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("pppp", "onSuccess: Update Successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
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
