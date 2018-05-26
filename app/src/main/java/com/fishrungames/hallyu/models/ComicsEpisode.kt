package com.fishrungames.hallyu.models

import android.os.Parcel
import android.os.Parcelable

class ComicsEpisode() : Parcelable {

    var id: Int? = null

    var seriesId: Int? = null

    var title: String? = null

    var description: String? = null

    var position: Int? = null

    var picturesCount: Int? = null

    var images: List<EpisodePicture>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        seriesId = parcel.readValue(Int::class.java.classLoader) as? Int
        title = parcel.readString()
        description = parcel.readString()
        position = parcel.readValue(Int::class.java.classLoader) as? Int
        picturesCount = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(seriesId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeValue(position)
        parcel.writeValue(picturesCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ComicsEpisode> {
        override fun createFromParcel(parcel: Parcel): ComicsEpisode {
            return ComicsEpisode(parcel)
        }

        override fun newArray(size: Int): Array<ComicsEpisode?> {
            return arrayOfNulls(size)
        }
    }

}