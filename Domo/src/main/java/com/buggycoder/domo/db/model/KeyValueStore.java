package com.buggycoder.domo.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by shirish on 25/7/13.
 */

@DatabaseTable(tableName = "keyvaluestore")
public class KeyValueStore extends Model implements Parcelable {

    public static class Keys {
//        public static final String PERSTEST_COMPUTE_RESULT = "PERSTEST_COMPUTE_RESULT";
    }

    @DatabaseField(id = true)
    String key;

    @DatabaseField(canBeNull = false)
    String value;

    public KeyValueStore() {

    }

    public KeyValueStore(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    protected KeyValueStore(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }

    public static final Creator<KeyValueStore> CREATOR = new Creator<KeyValueStore>() {
        public KeyValueStore createFromParcel(Parcel in) {
            return new KeyValueStore(in);
        }

        public KeyValueStore[] newArray(int size) {
            return new KeyValueStore[size];
        }
    };

}
