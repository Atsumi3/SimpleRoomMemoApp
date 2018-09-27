package jp.bizen.simpleroommemoapp.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * メモ本体のクラス
 * <p>
 * 今回はRoomを使うため、@Entityや@PrimaryKeyといったAnnotationが付与されている
 * ビルドする時に RoomCompilerがよしなにしてくれる
 * </p>
 * <p>
 * Parcelable は クラスを跨いだデータのやり取りに使う
 * 今回の場合は MemoListFragment -> EditMemoDialogFragment で、
 * 編集のためにMemoを渡す必要があったため利用
 * <p>
 * 実際に自分たちで別に何か実装するということはない
 * </p>
 */
@SuppressWarnings("ALL")
@Entity
public final class Memo implements Parcelable {
    // Parcelable
    public static final Creator<Memo> CREATOR = new Creator<Memo>() {
        @Override
        public Memo createFromParcel(Parcel in) {
            return new Memo(in);
        }

        @Override
        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };
    @NonNull
    @PrimaryKey
    private long id;
    private String text;

    public Memo() {
    }

    @Ignore
    protected Memo(Parcel in) {
        id = in.readLong();
        text = in.readString();
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Parcelable
    @SuppressWarnings({"SameReturnValue", "unused"})
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, @SuppressWarnings("unused") int flags) {
        dest.writeLong(id);
        dest.writeString(text);
    }
}
